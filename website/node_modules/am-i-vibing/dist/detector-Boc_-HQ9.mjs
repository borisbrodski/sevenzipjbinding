import { getProcessAncestry } from "process-ancestry";
//#region src/providers.ts
/**
* Provider configurations for major AI coding tools
*/
const providers = [
	{
		id: "opencode",
		name: "OpenCode",
		type: "agent",
		envVars: [{ any: [
			"OPENCODE",
			"OPENCODE_BIN_PATH",
			"OPENCODE_SERVER",
			"OPENCODE_APP_INFO",
			"OPENCODE_MODES"
		] }]
	},
	{
		id: "jules",
		name: "Jules",
		type: "agent",
		envVars: [{ all: [["HOME", "/home/jules"], ["USER", "swebot"]] }]
	},
	{
		id: "claude-code",
		name: "Claude Code",
		type: "agent",
		envVars: ["CLAUDECODE"]
	},
	{
		id: "cursor-agent",
		name: "Cursor Agent",
		type: "agent",
		envVars: [{ all: ["CURSOR_TRACE_ID", ["PAGER", "head -n 10000 | cat"]] }]
	},
	{
		id: "cursor",
		name: "Cursor",
		type: "interactive",
		envVars: ["CURSOR_TRACE_ID"]
	},
	{
		id: "antigravity",
		name: "Antigravity",
		type: "agent",
		envVars: [{ any: ["ANTIGRAVITY_AGENT", "ANTIGRAVITY_PROJECT_ID"] }]
	},
	{
		id: "gemini-agent",
		name: "Gemini CLI",
		type: "agent",
		envVars: [["GEMINI_CLI", "1"]],
		processChecks: ["gemini"]
	},
	{
		id: "codex",
		name: "OpenAI Codex",
		type: "agent",
		envVars: ["CODEX_THREAD_ID"],
		processChecks: ["codex"]
	},
	{
		id: "replit",
		name: "Replit",
		type: "agent",
		envVars: ["REPL_ID"]
	},
	{
		id: "aider",
		name: "Aider",
		type: "agent",
		envVars: ["AIDER_API_KEY"],
		processChecks: ["aider"]
	},
	{
		id: "bolt-agent",
		name: "Bolt.new Agent",
		type: "agent",
		envVars: [{ all: [["SHELL", "/bin/jsh"], "npm_config_yes"] }]
	},
	{
		id: "bolt",
		name: "Bolt.new",
		type: "interactive",
		envVars: [{
			all: [["SHELL", "/bin/jsh"]],
			none: ["npm_config_yes"]
		}]
	},
	{
		id: "zed-agent",
		name: "Zed Agent",
		type: "agent",
		envVars: [{ all: [["TERM_PROGRAM", "zed"], ["PAGER", "cat"]] }]
	},
	{
		id: "zed",
		name: "Zed",
		type: "interactive",
		envVars: [{
			all: [["TERM_PROGRAM", "zed"]],
			none: [["PAGER", "cat"]]
		}]
	},
	{
		id: "replit-assistant",
		name: "Replit Assistant",
		type: "agent",
		envVars: [{ all: ["REPL_ID", ["REPLIT_MODE", "assistant"]] }]
	},
	{
		id: "replit",
		name: "Replit",
		type: "interactive",
		envVars: [{
			all: ["REPL_ID"],
			none: [["REPLIT_MODE", "assistant"]]
		}]
	},
	{
		id: "windsurf",
		name: "Windsurf",
		type: "agent",
		envVars: ["CODEIUM_EDITOR_APP_ROOT"]
	},
	{
		id: "crush",
		name: "Crush",
		type: "agent",
		envVars: [{ any: [
			["CRUSH", "1"],
			["AGENT", "crush"],
			["AI_AGENT", "crush"]
		] }],
		processChecks: ["crush"]
	},
	{
		id: "amp",
		name: "Amp",
		type: "agent",
		envVars: [{ any: ["AMP_CURRENT_THREAD_ID", ["AGENT", "amp"]] }]
	},
	{
		id: "auggie",
		name: "Auggie",
		type: "agent",
		envVars: [["AUGMENT_AGENT", "1"]]
	},
	{
		id: "qwen-code",
		name: "Qwen Code",
		type: "agent",
		envVars: [["QWEN_CODE", "1"]]
	},
	{
		id: "vscode-copilot-agent",
		name: "GitHub Copilot in VS Code",
		type: "agent",
		envVars: [{ all: [["TERM_PROGRAM", "vscode"], ["GIT_PAGER", "cat"]] }]
	},
	{
		id: "warp",
		name: "Warp Terminal",
		type: "hybrid",
		envVars: [{ all: [["TERM_PROGRAM", "WarpTerminal"]] }]
	},
	{
		id: "octofriend",
		name: "Octofriend",
		type: "agent",
		processChecks: ["octofriend"]
	},
	{
		id: "devin",
		name: "Devin",
		type: "agent",
		processChecks: ["devin"]
	},
	{
		id: "droid",
		name: "Factory Droid",
		type: "agent",
		processChecks: ["droid"]
	}
];
/**
* Get provider configuration by name
*/
function getProvider(name) {
	return providers.find((p) => p.name === name);
}
/**
* Get all providers of a specific type
*/
function getProvidersByType(type) {
	return providers.filter((p) => p.type === type);
}
//#endregion
//#region src/detector.ts
/**
* Check if a specific environment variable exists (handles both strings and tuples)
*/
function checkEnvVar(envVarDef, env = process.env) {
	const [envVar, expectedValue] = typeof envVarDef === "string" ? [envVarDef, void 0] : envVarDef;
	const actualValue = env[envVar];
	return Boolean(actualValue && (!expectedValue || actualValue === expectedValue));
}
/**
* Check if a process is running in the process tree
*/
function checkProcess(processName, processAncestry) {
	for (const ancestorProcess of processAncestry) if (ancestorProcess.command?.includes(processName)) return true;
	return false;
}
/**
* Check if an environment variable group matches based on its properties
*/
function checkEnvVars(definition, env = process.env) {
	if (typeof definition === "string" || Array.isArray(definition)) return checkEnvVar(definition, env);
	const { any, all, none } = definition;
	const anyResult = !any?.length || any.some((envVar) => checkEnvVar(envVar, env));
	const allResult = !all?.length || all.every((envVar) => checkEnvVar(envVar, env));
	const noneResult = !none?.length || !none.some((envVar) => checkEnvVar(envVar, env));
	return anyResult && allResult && noneResult;
}
/**
* Run custom detectors for a provider
*/
function runCustomDetectors(provider) {
	return provider.customDetectors?.some((detector) => {
		try {
			return detector();
		} catch {
			return false;
		}
	}) ?? false;
}
/**
* Create a positive detection result
*/
function createDetectedResult(provider) {
	return {
		isAgentic: true,
		id: provider.id,
		name: provider.name,
		type: provider.type
	};
}
/**
* Normalize the various supported argument shapes into a DetectOptions object.
*
* Supported shapes:
*   - detectAgenticEnvironment()
*   - detectAgenticEnvironment(options)
*   - detectAgenticEnvironment(env)                       // legacy
*   - detectAgenticEnvironment(env, processAncestry)      // legacy
*/
function normalizeOptions(envOrOptions, legacyAncestry) {
	if (envOrOptions != null && typeof envOrOptions === "object" && ("env" in envOrOptions || "processAncestry" in envOrOptions || "checkProcesses" in envOrOptions)) {
		const opts = envOrOptions;
		return {
			env: opts.env ?? process.env,
			processAncestry: opts.processAncestry,
			checkProcesses: opts.checkProcesses ?? opts.processAncestry !== void 0
		};
	}
	return {
		env: envOrOptions ?? process.env,
		processAncestry: legacyAncestry,
		checkProcesses: legacyAncestry !== void 0
	};
}
function detectAgenticEnvironment(envOrOptions, legacyAncestry) {
	const { env, processAncestry, checkProcesses } = normalizeOptions(envOrOptions, legacyAncestry);
	for (const provider of providers) if (provider.envVars?.some((group) => checkEnvVars(group, env))) return createDetectedResult(provider);
	for (const provider of providers) if (runCustomDetectors(provider)) return createDetectedResult(provider);
	if (checkProcesses) {
		let cachedAncestry = processAncestry;
		const getAncestry = () => {
			if (cachedAncestry === void 0) try {
				cachedAncestry = getProcessAncestry();
			} catch {
				cachedAncestry = [];
			}
			return cachedAncestry;
		};
		for (const provider of providers) if (provider.processChecks?.some((processName) => checkProcess(processName, getAncestry()))) return createDetectedResult(provider);
	}
	return {
		isAgentic: false,
		id: null,
		name: null,
		type: null
	};
}
function isProvider(providerName, envOrOptions, legacyAncestry) {
	return detectAgenticEnvironment(envOrOptions, legacyAncestry).name === providerName;
}
function isAgent(envOrOptions, legacyAncestry) {
	const result = detectAgenticEnvironment(envOrOptions, legacyAncestry);
	return result.type === "agent" || result.type === "hybrid";
}
function isInteractive(envOrOptions, legacyAncestry) {
	const result = detectAgenticEnvironment(envOrOptions, legacyAncestry);
	return result.type === "interactive" || result.type === "hybrid";
}
function isHybrid(envOrOptions, legacyAncestry) {
	return detectAgenticEnvironment(envOrOptions, legacyAncestry).type === "hybrid";
}
//#endregion
export { isProvider as a, providers as c, isInteractive as i, isAgent as n, getProvider as o, isHybrid as r, getProvidersByType as s, detectAgenticEnvironment as t };

//# sourceMappingURL=detector-Boc_-HQ9.mjs.map