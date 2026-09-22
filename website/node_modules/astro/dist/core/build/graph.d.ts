import type { Rolldown } from 'vite';
interface ExtendedModuleInfo {
    info: Rolldown.ModuleInfo;
    depth: number;
    order: number;
}
export declare function getParentExtendedModuleInfos(id: string, ctx: {
    getModuleInfo: Rolldown.GetModuleInfo;
}, until?: (importer: string) => boolean, depth?: number, order?: number, childId?: string, seen?: Set<string>, accumulated?: ExtendedModuleInfo[]): ExtendedModuleInfo[];
export declare function getParentModuleInfos(id: string, ctx: {
    getModuleInfo: Rolldown.GetModuleInfo;
}, until?: (importer: string) => boolean, seen?: Set<string>, accumulated?: Rolldown.ModuleInfo[]): Rolldown.ModuleInfo[];
export declare function moduleIsTopLevelPage(info: Rolldown.ModuleInfo): boolean;
export declare function getTopLevelPageModuleInfos(id: string, ctx: {
    getModuleInfo: Rolldown.GetModuleInfo;
}): Rolldown.ModuleInfo[];
export {};
