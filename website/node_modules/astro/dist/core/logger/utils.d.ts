import type { LoggerHandlerConfig } from './config.js';
export declare const COMPOSE_LOGGER_ENTRYPOINT = "astro/logger/compose";
export interface NormalizedLoggerConfig {
    /** An absolute file path or a package specifier */
    entrypoint: string;
    /** Serializable options passed to the handler factory */
    config?: Record<string, any> | undefined;
    /** The composed handlers. Only set for `astro/logger/compose` */
    loggers?: NormalizedLoggerConfig[];
}
/**
 * Normalizes a user-provided logger config by turning its `entrypoint` into a string that
 * can be resolved as-is, either by Vite (see `vitePluginLogger`) or by Node
 * (see `loadLoggerDestination`), and by applying the same treatment to the handlers
 * composed through `astro/logger/compose`.
 *
 * Concretely, `URL` and relative entrypoints become absolute file paths — mirroring what
 * session drivers do, since both Vite and `import()` can handle those — while any other
 * string entrypoint, e.g. a package specifier, is left untouched.
 */
export declare function normalizeLoggerConfig(logger: LoggerHandlerConfig, 
/** The project root, which relative entrypoints are resolved against */
root: URL): NormalizedLoggerConfig;
