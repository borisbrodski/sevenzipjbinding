import { AstroLogger, type AstroLoggerDestination } from './core.js';
import type { LoggerHandlerConfig } from './config.js';
import type { AstroConfig, AstroInlineConfig } from '../../types/public/index.js';
/**
 * Loads a logger destination in a Node context, i.e. outside of a built server bundle.
 * Inside the bundle, the destination comes from the `virtual:astro:logger` module instead.
 */
export declare function loadLoggerDestination(config: LoggerHandlerConfig, 
/** The project root, which relative entrypoints are resolved against */
root: URL): Promise<AstroLoggerDestination>;
/**
 * It attempts to load a logger from the entrypoint.
 * If not provided, it creates a new logger instance on the fly.
 * @param astroConfig
 * @param inlineAstroConfig
 */
export declare function loadOrCreateNodeLogger(astroConfig: AstroConfig, inlineAstroConfig: AstroInlineConfig): Promise<AstroLogger>;
