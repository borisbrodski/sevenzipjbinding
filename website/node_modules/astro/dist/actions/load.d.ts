import type { $ZodType } from 'zod/v4/core';
import type { SSRActions, SSRManifest } from '../types/public/internal.js';
import type { ActionAccept, ActionClient } from './runtime/types.js';
/** Resolves the actions module from the manifest (a no-op module when none). */
export declare function getActions(manifest: SSRManifest): Promise<SSRActions>;
/**
 * Clears the cached actions so they are re-resolved on the next request.
 * Called via HMR when action files change during development.
 */
export declare function clearActions(manifest: SSRManifest): void;
/** Looks up a single action handler by its dot-separated path. */
export declare function getAction(manifest: SSRManifest, path: string): Promise<ActionClient<unknown, ActionAccept, $ZodType>>;
