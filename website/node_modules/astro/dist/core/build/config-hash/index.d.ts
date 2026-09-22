import type { AstroConfig } from '../../../types/public/config.js';
export { getConfigHashInput } from './input.js';
/**
 * Produce a sha256 over the output-affecting subset of the resolved config (see
 * {@link getConfigHashInput}). A mismatch invalidates the whole incremental
 * build cache.
 */
export declare function computeConfigHash(config: AstroConfig): Promise<string>;
