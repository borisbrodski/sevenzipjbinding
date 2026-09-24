import type fsMod from 'node:fs';
/**
 * Check whether the Actions config file is present.
 */
export declare function isActionsFilePresent(fs: typeof fsMod, srcDir: URL): Promise<string | false>;
