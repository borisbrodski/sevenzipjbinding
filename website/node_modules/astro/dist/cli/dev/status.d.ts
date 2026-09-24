import type { AstroLogger } from '../../core/logger/core.js';
import type { Flags } from '../flags.js';
import { formatStatusOutput, type StatusResult } from '../server.js';
export { formatStatusOutput, type StatusResult };
export declare function status({ flags, logger, }: {
    flags: Flags;
    logger: AstroLogger;
}): Promise<void>;
