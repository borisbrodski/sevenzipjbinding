import type { AstroLogger } from '../../core/logger/core.js';
import type { Flags } from '../flags.js';
import { formatStopOutput, type StopResult } from '../server.js';
export { formatStopOutput, type StopResult };
export declare function stop({ flags, logger, }: {
    flags: Flags;
    logger: AstroLogger;
}): Promise<void>;
