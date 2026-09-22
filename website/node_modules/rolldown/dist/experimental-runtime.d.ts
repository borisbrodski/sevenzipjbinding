/**
 * Compiler-emitted module-graph delta — pure topology (static + dynamic edges).
 * `ids[0, localCount)` are the modules this payload carries; `ids[localCount, …)` are foreign edge targets.
 * `edges[i]` / `dynamicEdges[i]` are the static / dynamic-`import()` out-edges of `ids[i]`.
 * @typedef {{ ids: string[], localCount: number, edges: number[][], dynamicEdges?: number[][] }} ModuleGraphDelta
 * @typedef {{ createModuleHotContext(moduleId: string): any, onModuleCacheRemoval(moduleId: string): void }} DevRuntimeHooks
 */
export class MissingFactoryError {
    /**
     * @param {string} id
     */
    constructor(id: string);
    id: string;
}
export class DevRuntime {
    /**
     * @param {string} clientId
     */
    constructor(clientId: string);
    /**
     * Client ID generated at runtime initialization, used for lazy compilation requests.
     * @type {string}
     */
    clientId: string;
    /**
     * Static import edges from `registerGraph` — entries persist across `removeModuleCache`
     * and change only by replacement from a newer payload (last write wins).
     * @type {Map<string, { edges: string[] }>}
     */
    staticImports: Map<string, {
        edges: string[];
    }>;
    /**
     * Reverse index over the static imports.
     * @type {Map<string, Set<string>>}
     */
    importers: Map<string, Set<string>>;
    /**
     * Dynamic `import()` edges from `registerGraph`, keyed by importer — mirror of
     * `staticImports` for the dynamic reverse index.
     * @type {Map<string, { edges: string[] }>}
     */
    dynamicImports: Map<string, {
        edges: string[];
    }>;
    /**
     * Reverse index over the dynamic imports.
     * @type {Map<string, Set<string>>}
     */
    dynamicImporters: Map<string, Set<string>>;
    /**
     * The module cache. Membership means "this module's side effects ran in this tab" —
     * registration is emitted ahead of every module body, and nothing un-registers on
     * unwind, so a factory that throws mid-body stays registered. A `Map` rather than a
     * plain object: HMR eviction deletes entries, and a `delete` on an object drops V8
     * into dictionary mode, taxing every later lookup on the hottest read path.
     * @type {Map<string, Module>}
     */
    moduleCache: Map<string, Module>;
    /**
     * Re-runnable factories from HMR patches and lazy chunks. The initial bundle stays
     * scope-hoisted and contributes none.
     * @type {Map<string, { kind: 'esm' | 'cjs', fn: (id: string) => void }>}
     */
    factories: Map<string, {
        kind: "esm" | "cjs";
        fn: (id: string) => void;
    }>;
    /**
     * Installed by the dev client at boot. The runtime is a store + executor and makes
     * no HMR decisions; accepting, disposing, and reloading live behind these hooks.
     * @type {DevRuntimeHooks | null}
     */
    hooks: DevRuntimeHooks | null;
    /**
     * @param {ModuleGraphDelta} delta
     */
    registerGraph(delta: ModuleGraphDelta): void;
    /**
     * @param {string} id
     * @param {'esm' | 'cjs'} kind
     * @param {(id: string) => void} fn
     */
    registerFactory(id: string, kind: "esm" | "cjs", fn: (id: string) => void): void;
    /**
     * @param {string} id
     * @param {{ exports: any }} [exportsHolder]
     */
    registerModule(id: string, exportsHolder?: {
        exports: any;
    }): void;
    /**
     * @param {string} id
     * @returns {string[]}
     */
    getImporters(id: string): string[];
    /**
     * @param {string} id
     */
    isExecuted(id: string): any;
    /**
     * @param {string} id
     */
    hasFactory(id: string): any;
    /**
     * Module-cache delete only — static imports and factories persist. Removal is what
     * re-arms a cache-gated factory for `initModule`.
     * @param {string} id
     */
    removeModuleCache(id: string): void;
    /**
     * The one re-execution gate: registered → return the live exports; otherwise run the
     * mapped factory (which registers itself first, then runs the body).
     * @param {string} id
     */
    initModule(id: string): any;
    /**
     * @param {string} id
     */
    loadExports(id: string): any;
    /** @type {Map<string, Promise<any>>} */
    lazyRequests: Map<string, Promise<any>>;
    /**
     * The entry point for a lazy `import()`. `id` is the module the boundary stands for; the
     * boundary's own id appears only inside `fetchChunk`'s URL.
     *
     * Nothing is registered under the boundary id, deliberately. A cache entry with no factory
     * behind it reads as "executed" to the HMR boundary walk, and `applyUpdate` turns an
     * updated-but-factory-less module into a full page reload.
     *
     * A rejection is memoized like any other outcome, matching `import()` of a module that
     * threw. Retrying could not work anyway: a factory registers its module before running its
     * body, so re-running `initModule` would return half-initialized exports as success.
     *
     * @param {string} id
     * @param {() => Promise<unknown>} fetchChunk
     * @returns {Promise<any>}
     */
    requestLazy(id: string, fetchChunk: () => Promise<unknown>): Promise<any>;
    /**
     * @param {string} moduleId
     */
    createModuleHotContext(moduleId: string): any;
    /** @internal */
    __toESM: any;
    /** @internal */
    __toCommonJS: any;
    /** @internal */
    __exportAll: any;
    /**
     * @param {boolean} [isNodeMode]
     * @returns {(mod: any) => any}
     * @internal
     */
    __toDynamicImportESM: (isNodeMode?: boolean) => (mod: any) => any;
    /** @internal */
    __reExport: any;
}
/**
 * Compiler-emitted module-graph delta — pure topology (static + dynamic edges).
 * `ids[0, localCount)` are the modules this payload carries; `ids[localCount, …)` are foreign edge targets.
 * `edges[i]` / `dynamicEdges[i]` are the static / dynamic-`import()` out-edges of `ids[i]`.
 */
export type ModuleGraphDelta = {
    ids: string[];
    localCount: number;
    edges: number[][];
    dynamicEdges?: number[][];
};
/**
 * Compiler-emitted module-graph delta — pure topology (static + dynamic edges).
 * `ids[0, localCount)` are the modules this payload carries; `ids[localCount, …)` are foreign edge targets.
 * `edges[i]` / `dynamicEdges[i]` are the static / dynamic-`import()` out-edges of `ids[i]`.
 */
export type DevRuntimeHooks = {
    createModuleHotContext(moduleId: string): any;
    onModuleCacheRemoval(moduleId: string): void;
};
declare class Module {
    /**
     * @param {string} id
     */
    constructor(id: string);
    /**
     * @type {{ exports: any }}
     */
    exportsHolder: {
        exports: any;
    };
    /**
     * @type {string}
     */
    id: string;
    get exports(): any;
}
export {};
