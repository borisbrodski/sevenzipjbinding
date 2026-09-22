import type { SSRManifestCSP } from '../app/types.js';
import type { CspDirective, CspHash, CspHashEntry, CspKind, CspResourceEntry } from './config.js';
export declare function normalizeCspResourceEntry(entry: CspResourceEntry): {
    resource: string;
    kind: CspKind;
};
export declare function normalizeCspHashEntry(entry: CspHashEntry): {
    hash: CspHash;
    kind: CspKind;
};
/** The resolved sources of a single CSP directive. */
export type CspDirectiveSources = {
    resources: string[];
    hashes: string[];
};
/**
 * Groups a directive's `resources`/`hashes` entries by their `kind`.
 */
export declare function partitionByKind(directive: {
    resources: CspResourceEntry[];
    hashes: CspHashEntry[];
}): Record<CspKind, CspDirectiveSources>;
/**
 * `existingDirective` is something like `img-src 'self'`. Same as `newDirective`.
 *
 * Returns `undefined` if no directive has been deduped
 * @param existingDirective
 * @param newDirective
 */
export declare function deduplicateDirectiveValues(existingDirective: CspDirective, newDirective: CspDirective): CspDirective | undefined;
export declare function pushDirective(directives: SSRManifestCSP['directives'], newDirective: CspDirective): SSRManifestCSP['directives'];
