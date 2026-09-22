import type { AstroMetadata } from '@astrojs/internal-helpers/markdown';
import { type HastPluginDefinition } from 'satteri';
export type { AstroMetadata };
declare module 'satteri' {
    interface DataMap {
        __astroMetadata: AstroMetadata;
    }
}
export declare function createAstroMetadataPlugin(filePath: string): HastPluginDefinition;
