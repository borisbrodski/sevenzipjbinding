import { getDefaultClientDirectives } from "../core/client-directive/default.js";
import { ASTRO_CONFIG_DEFAULTS } from "../core/config/schemas/defaults.js";
import { createKey } from "../core/encryption.js";
import { FetchState } from "../core/fetch/fetch-state.js";
import { handleMiddleware } from "../core/middleware/astro-middleware.js";
import { NOOP_MIDDLEWARE_FN } from "../core/middleware/noop-middleware.js";
import { handlePages } from "../core/pages/handler.js";
import { removeLeadingForwardSlash } from "../core/path.js";
import { getParts } from "../core/routing/parts.js";
import { getPattern } from "../core/routing/pattern.js";
import { validateSegment } from "../core/routing/segment.js";
import { SlotString } from "../runtime/server/render/slot.js";
import { createContainerEnvironment } from "./environment.js";
import { setEnvironment } from "../core/environment/index.js";
import { createConsoleLogger } from "../core/logger/impls/console.js";
import { setLogger } from "../core/logger/manifest-logger.js";
import { peekMiddleware } from "../core/middleware/load.js";
import { getRouteTable } from "../core/routing/route-table.js";
function createManifest(manifest, renderers, middleware, site) {
  function middlewareInstance() {
    return {
      onRequest: middleware ?? NOOP_MIDDLEWARE_FN
    };
  }
  let root;
  try {
    root = new URL(import.meta.url);
  } catch {
    root = new URL("file:///container/");
  }
  return {
    rootDir: root,
    srcDir: manifest?.srcDir ?? new URL(ASTRO_CONFIG_DEFAULTS.srcDir, root),
    buildClientDir: manifest?.buildClientDir ?? new URL(ASTRO_CONFIG_DEFAULTS.build.client, root),
    buildServerDir: manifest?.buildServerDir ?? new URL(ASTRO_CONFIG_DEFAULTS.build.server, root),
    publicDir: manifest?.publicDir ?? new URL(ASTRO_CONFIG_DEFAULTS.publicDir, root),
    outDir: manifest?.outDir ?? new URL(ASTRO_CONFIG_DEFAULTS.outDir, root),
    cacheDir: manifest?.cacheDir ?? new URL(ASTRO_CONFIG_DEFAULTS.cacheDir, root),
    trailingSlash: manifest?.trailingSlash ?? ASTRO_CONFIG_DEFAULTS.trailingSlash,
    buildFormat: manifest?.buildFormat ?? ASTRO_CONFIG_DEFAULTS.build.format,
    compressHTML: manifest?.compressHTML ?? ASTRO_CONFIG_DEFAULTS.compressHTML,
    assetsDir: manifest?.assetsDir ?? ASTRO_CONFIG_DEFAULTS.build.assets,
    serverLike: manifest?.serverLike ?? true,
    middlewareMode: manifest?.middlewareMode ?? "classic",
    assets: manifest?.assets ?? /* @__PURE__ */ new Set(),
    assetsPrefix: manifest?.assetsPrefix ?? void 0,
    entryModules: manifest?.entryModules ?? {},
    routes: manifest?.routes ?? [],
    adapterName: "",
    clientDirectives: manifest?.clientDirectives ?? getDefaultClientDirectives(),
    renderers: renderers ?? manifest?.renderers ?? [],
    base: manifest?.base ?? ASTRO_CONFIG_DEFAULTS.base,
    userAssetsBase: manifest?.userAssetsBase ?? "",
    componentMetadata: manifest?.componentMetadata ?? /* @__PURE__ */ new Map(),
    inlinedScripts: manifest?.inlinedScripts ?? /* @__PURE__ */ new Map(),
    i18n: manifest?.i18n,
    site: site ?? manifest?.site,
    checkOrigin: false,
    allowedDomains: manifest?.allowedDomains ?? [],
    actionBodySizeLimit: 1024 * 1024,
    serverIslandBodySizeLimit: 1024 * 1024,
    middleware: manifest?.middleware ?? middlewareInstance,
    key: createKey(),
    csp: manifest?.csp,
    image: manifest?.image ?? {},
    shouldInjectCspMetaTags: false,
    devToolbar: {
      enabled: false,
      latestAstroVersion: void 0,
      debugInfoOutput: "",
      placement: void 0
    },
    logLevel: "silent"
  };
}
class experimental_AstroContainer {
  /**
   * The container's fabricated manifest — the source of truth all the
   * functional-core accessors key off. The container never touches the
   * ambient manifest, so multiple containers in one process stay isolated.
   */
  #manifest;
  /**
   * The route → module interner, shared between the environment record
   * (lookups) and the `insertRoute` writes below.
   */
  #interner;
  /**
   * Internally used to check if the container was created with a manifest.
   * @private
   */
  #withManifest = false;
  constructor({
    streaming = false,
    manifest,
    renderers,
    resolve,
    site
  }) {
    const ssrManifest = createManifest(manifest, renderers, void 0, site);
    const containerRenderers = renderers ?? manifest?.renderers ?? [];
    const containerResolve = async (specifier) => {
      if (this.#withManifest) {
        return this.#containerResolve(specifier, ssrManifest);
      } else if (resolve) {
        return resolve(specifier);
      }
      return specifier;
    };
    const interner = /* @__PURE__ */ new WeakMap();
    setLogger(ssrManifest, createConsoleLogger({ level: "error" }));
    setEnvironment(
      ssrManifest,
      createContainerEnvironment({
        interner,
        resolve: containerResolve,
        renderers: containerRenderers,
        streaming
      })
    );
    getRouteTable(ssrManifest);
    this.#manifest = ssrManifest;
    this.#interner = interner;
  }
  async #containerResolve(specifier, manifest) {
    const found = manifest.entryModules[specifier];
    if (found) {
      return new URL(found, manifest.buildClientDir).toString();
    }
    return found;
  }
  /**
   * Creates a new instance of a container.
   *
   * @param {AstroContainerOptions=} containerOptions
   */
  static async create(containerOptions = {}) {
    const { streaming = false, manifest, renderers = [], resolve, astroConfig } = containerOptions;
    return new experimental_AstroContainer({
      streaming,
      manifest,
      renderers,
      resolve,
      site: astroConfig?.site ?? manifest?.site
    });
  }
  /**
   * Use this function to manually add a **server** renderer to the container.
   *
   * This function is preferred when you require to use the container with a renderer in environments such as on-demand pages.
   *
   * ## Example
   *
   * ```js
   * import reactRenderer from "@astrojs/react/server.js";
   * import vueRenderer from "@astrojs/vue/server.js";
   * import customRenderer from "../renderer/customRenderer.js";
   * import { experimental_AstroContainer as AstroContainer } from "astro/container"
   *
   * const container = await AstroContainer.create();
   * container.addServerRenderer(reactRenderer);
   * container.addServerRenderer(vueRenderer);
   * container.addServerRenderer("customRenderer", customRenderer);
   * ```
   *
   * @param options {object}
   * @param options.name The name of the renderer. The name **isn't** arbitrary, and it should match the name of the package.
   * @param options.renderer The server renderer exported by integration.
   */
  addServerRenderer(options) {
    const { renderer } = options;
    if (!renderer.check || !renderer.renderToStaticMarkup) {
      throw new Error(
        "The renderer you passed isn't valid. A renderer is usually an object that exposes the `check` and `renderToStaticMarkup` functions.\nUsually, the renderer is exported by a /server.js entrypoint e.g. `import renderer from '@astrojs/react/server.js'`"
      );
    }
    if (isNamedRenderer(renderer)) {
      this.#manifest.renderers.push({
        name: renderer.name,
        ssr: renderer
      });
    } else if ("name" in options) {
      this.#manifest.renderers.push({
        name: options.name,
        ssr: renderer
      });
    } else {
      throw new Error(
        "The renderer name must be provided when adding a server renderer that is not a named renderer."
      );
    }
  }
  /**
   * Use this function to manually add a **client** renderer to the container.
   *
   * When rendering components that use the `client:*` directives, you need to use this function.
   *
   * ## Example
   *
   * ```js
   * import reactRenderer from "@astrojs/react/server.js";
   * import { experimental_AstroContainer as AstroContainer } from "astro/container"
   *
   * const container = await AstroContainer.create();
   * container.addServerRenderer(reactRenderer);
   * container.addClientRenderer({
   * 	name: "@astrojs/react",
   * 	entrypoint: "@astrojs/react/client.js"
   * });
   * ```
   *
   * @param options {object}
   * @param options.name The name of the renderer. The name **isn't** arbitrary, and it should match the name of the package.
   * @param options.entrypoint The entrypoint of the client renderer.
   */
  addClientRenderer(options) {
    const { entrypoint, name } = options;
    const rendererIndex = this.#manifest.renderers.findIndex((r) => r.name === name);
    if (rendererIndex === -1) {
      throw new Error(
        "You tried to add the " + name + " client renderer, but its server renderer wasn't added. You must add the server renderer first. Use the `addServerRenderer` function."
      );
    }
    const renderer = this.#manifest.renderers[rendererIndex];
    renderer.clientEntrypoint = entrypoint;
    this.#manifest.renderers[rendererIndex] = renderer;
  }
  // NOTE: we keep this private via TS instead via `#` so it's still available on the surface, so we can play with it.
  // @ts-expect-error @ematipico: I plan to use it for a possible integration that could help people
  static async createFromManifest(manifest) {
    const container = new experimental_AstroContainer({
      manifest
    });
    container.#withManifest = true;
    return container;
  }
  /**
   * Associates a runtime-inserted route with its component module in the
   * interner shared with the container environment record. Snapshots the
   * already-resolved middleware synchronously via `peekMiddleware` —
   * `undefined` when `getMiddleware` has not settled yet.
   */
  #internRoute(routeData, componentInstance) {
    this.#interner.set(routeData, {
      page() {
        return Promise.resolve(componentInstance);
      },
      onRequest: peekMiddleware(this.#manifest)
    });
  }
  #insertRoute({
    path,
    componentInstance,
    params = {},
    type = "page"
  }) {
    const pathUrl = new URL(path, "https://example.com");
    const routeData = this.#createRoute(pathUrl, params, type);
    this.#manifest.routes.push({
      routeData,
      file: "",
      links: [],
      styles: [],
      scripts: []
    });
    this.#internRoute(routeData, componentInstance);
    return routeData;
  }
  /**
   * @description
   * It renders a component and returns the result as a string.
   *
   * ## Example
   *
   * ```js
   * import Card from "../src/components/Card.astro";
   *
   * const container = await AstroContainer.create();
   * const result = await container.renderToString(Card);
   *
   * console.log(result); // it's a string
   * ```
   *
   *
   * @param {AstroComponentFactory} component The instance of the component.
   * @param {ContainerRenderOptions=} options Possible options to pass when rendering the component.
   */
  async renderToString(component, options = {}) {
    if (options.slots) {
      options.slots = markAllSlotsAsSlotString(options.slots);
    }
    const response = await this.renderToResponse(component, options);
    return await response.text();
  }
  /**
   * @description
   * It renders a component and returns the `Response` as result of the rendering phase.
   *
   * ## Example
   *
   * ```js
   * import Card from "../src/components/Card.astro";
   *
   * const container = await AstroContainer.create();
   * const response = await container.renderToResponse(Card);
   *
   * console.log(response.status); // it's a number
   * ```
   *
   *
   * @param {AstroComponentFactory} component The instance of the component.
   * @param {ContainerRenderOptions=} options Possible options to pass when rendering the component.
   */
  async renderToResponse(component, options = {}) {
    const { routeType = "page", slots } = options;
    const request = options?.request ?? new Request("https://example.com/");
    const url = new URL(request.url);
    const componentInstance = routeType === "endpoint" ? component : this.#wrapComponent(component, options.params);
    const routeData = this.#insertRoute({
      path: request.url,
      componentInstance,
      params: options.params,
      type: routeType
    });
    const state = new FetchState(this.#manifest, request);
    state.routeData = routeData;
    state.pathname = url.pathname;
    state.clientAddress = "";
    state.partial = options?.partial ?? true;
    state.componentInstance = componentInstance;
    state.slots = slots ?? {};
    if (options.params) {
      state.params = options.params;
    }
    state.locals = options?.locals ?? {};
    if (options.props) {
      state.initialProps = options.props;
    }
    return handleMiddleware(state, handlePages);
  }
  /**
   * It stores an Astro **page** route. The first argument, `route`, gets associated to the `component`.
   *
   * This function can be useful when you want to render a route via `AstroContainer.renderToString`, where that
   * route eventually renders another route via `Astro.rewrite`.
   *
   * @param {string} route - The URL that will render the component.
   * @param {AstroComponentFactory} component - The component factory to be used for rendering the route.
   * @param {Record<string, string | undefined>} params - An object containing key-value pairs of route parameters.
   */
  insertPageRoute(route, component, params) {
    const url = new URL(route, "https://example.com/");
    const routeData = this.#createRoute(url, params ?? {}, "page");
    this.#manifest.routes.push({
      routeData,
      file: "",
      links: [],
      styles: [],
      scripts: []
    });
    const componentInstance = this.#wrapComponent(component, params);
    this.#internRoute(routeData, componentInstance);
  }
  #createRoute(url, params, type) {
    const segments = removeLeadingForwardSlash(url.pathname).split("/").filter(Boolean).map((s) => {
      validateSegment(s);
      return getParts(s, url.pathname);
    });
    return {
      route: url.pathname,
      component: "",
      params: Object.keys(params),
      pattern: getPattern(
        segments,
        ASTRO_CONFIG_DEFAULTS.base,
        ASTRO_CONFIG_DEFAULTS.trailingSlash
      ),
      prerender: false,
      segments,
      type,
      fallbackRoutes: [],
      isIndex: false,
      origin: "internal",
      distURL: []
    };
  }
  /**
   * If the provided component isn't a default export, the function wraps it in an object `{default: Component }` to mimic the default export.
   * @param componentFactory
   * @param params
   * @private
   */
  #wrapComponent(componentFactory, params) {
    if (params) {
      return {
        default: componentFactory,
        getStaticPaths() {
          return [{ params }];
        }
      };
    }
    return { default: componentFactory };
  }
}
function isNamedRenderer(renderer) {
  return !!renderer?.name;
}
function markAllSlotsAsSlotString(slots) {
  const markedSlots = {};
  for (const slotName in slots) {
    markedSlots[slotName] = new SlotString(slots[slotName], null);
  }
  return markedSlots;
}
export {
  experimental_AstroContainer
};
