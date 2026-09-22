import type { Rolldown } from 'vite';
import * as z from 'zod/v4';
export declare function createImage(pluginContext: Rolldown.PluginContext, shouldEmitFile: boolean, entryFilePath: string): () => z.ZodPipe<z.ZodString, z.ZodTransform<z.ZodNever | {
    ASTRO_ASSET: string;
    width: number;
    height: number;
    format: import("../assets/types.js").ImageInputFormat;
    src: string;
    fsPath: string;
    orientation?: number | undefined;
}, string>>;
