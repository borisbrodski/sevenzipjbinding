import type { AstroSettings } from '../../types/astro.js';
import type { RenderEnvironment } from '../environment/index.js';
import type { BuildInternals } from './internal.js';
import type { StaticBuildOptions } from './types.js';
/**
 * The build / prerender environment record and its mutable closure slots.
 * The build has a two-phase initialization: the prerender bundle is imported
 * first, and `createDefaultPrerenderer.setup()` injects `BuildInternals` /
 * `StaticBuildOptions` afterwards through the facade. The slots live in this
 * closure; accessors throw before injection, and the environment functions
 * close over the same slots.
 */
export interface BuildEnvironmentSlots {
    /** The build `RenderEnvironment`; its functions close over the slots below. */
    env: RenderEnvironment;
    setInternals(internals: BuildInternals): void;
    setOptions(options: StaticBuildOptions): void;
    /** Throws `No internals defined` before injection. */
    getInternals(): BuildInternals;
    /** Throws `No options defined` before injection. */
    getOptions(): StaticBuildOptions;
    /** Throws `No options defined` before injection. */
    getSettings(): AstroSettings;
}
export declare function createBuildEnvironment(): BuildEnvironmentSlots;
