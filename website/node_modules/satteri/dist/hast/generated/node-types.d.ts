/** Node-type tag -> canonical AST name. */
export declare const TYPE_NAMES: Readonly<Record<number, string>>;
/** Canonical AST name -> node-type tag. */
export declare const NAME_TO_TYPE: Readonly<Record<string, number>>;
/** Names a plugin can subscribe to (every node except `root`). */
export declare const VISITOR_KEYS: ReadonlySet<string>;
/** Name -> tag for the types the op-stream can encode; one lookup gates AND
 *  resolves the emit-path tag. Excluded names (see `*_OPSTREAM_EXCLUDED` in
 *  schema.rs) have no encoding — the visitor throws for them. */
export declare const HAST_OPSTREAM_TYPES: Readonly<Record<string, number>>;
/** Names of the variable-length `custom` node types (hand-written or
 *  `Tail`-generated codec). The op-stream round-trip oracle asserts it covers
 *  every one, so a forgotten or drifted encode/decode arm fails loudly. */
export declare const HAST_CUSTOM_TYPES: readonly string[];
