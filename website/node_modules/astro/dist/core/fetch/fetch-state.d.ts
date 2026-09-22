import type { ActionAPIContext } from '../../actions/runtime/types.js';
import type { ComponentInstance } from '../../types/astro.js';
import type { Params, Props, RewritePayload } from '../../types/public/common.js';
import type { APIContext, AstroGlobal } from '../../types/public/context.js';
import type { RouteData, SSRResult } from '../../types/public/internal.js';
import { AstroCookies } from '../cookies/index.js';
import type { RenderErrorOptions, ResolvedRenderOptions } from '../app/base.js';
import type { SSRManifest } from '../app/types.js';
import { type RequestLogPayload } from '../environment/index.js';
import { type AstroLogger } from '../logger/core.js';
/**
 * Per-render facade inputs passed by `BaseApp.render`'s fast path to the
 * internal `FetchState` constructor and stored as plain state fields.
 *
 * MEMBERSHIP CLAMP: frozen at `{ streaming?, renderError?, logRequest? }`.
 * Any addition must be facade-INSTANCE behavior — something a public
 * overridable/reassignable method dispatches — never static or per-manifest
 * data. This type is never exported from a public entrypoint;
 * only `BaseApp.render` constructs it; only the `FetchState` constructor
 * consumes it; it is never stored on a request, in a registry, or passed to
 * any other function.
 */
export interface FacadeHooks {
    /** Overrides the environment's `defaultStreaming` for this render. */
    streaming?: boolean;
    /**
     * Late-bound `app.renderError` dispatch for deep chain-internal error
     * reroutes (preserves cloudflare's instance-property reassignment).
     */
    renderError?: (request: Request, options: RenderErrorOptions) => Promise<Response>;
    /** Late-bound `app.logThisRequest` dispatch (dev request lines). */
    logRequest?: (payload: RequestLogPayload) => void;
}
/**
 * The public contract of {@link FetchState} exposed to user-land code
 * (custom fetch handlers, Hono middleware, etc.).
 *
 * Only the members listed here are part of the stable public API.
 * Everything else on the concrete `FetchState` class is internal and
 * may change without notice.
 *
 * If you add a new member to `FetchState` that should be user-visible,
 * add it here first — the `implements` clause on the class ensures a
 * compile-time error if the class falls out of sync.
 */
export interface AstroFetchState {
    /** The incoming request. */
    readonly request: Request;
    /** Normalized URL derived from the request. */
    readonly url: URL;
    /** Base-stripped, decoded pathname of the request. */
    readonly pathname: string;
    /** The matched route for this request, if any. */
    readonly routeData: RouteData | undefined;
    /** Cookies for this request. */
    readonly cookies: AstroCookies;
    /** Request-scoped locals object, shared with user middleware. */
    readonly locals: App.Locals;
    /** Route params derived from routeData + pathname. */
    readonly params: Params | undefined;
    /** The `Response` produced by handlers, if any. Set after rendering. */
    response: Response | undefined;
    /** Default HTTP status for the rendered response. */
    status: number;
    /**
     * Triggers a rewrite to a different route.
     *
     * [Astro reference](https://docs.astro.build/en/guides/routing/#rewrites)
     */
    rewrite(payload: RewritePayload): Promise<Response>;
}
/**
 * Retrieves the `FetchState` stashed on an `APIContext` by
 * `FetchState.getAPIContext()`. Throws if not found — this indicates
 * the context was not created through Astro's request pipeline.
 */
export declare function getFetchStateFromAPIContext(context: APIContext): FetchState;
/**
 * Holds per-request state as it flows through the handler pipeline.
 *
 * **This class is user-facing** via `astro/fetch` and `astro/hono`.
 * The {@link AstroFetchState} interface defines the stable public
 * surface. Members not on that interface are internal and
 * may change without notice.
 *
 * Performance note: fields on this class are plain properties — ES
 * private fields (`#foo`) have a non-zero per-access cost in V8
 * which is measurable on the hot render path, so `#` is used only
 * for rarely-accessed memoized caches and Maps.
 */
