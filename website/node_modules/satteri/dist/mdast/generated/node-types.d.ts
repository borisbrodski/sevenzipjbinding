/** Node-type tag -> canonical AST name. */
export declare const TYPE_NAMES: Readonly<Record<number, string>>;
/** Canonical AST name -> node-type tag. */
export declare const NAME_TO_TYPE: Readonly<Record<string, number>>;
/** Node-type tag by Rust enum variant name. */
export declare const NodeType: Readonly<{
    readonly Root: 0;
    readonly Paragraph: 1;
    readonly Heading: 2;
    readonly ThematicBreak: 3;
    readonly Blockquote: 4;
    readonly List: 5;
    readonly ListItem: 6;
    readonly Html: 7;
    readonly Code: 8;
    readonly Definition: 9;
    readonly Text: 10;
    readonly Emphasis: 11;
    readonly Strong: 12;
    readonly InlineCode: 13;
    readonly Break: 14;
    readonly Link: 15;
    readonly Image: 16;
    readonly LinkReference: 17;
    readonly ImageReference: 18;
    readonly FootnoteDefinition: 19;
    readonly FootnoteReference: 20;
    readonly Table: 21;
    readonly TableRow: 22;
    readonly TableCell: 23;
    readonly Delete: 24;
    readonly Yaml: 25;
    readonly Toml: 26;
    readonly Math: 27;
    readonly InlineMath: 28;
    readonly ContainerDirective: 30;
    readonly LeafDirective: 31;
    readonly TextDirective: 32;
    readonly Superscript: 33;
    readonly Subscript: 34;
    readonly DescriptionList: 35;
    readonly DescriptionTerm: 36;
    readonly DescriptionDetails: 37;
    readonly Custom: 38;
    readonly MdxJsxFlowElement: 100;
    readonly MdxJsxTextElement: 101;
    readonly MdxFlowExpression: 102;
    readonly MdxTextExpression: 103;
    readonly MdxjsEsm: 104;
}>;
/** Node-type tag -> Rust variant name (`typeName` diagnostics). */
export declare const NodeTypeName: Readonly<Record<number, string>>;
/** Names a plugin can subscribe to (every node except `root`). */
export declare const VISITOR_KEYS: ReadonlySet<string>;
/** Name -> tag for the types the op-stream can encode; one lookup gates AND
 *  resolves the emit-path tag. Excluded names (see `*_OPSTREAM_EXCLUDED` in
 *  schema.rs) have no encoding — the visitor throws for them. */
export declare const MDAST_OPSTREAM_TYPES: Readonly<Record<string, number>>;
/** Names of the variable-length `custom` node types (hand-written or
 *  `Tail`-generated codec). The op-stream round-trip oracle asserts it covers
 *  every one, so a forgotten or drifted encode/decode arm fails loudly. */
export declare const MDAST_CUSTOM_TYPES: readonly string[];
