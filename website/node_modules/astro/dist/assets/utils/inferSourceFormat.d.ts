/**
 * Infer the image format from a source path or URL.
 *
 * For `data:` URIs the MIME is read up to the first `;` or `,` (whichever comes first),
 * so both `data:image/svg+xml;base64,...` and `data:image/svg+xml,<svg>...` work.
 * `image/svg+xml` normalizes to `svg`; otherwise the subtype after the slash is returned.
 *
 * Returns undefined when the format cannot be determined.
 */
export declare function inferSourceFormat(src: string): string | undefined;
export declare function resolveDefaultOutputFormat(sourceFormat: string | undefined): string;
