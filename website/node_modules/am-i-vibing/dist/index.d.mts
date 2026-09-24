//#region src/types.d.ts
/**
 * The type of AI coding environment detected
 */
type AgenticType = "agent" | "interactive" | "hybrid";
/**
 * Environment variable definition - either just a name or a name/value tuple
 */
type EnvVarDefinition = string | [string, string];
/**
 * Environment variable group with logical operators
 */
interface EnvVarGroup {
  /** ANY of these environment variables can match (OR logic) */
  any?: EnvVarDefinition[];
  /** ALL of these environment variables must match (AND logic) */
  all?: EnvVarDefinition[];
  /** NONE of these environment variables should be present (NOT logic) */
  none?: EnvVarDefinition[];
}
/**
 * Configuration for detecting a specific AI coding provider
 */
interface ProviderConfig {
  /** Unique identifier for the provider */
  id: string;
  /** Human-readable name of the provider */
  name: string;
  /** Type of AI coding environment */
  type: AgenticType;
  /** Environment variables */
  envVars?: Array<EnvVarGroup | EnvVarDefinition>;
  /** Process names to check for in the process tree (only used when checkProcesses is enabled) */
  processChecks?: string[];
  /** Custom detection functions for complex logic */
  customDetectors?: (() => boolean)[];
}
/**
 * Result of agentic environment detection
 */
interface DetectionResult {
  /** Whether an agentic environment was detected */
  isAgentic: boolean;
  /** ID of the detected provider, if any */
  id: string | null;
  /** Name of the detected provider, if any */
  name: string | null;
  /** Type of agentic environment, if detected */
  type: AgenticType | null;
}
/**
 * Options for `detectAgenticEnvironment` and related helpers.
 */
interface DetectOptions {
  /**
   * Environment variables to inspect. Defaults to `process.env`.
   */
  env?: Record<string, string | undefined>;
  /**
   * Pre-computed process ancestry (from `process-ancestry` or compatible). When
   * provided, this is used in place of fetching ancestry at detection time.
   * Implies `checkProcesses: true` unless explicitly set to `false`.
   */
  processAncestry?: Array<{
    command?: string;
  }>;
  /**
   * Whether to fall back to process-ancestry checks when no environment-variable
   * match is found. Defaults to `false` because fetching the process tree is
   * expensive on some platforms (notably Windows).
   *
   * Set to `true` to enable detection of providers that only expose a
   * processChecks signal (e.g. Octofriend), at the cost of spawning a
   * subprocess to read the process tree.
   */
  checkProcesses?: boolean;
}
//#endregion
//#region src/providers.d.ts
/**
 * Provider configurations for major AI coding tools
 */
declare const providers: ProviderConfig[];
/**
 * Get provider configuration by name
 */
declare function getProvider(name: string): ProviderConfig | undefined;
/**
 * Get all providers of a specific type
 */
declare function getProvidersByType(type: "agent" | "interactive" | "hybrid"): ProviderConfig[];
//#endregion
//#region src/detector.d.ts
/**
 * Detect agentic coding environment
 */
declare function detectAgenticEnvironment(options?: DetectOptions): DetectionResult;
/**
 * @deprecated Pass an options object instead. This signature is retained for
 * backwards compatibility and will be removed in a future major release.
 */
declare function detectAgenticEnvironment(env: Record<string, string | undefined>, processAncestry?: Array<{
  command?: string;
}>): DetectionResult;
/**
 * Check if currently running in a specific provider
 */
declare function isProvider(providerName: string, options?: DetectOptions): boolean;
/**
 * @deprecated Pass an options object instead.
 */
declare function isProvider(providerName: string, env: Record<string, string | undefined>, processAncestry?: Array<{
  command?: string;
}>): boolean;
/**
 * Check if currently running in any agent environment
 */
declare function isAgent(options?: DetectOptions): boolean;
/**
 * @deprecated Pass an options object instead.
 */
declare function isAgent(env: Record<string, string | undefined>, processAncestry?: Array<{
  command?: string;
}>): boolean;
/**
 * Check if currently running in any interactive AI environment
 */
declare function isInteractive(options?: DetectOptions): boolean;
/**
 * @deprecated Pass an options object instead.
 */
declare function isInteractive(env: Record<string, string | undefined>, processAncestry?: Array<{
  command?: string;
}>): boolean;
/**
 * Check if currently running in any hybrid AI environment
 */
declare function isHybrid(options?: DetectOptions): boolean;
/**
 * @deprecated Pass an options object instead.
 */
declare function isHybrid(env: Record<string, string | undefined>, processAncestry?: Array<{
  command?: string;
}>): boolean;
//#endregion
export { type AgenticType, type DetectOptions, type DetectionResult, type EnvVarDefinition, type EnvVarGroup, type ProviderConfig, detectAgenticEnvironment as default, detectAgenticEnvironment, getProvider, getProvidersByType, isAgent, isHybrid, isInteractive, isProvider, providers };
//# sourceMappingURL=index.d.mts.map