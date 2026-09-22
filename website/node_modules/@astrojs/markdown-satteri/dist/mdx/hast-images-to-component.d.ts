import { type HastPluginDefinition } from 'satteri';
export interface ImageImportInfo {
    importedImages: Map<string, string>;
    hasImages: boolean;
}
export declare function createImageToComponentPlugin(imageImportInfo: ImageImportInfo): HastPluginDefinition;
