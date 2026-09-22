import type { AstroRuntimeLogger } from '../../types/public/context.js';
export declare function loadRemoteImage(src: URL): Promise<Buffer | undefined>;
export type LocalImageLoadResult = {
    kind: 'loaded';
    buffer: Buffer;
} | {
    kind: 'invalid-path';
} | {
    kind: 'not-found';
} | {
    kind: 'failed';
};
export declare const handleImageRequest: ({ request, loadLocalImage, logger, }: {
    request: Request;
    loadLocalImage: (src: string, baseUrl: URL) => Promise<LocalImageLoadResult>;
    logger: AstroRuntimeLogger;
}) => Promise<Response>;
