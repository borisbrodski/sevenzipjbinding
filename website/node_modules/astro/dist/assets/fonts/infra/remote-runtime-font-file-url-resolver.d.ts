import type { AddressInfo } from 'node:net';
import type { RuntimeFontFileUrlResolver } from '../definitions.js';
/**
 * In development, font files are served through a Vite middleware.
 * During prerendering, a temporary Node HTTP server is started to
 * serve font files.
 *
 * When possible, the resolver uses a statically known server
 * {@link address}. When the address is not yet available (e.g. the
 * virtual module was evaluated before the HTTP server started
 * listening — see #17722), the resolver falls back to deriving the
 * origin from the caller-supplied {@link requestUrl}.
 */
export declare class RemoteRuntimeFontFileUrlResolver implements RuntimeFontFileUrlResolver {
    #private;
    constructor({ urls, address, }: {
        urls: Set<string>;
        address: AddressInfo | null;
    });
    resolve(url: string, requestUrl: URL | undefined): string | null;
}
