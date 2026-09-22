/**
 * Creates a normalized URL from a request URL string.
 * Decodes and validates the pathname, collapses duplicate slashes.
 */
export declare function createNormalizedUrl(requestUrl: string): URL;
/**
 * Assigns `url.pathname` only when the value differs.
 * The setter re-parses the whole URL, so a no-op write is still expensive.
 */
export declare function setPathname(url: URL, pathname: string): void;
/**
 * Normalizes an already-parsed URL in place: decodes and validates the
 * pathname, collapses duplicate slashes. Returns the same URL object.
 *
 * Collapse runs after the decode is written back: the pathname setter
 * rewrites `\` to `/`, so a decoded backslash only becomes `//` once assigned.
 */
export declare function normalizeUrl(url: URL): URL;
