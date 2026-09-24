/**
 * Shared machinery for walk-path child stubs (see `hast/child-stub.ts` and
 * `mdast/child-stub.ts`): lightweight stand-ins that carry arena id + type
 * eagerly and defer the full-arena snapshot until a plugin reads a real field.
 * Passthrough children (the common replaceNode shape) thus compile to one-word
 * refs without ever serializing the arena.
 */
export type StubDescriptorEntry = readonly [string, PropertyDescriptor];
/**
 * Descriptor entries for one node type's stub fields. Own enumerable getters
 * so a spread copy carries correct values. `position`/`data` ride on every
 * stub; they may yield `undefined` where a materialized node omits the key —
 * accepted drift, invisible to `toEqual`. Installed one `defineProperty` at a
 * time: `Object.defineProperties` re-validates the whole map per call and
 * costs nearly double.
 */
export declare function stubDescriptors(fields: readonly string[]): readonly StubDescriptorEntry[];
export declare function installStubDescriptors(host: object, entries: readonly StubDescriptorEntry[]): void;
/** Node tags are dense small ints, so the per-child stub loop indexes flat
 *  arrays instead of paying Map/dictionary lookups. */
export declare function flatByTag<T>(table: Readonly<Record<number, T>>): readonly (T | undefined)[];
