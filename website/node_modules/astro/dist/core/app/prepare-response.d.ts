/**
 * Appends cookies written via `Astro.cookie.set()` to the `Set-Cookie` header
 * and marks the response as sent.
 *
 * This is a pure function with no dependencies on the app; it is shared by
 * `handleRequest` and the various error handlers.
 */
export declare function prepareResponse(response: Response, { addCookieHeader }: {
    addCookieHeader: boolean;
}): void;
