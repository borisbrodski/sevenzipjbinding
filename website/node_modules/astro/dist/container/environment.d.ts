import type { RouteData, SSRLoadedRenderer, SSRResult } from '../types/public/internal.js';
import type { SinglePageBuiltModule } from '../core/build/types.js';
import type { RenderEnvironment } from '../core/environment/index.js';
export interface ContainerEnvironmentOptions {
    /**
     * The route → module interner. Created by `experimental_AstroContainer`'s
     * constructor and shared with its `insertRoute` writes; this record owns
     * the lookups.
     */
    interner: WeakMap<RouteData, SinglePageBuiltModule>;
    resolve: SSRResult['resolve'];
    renderers: SSRLoadedRenderer[];
    streaming: boolean;
}
/**
 * The container environment. Registered by `experimental_AstroContainer`'s
 * constructor on its fabricated manifest — the container never touches the
 * ambient manifest, so multiple containers in one process stay isolated.
 */
export declare function createContainerEnvironment({ interner, resolve, renderers, streaming, }: ContainerEnvironmentOptions): RenderEnvironment;
