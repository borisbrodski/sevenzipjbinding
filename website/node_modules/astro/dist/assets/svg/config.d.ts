import * as z from 'zod/v4';
export declare const SvgOptimizerSchema: z.ZodObject<{
    name: z.ZodString;
    optimize: z.ZodCustom<(contents: string, path: string) => string | Promise<string>, (contents: string, path: string) => string | Promise<string>>;
}, z.core.$strip>;
