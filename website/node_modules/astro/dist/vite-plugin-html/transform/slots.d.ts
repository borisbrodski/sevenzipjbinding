export declare const SLOT_PREFIX = "___SLOTS___";
export interface SlotReplacement {
    start: number;
    end: number;
    value: string;
}
/**
 * Find every `<slot>` (excluding `is:inline` ones) and describe how to replace it
 * with a `${___SLOTS___[name] ?? `fallback`}` template literal expression.
 */
export declare function collectSlots(code: string): SlotReplacement[];
