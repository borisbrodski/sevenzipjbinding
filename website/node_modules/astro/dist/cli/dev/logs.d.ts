import type { AstroLogger } from '../../core/logger/core.js';
import type { Flags } from '../flags.js';
export declare function logs({ flags, logger, }: {
    flags: Flags;
    logger: AstroLogger;
}): Promise<void>;
