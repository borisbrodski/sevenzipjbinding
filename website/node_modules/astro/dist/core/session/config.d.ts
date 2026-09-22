import * as z from 'zod/v4';
export declare const SessionDriverConfigSchema: z.ZodObject<{
    config: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodAny>>;
    entrypoint: z.ZodUnion<readonly [z.ZodString, z.ZodCustom<URL, URL>]>;
}, z.core.$strip>;
export declare const SessionSchema: z.ZodUnion<readonly [z.ZodLiteral<false>, z.ZodObject<{
    driver: z.ZodOptional<z.ZodUnion<readonly [z.ZodString, z.ZodObject<{
        config: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodAny>>;
        entrypoint: z.ZodUnion<readonly [z.ZodString, z.ZodCustom<URL, URL>]>;
    }, z.core.$strip>]>>;
    options: z.ZodOptional<z.ZodRecord<z.ZodString, z.ZodAny>>;
    cookie: z.ZodOptional<z.ZodUnion<readonly [z.ZodObject<{
        name: z.ZodOptional<z.ZodString>;
        domain: z.ZodOptional<z.ZodString>;
        path: z.ZodOptional<z.ZodString>;
        maxAge: z.ZodOptional<z.ZodNumber>;
        sameSite: z.ZodOptional<z.ZodUnion<readonly [z.ZodEnum<{
            none: "none";
            strict: "strict";
            lax: "lax";
        }>, z.ZodBoolean]>>;
        secure: z.ZodOptional<z.ZodBoolean>;
    }, z.core.$strip>, z.ZodPipe<z.ZodString, z.ZodTransform<{
        name: string;
    }, string>>]>>;
    ttl: z.ZodOptional<z.ZodNumber>;
}, z.core.$strip>]>;
