import type { FetchState } from '../fetch/fetch-state.js';
/**
 * The composite "batteries-included" handler that wires up every request
 * feature internally; `astro(state)` (astro/fetch) delegates here, as does
 * `BaseApp.render`'s default-handler fast path.
 */
export declare function handleRequest(state: FetchState): Promise<Response>;
