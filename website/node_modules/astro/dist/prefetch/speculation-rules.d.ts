/**
 * Generates static speculation rules JSON using `"source": "document"` with CSS selector matching.
 * This produces a deterministic payload that can be hashed at build time for CSP compatibility,
 * unlike the dynamic per-URL `"source": "list"` approach in `appendSpeculationRules()`.
 */
export declare function generateSpeculationRulesContent(prefetchAll: boolean): string;
