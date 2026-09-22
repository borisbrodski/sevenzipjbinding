import type { SSRManifest } from '../app/types.js';
import type { SessionDriverFactory } from './types.js';
/** Resolves the session driver factory from the manifest, `null` when none. */
export declare function getSessionDriver(manifest: SSRManifest): Promise<SessionDriverFactory | null>;
