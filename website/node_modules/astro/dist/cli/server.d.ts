import { type LockFileData, type ServerCommand } from '../core/dev/lockfile.js';
import type { AstroLogger } from '../core/logger/core.js';
import type { Flags } from './flags.js';
export interface BackgroundCommandConfig {
    command: ServerCommand;
    displayName: string;
    envVar: 'ASTRO_DEV_BACKGROUND' | 'ASTRO_PREVIEW_BACKGROUND';
}
export declare const devServerCommand: BackgroundCommandConfig;
export declare const previewServerCommand: BackgroundCommandConfig;
export interface BackgroundResult {
    pid: number;
    url: string;
    existing?: boolean;
}
export interface BackgroundErrorResult {
    error: string;
    message: string;
}
export interface StopResult {
    stopped: boolean;
    pid?: number;
    reason?: string;
}
export interface StatusResult {
    running: boolean;
    pid?: number;
    url?: string;
    port?: number;
    background?: boolean;
    uptime?: number;
}
export declare function formatBackgroundOutput(result: BackgroundResult | BackgroundErrorResult): string;
export declare function formatStopOutput(result: StopResult): string;
export declare function formatStatusOutput(result: StatusResult): string;
export declare function formatServerRunningMessage(data: LockFileData, config: BackgroundCommandConfig, { existing }?: {
    existing?: boolean;
}): string;
export declare function buildBackgroundArgs(command: ServerCommand, flags: Flags): string[];
export declare function stopExistingServer(root: URL, command: ServerCommand, pid: number): Promise<void>;
export declare function background({ flags, logger, config, }: {
    flags: Flags;
    logger: AstroLogger;
    config: BackgroundCommandConfig;
}): Promise<void>;
export declare function stop({ flags, logger, config, }: {
    flags: Flags;
    logger: AstroLogger;
    config: BackgroundCommandConfig;
}): Promise<void>;
export declare function status({ flags, logger, config, }: {
    flags: Flags;
    logger: AstroLogger;
    config: BackgroundCommandConfig;
}): Promise<void>;
export declare function logs({ flags, logger, config, }: {
    flags: Flags;
    logger: AstroLogger;
    config: BackgroundCommandConfig;
}): Promise<void>;
