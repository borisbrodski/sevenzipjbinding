import type { SSRManifest } from '../app/types.js';
import type { AstroLogger } from '../logger/core.js';
/**
 * For domain-based i18n routing strategies, derives the locale-prefixed
 * pathname from the request's `Host` header rather than its URL. For example,
 * a request for `/foo` served from `https://example.fr` resolves to `/fr/foo`.
 *
 * Returns `undefined` when the strategy isn't domain-based or the host isn't
 * mapped to a locale — in which case normal pathname routing applies.
 *
 */
export declare function computePathnameFromDomain(request: Request, url: URL, i18n: SSRManifest['i18n'], base: SSRManifest['base'], trailingSlash: SSRManifest['trailingSlash'], logger: AstroLogger, pathnameFromRequest?: string): string | undefined;
