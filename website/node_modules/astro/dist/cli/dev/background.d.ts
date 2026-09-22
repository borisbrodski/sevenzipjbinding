import type { AstroLogger } from '../../core/logger/core.js';
import type { Flags } from '../flags.js';
import type { LockFileData } from '../../core/dev/lockfile.js';
import { formatBackgroundOutput, type BackgroundErrorResult, type BackgroundResult } from '../server.js';
export { formatBackgroundOutput, type BackgroundErrorResult, type BackgroundResult };
export declare function formatServerRunningMessage(data: LockFileData, { existing }?: {
    existing?: boolean;
}): string;
export declare function background({ flags, logger, }: {
    flags: Flags;
    logger: AstroLogger;
}): Promise<void>;