export declare class FetchState implements AstroFetchState {
    #private;
    /** The manifest — the single ambient source of static, build-time data. */
    manifest: SSRManifest;
    /** The manifest's identity-stable logger, captured once at construction. */
    logger: AstroLogger;
    /**
     * Whether page renders stream. From the facade hooks on the fast path,
     * else the environment's default.
     */
    streaming: boolean;
    /**
     * Internal facade hook: late-bound `app.renderError` dispatch. Undefined on
     * bare and custom-handler paths — those fall through to the environment's
     * error strategy (`renderErrorPage`).
     */
    renderError: ((request: Request, options: RenderErrorOptions) => Promise<Response>) | undefined;
    /**
     * Internal facade hook: late-bound `app.logThisRequest` dispatch. Undefined
     * on bare and custom-handler paths — those fall through to the
     * environment's `logRequest` behavior.
     */
    logRequest: ((payload: RequestLogPayload) => void) | undefined;
    /**
     * The request to render. Mutated during rewrites so subsequent renders
     * see the rewritten URL.
     */
    request: Request;
    routeData: RouteData | undefined;
    /**
     * The pathname to use for routing and rendering. Starts out as the raw,
     * base-stripped, decoded pathname from the request. May be further
     * normalized by `handleRequest` after routeData is known (in dev, when
     * the matched route has no `.html` extension, `.html` / `/index.html`
     * suffixes are stripped).
     */
    pathname: string;
    /** Resolved render options (addCookieHeader, clientAddress, locals, etc.). */
    readonly renderOptions: ResolvedRenderOptions;
    /** When the request started, used to log duration. */
    readonly timeStart: number;
    /**
     * The route's loaded component module. Set before middleware runs; may
     * be swapped during in-flight rewrites from inside the middleware chain.
     */
    componentInstance: ComponentInstance | undefined;
    /**
     * Slot overrides supplied by the container API. `undefined` for HTTP
     * requests — `PagesHandler` coalesces to `{}` on read so we don't
     * allocate an empty object per request.
     */
    slots: Record<string, any> | undefined;
    /**
     * The `Response` produced by handlers, if any. Set after page
     * rendering or middleware completes.
     */
    response: Response | undefined;
    /**
     * Default HTTP status for the rendered response. Callers override
     * before rendering runs (e.g. `handleRequest` sets this from
     * `BaseApp.getDefaultStatusCode`; error handlers set `404` / `500`).
     */
    status: number;
    /** Whether user middleware should be skipped for this request. */
    skipMiddleware: boolean;
    /**
     * Set to `true` when the request path was encoded too many times to fully
     * decode (see {@link validateAndDecodePathname}). These requests are
     * rejected with a `400` before middleware or routing run.
     */
    invalidEncoding: boolean;
    /** A flag that tells the render content if the rewriting was triggered. */
    isRewriting: boolean;
    /** A safety net in case of loops (rewrite counter). */
    counter: number;
    /** Cookies for this request. Created lazily on first access. */
    cookies: AstroCookies;
    get params(): Params | undefined;
    set params(value: Params | undefined);
    /** Normalized URL for this request. */
    url: URL;
    /** Client address for this request. */
    clientAddress: string | undefined;
    /** Whether this is a partial render (container API). */
    partial: boolean | undefined;
    /** Internal metadata about the current response route type. */
    responseRouteType: 'page' | 'fallback' | undefined;
    /** Internal flag to prevent rerouting this response to an error page. */
    skipErrorReroute: boolean;
    /** Whether to inject CSP meta tags. */
    shouldInjectCspMetaTags: boolean | undefined;
    /** Request-scoped locals object, shared with user middleware. */
    locals: App.Locals;
    /**
     * Memoized `props` (see `getProps`). `null` means "not yet computed"
     * — using `null` (rather than `undefined`) keeps the hidden class
     * stable and distinct from a valid-but-empty result.
     */
    props: APIContext['props'] | null;
    /** Memoized `ActionAPIContext` (see `getActionAPIContext`). */
    actionApiContext: ActionAPIContext | null;
    /** Memoized `APIContext` (see `getAPIContext`). */
    apiContext: APIContext | null;
    /** SSR result for the current page render. */
    result: SSRResult | undefined;
    /** Initial props (from container/error handler). */
    initialProps: Props;
    constructor(manifest: SSRManifest, request: Request, options?: ResolvedRenderOptions, hooks?: FacadeHooks);
    /**
     * Triggers a rewrite. Delegates to the rewrites handler module.
     */
    rewrite(payload: RewritePayload): Promise<Response>;
    /**
     * Creates the SSR result for the current page render.
     */
    createResult(mod: ComponentInstance, ctx: ActionAPIContext): Promise<SSRResult>;
    /**
     * Creates the Astro global object for a component render.
     */
    createAstro(result: SSRResult, props: Record<string, any>, slotValues: Record<string, any> | null, apiContext: ActionAPIContext): AstroGlobal;
    /**
     * Creates the Astro page-level partial (prototype for Astro global).
     */
    createAstroPagePartial(result: SSRResult, apiContext: ActionAPIContext): Omit<AstroGlobal, 'props' | 'self' | 'slots'>;
    getClientAddress(): string;
    getCookies(): AstroCookies;
    getCsp(): APIContext['csp'];
    computeCurrentLocale(): string | undefined;
    computePreferredLocale(): string | undefined;
    computePreferredLocaleList(): string[] | undefined;
    /**
     * Lazily loads the route's component module. Returns the cached
     * instance if already loaded. The promise is cached so concurrent
     * callers share the same load.
     */
    loadComponentInstance(): Promise<ComponentInstance>;
    /**
     * Registers a context provider under the given key. Handlers call
     * this to contribute values to the request context (e.g. sessions).
     * The `create` factory is called lazily on the first `resolve(key)`.
     */
    provide<T>(key: string, provider: ContextProvider<T>): void;
    /**
     * Lazily resolves a provider registered under `key`. Calls
     * `provider.create()` on first access and caches the result.
     * Returns `undefined` if no provider was registered for the key.
     */
    resolve<T>(key: string): T | undefined;
    /**
     * Runs all registered `finalize` callbacks. Should be called after
     * the response is produced, typically in a `finally` block.
     *
     * Returns synchronously (no promise allocation) when nothing needs
     * finalizing — important for the hot path where sessions are not used.
     */
    finalizeAll(): Promise<void> | void;
    /**
     * Adds lazy getters to `target` for each registered provider key.
     * Used by context creation (APIContext, Astro global) so that
     * provider values like `session` and `cache` appear as properties
     * without hard-coding the keys.
     *
     * Always defines a `session` getter (returning `undefined` when no
     * provider is registered) so `ctx.session` / `Astro.session` is a
     * present property regardless of whether the sessions handler was
     * included in the pipeline.
     */
    defineProviderGetters(target: Record<string, any>): void;
    /**
     * Returns the resolved `props` for this render, computing them lazily
     * from the route + component module on first access. If the
     * `initialProps` already carries user-supplied props (e.g. the
     * container API) those are used verbatim.
     */
    getProps(): Promise<APIContext['props']>;
    /**
     * Returns the `ActionAPIContext` for this render, creating it lazily.
     * Used by middleware, actions, and page dispatch.
     */
    getActionAPIContext(): ActionAPIContext;
    /**
     * Returns the `APIContext` for this render, creating it lazily from
     * the memoized props + action context.
     *
     * Callers must ensure `getProps()` has resolved at least once before
     * calling this.
     */
    getAPIContext(): APIContext;
    /**
     * Invalidates the cached `APIContext` so the next `getAPIContext()`
     * call re-derives it from the (possibly mutated) state. Used
     * after an in-flight rewrite swaps the route / request / params.
     */
    invalidateContexts(): void;
    resetResponseMetadata(): void;
}
