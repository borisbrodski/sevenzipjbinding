import type { FetchState } from '../../core/fetch/fetch-state.js';
import type { AstroLogger } from '../../core/logger/core.js';
import type { APIRoute } from '../../types/public/common.js';
import type { APIContext } from '../../types/public/context.js';
/** Renders an endpoint request to completion, returning the body. */
export declare function renderEndpoint(mod: {
    [method: string]: APIRoute;
}, context: APIContext, isPrerendered: boolean, logger: AstroLogger, state?: FetchState): Promise<Response>;
