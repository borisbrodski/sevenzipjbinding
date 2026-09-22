import type { AstroUserConfig } from '../types/public/config.js';
interface EventPayload {
    cliCommand: string;
    config?: ConfigInfo;
    configKeys?: string[];
    flags?: string[];
    optionalIntegrations?: number;
    /** Whether the CLI session was run by an AI coding agent. */
    isAgentic?: boolean;
    /** ID of the detected agent, e.g. "cursor-agent", "claude-code". */
    agentId?: string;
    /** Display name of the detected agent, e.g. "Cursor Agent", "Claude Code". */
    agentName?: string;
    /** Type of agentic environment: "agent", "interactive", or "hybrid". */
    agentType?: string;
}
type ConfigInfoValue = string | boolean | string[] | undefined;
type ConfigInfoRecord = Record<string, ConfigInfoValue>;
type ConfigInfoBase = {
    [alias in keyof AstroUserConfig]: ConfigInfoValue | ConfigInfoRecord;
};
interface ConfigInfo extends ConfigInfoBase {
    build: ConfigInfoRecord;
    image: ConfigInfoRecord;
    markdown: ConfigInfoRecord;
    experimental: ConfigInfoRecord;
    legacy: ConfigInfoRecord;
    vite: ConfigInfoRecord | undefined;
}
export declare function eventCliSession(cliCommand: string, userConfig: AstroUserConfig, flags?: Record<string, any>): {
    eventName: string;
    payload: EventPayload;
}[];
export {};
