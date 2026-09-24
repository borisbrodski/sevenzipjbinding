import { MdastReader } from "./mdast-reader.js";
import { CommandBuffer } from "../command-buffer.js";
import type { MdastNode, Toml, MathNode, InlineMath, Superscript, Subscript, DescriptionList, DescriptionTerm, DescriptionDetails, Custom, Data, SourceFormat } from "../types.js";
import { type PluginOptions } from "../visitor-shared.js";
import { LazyChildResolver } from "../lazy-child-resolver.js";
import type { MdastHandle } from "../handles.js";
import type { Blockquote, Break, Code, Definition, Delete, Emphasis, FootnoteDefinition, FootnoteReference, Heading, Html, Image, ImageReference, InlineCode, Link, LinkReference, List, ListItem, Paragraph, Strong, Table, TableRow, TableCell, Text, ThematicBreak, Yaml, Parents as MdastParents, Root as MdastRoot } from "mdast";
import type { MdxJsxFlowElement, MdxJsxTextElement } from "../mdx-types.js";
import type { MdxFlowExpression, MdxTextExpression } from "../mdx-types.js";
import type { MdxjsEsm } from "../mdx-types.js";
import type { ContainerDirective, LeafDirective, TextDirective } from "../directive-types.js";
/** A string spliced into the tree, re-parsed as Markdown. Set `mdxExpressions:
 *  false` to keep MDX `{…}` literal, needed when injecting generated HTML
 *  (KaTeX, highlighters, diagrams) whose braces aren't expressions. Default true. */
export interface RawMdastContent {
    raw: string;
    mdxExpressions?: boolean;
}
export interface RawHtmlMdastContent {
    /** @deprecated Use the equivalent `{ raw, mdxExpressions: false }`. */
    rawHtml: string;
}
/** New content for a structural mutation: a declarative node, or a raw string
 *  escape hatch ({@link RawMdastContent}). Declarative nodes compile to the
 *  op-stream; a type the op-stream can't encode is a hard error. */
export type MdastContent = MdastNode | Custom | RawMdastContent | RawHtmlMdastContent;
/** An existing node a mutation targets. Includes {@link Custom} so a node
 *  reached through the `custom` visitor can be passed straight back in. */
export type MdastTarget = MdastNode | Custom;
/** A `wrapNode` wrapper: a built-in parent, or a user-defined node declaring
 *  a children array. */
export type MdastParentContent = Exclude<Extract<MdastNode, {
    children: unknown[];
}>, MdastRoot> | (Custom & {
    children: NonNullable<Custom["children"]>;
});
export interface MdastDiagnostic {
    message: string;
    nodeId?: number | undefined;
    position?: MdastNode["position"] | undefined;
    severity: "error" | "warning" | "info";
}
export declare class MdastVisitorContext {
    #private;
    /**
     * The URL of the document being processed (the compile `fileURL` option),
     * or `undefined` when none was given. Use `fileURLToPath(ctx.fileURL)` for a
     * decoded filesystem path.
     */
    readonly fileURL: URL | undefined;
    /**
     * Document-level data bag, shared across every plugin in the compile and
     * across the mdast→hast phase boundary. Mutate keys directly
     * (`ctx.data.foo = x`); the bag itself isn't reassignable. Values are kept
     * on the JS side, so any value is allowed, including functions and class
     * instances. Returned to the caller as `result.data`.
     */
    readonly data: Data;
    /**
     * The source format this compile is processing: `"markdown"` for a plain
     * Markdown compile, `"mdx"` for an MDX one. Lets a plugin shared between both
     * pipelines branch on which it is handling.
     */
    readonly sourceFormat: SourceFormat;
    constructor(handle: MdastHandle, getSource: () => string, fileURL: URL | undefined, resolver: LazyChildResolver<MdastReader, MdastNode>, data: Data, sourceFormat: SourceFormat, diagnostics: MdastDiagnostic[]);
    get source(): string;
    removeNode(node: Readonly<MdastTarget>): void;
    insertBefore(node: Readonly<MdastTarget>, newNode: MdastContent | MdastContent[]): void;
    insertAfter(node: Readonly<MdastTarget>, newNode: MdastContent | MdastContent[]): void;
    /**
     * Wrap `node` in `parentNode`, making it `parentNode`'s first child. Any
     * children `parentNode` declares are kept after it. `parentNode` must be a
     * node type that can hold children, or a raw string parsing to exactly one
     * such block (`{ raw: "> " }` wraps in a blockquote); to surround a node
     * with raw HTML tags, use `replaceNode(node, [openTag, node, closeTag])`
     * instead.
     */
    wrapNode(node: Readonly<MdastTarget>, parentNode: MdastParentContent | RawMdastContent | RawHtmlMdastContent): void;
    prependChild(node: Readonly<MdastTarget>, childNode: MdastContent | MdastContent[]): void;
    appendChild(node: Readonly<MdastTarget>, childNode: MdastContent | MdastContent[]): void;
    /** Insert one node or an array at `index`; clamps (`0` or less prepends, past the end appends). */
    insertChildAt(node: Readonly<MdastTarget>, index: number, childNode: MdastContent | MdastContent[]): void;
    /** Remove the `index`-th child of `node`; a no-op when there is no such child. */
    removeChildAt(node: Readonly<MdastTarget>, index: number): void;
    /**
     * Swap `node` for one node, or for an array of nodes placed in order at its
     * position. An empty array drops the node, the same as `removeNode`.
     * The document root takes a `root`, the one place a `root` is accepted as
     * content, or a raw string, which parses to a root of its own.
     */
    replaceNode(node: Readonly<MdastTarget>, newNode: MdastContent | MdastContent[]): void;
    setProperty<N extends MdastTarget, K extends keyof N & string>(node: Readonly<N>, key: K, value: N[K]): void;
    /** `children` is structural and every parent accepts it, so the key also
     *  works on node-type unions (e.g. a node returned by `parent()`). */
    setProperty(node: Readonly<MdastTarget>, key: "children", value: readonly MdastTarget[]): void;
    /** `data` is an open per-node bag serialized to JSON on the wire, so it
     *  accepts any record (hName/hProperties/custom fields), not just the node's
     *  declared `data` shape. `null` clears it. */
    setProperty(node: Readonly<MdastTarget>, key: "data", value: Record<string, unknown> | null): void;
    /** Collect the concatenated text of all descendant text nodes (like mdast-util-to-string). */
    textContent(node: Readonly<MdastTarget>, options?: {
        includeImageAlt?: boolean;
        includeHtml?: boolean;
    }): string;
    /**
     * The parent of a node, or `undefined` at the root. Within a pass the same
     * parent is always the same object, so visitors on sibling nodes can dedupe
     * by identity.
     */
    parent<N extends Exclude<MdastNode, MdastRoot>>(node: Readonly<N>): Readonly<MdastParents>;
    parent(node: Readonly<MdastTarget>): Readonly<MdastParents> | undefined;
    /**
     * Index of `node` within its parent's children, or `undefined` at the root.
     * Use this rather than `parent.children.indexOf(node)`, which won't find it.
     */
    indexOf(node: Readonly<MdastTarget>): number | undefined;
    report({ message, node, severity, }: {
        message: string;
        node?: Readonly<MdastTarget>;
        severity?: "error" | "warning" | "info";
    }): void;
    /** Get the binary command buffer for all mutations recorded via context methods. */
    getCommandBuffer(): CommandBuffer;
    getDiagnostics(): MdastDiagnostic[];
}
type MdastVisitorResult = MdastNode | RawMdastContent | RawHtmlMdastContent | undefined | null | void;
type MdastVisitorFn<N extends MdastNode | Custom = MdastNode> = (node: Readonly<N>, context: MdastVisitorContext) => MdastVisitorResult | Promise<MdastVisitorResult>;
export type MdastHookFn = (root: Readonly<MdastRoot>, context: MdastVisitorContext) => void | Promise<void>;
export interface MdastPluginInstance {
    /** Plugin-level configuration (e.g. `{ position: true }` to read positions). */
    options?: PluginOptions;
    /** Runs once per document, before the plugin's visitors. Awaited when async. */
    before?: MdastHookFn;
    /** Runs once per document, after the plugin's visitors have settled. Awaited
     *  when async. */
    after?: MdastHookFn;
    paragraph?: MdastVisitorFn<Paragraph>;
    heading?: MdastVisitorFn<Heading>;
    thematicBreak?: MdastVisitorFn<ThematicBreak>;
    blockquote?: MdastVisitorFn<Blockquote>;
    list?: MdastVisitorFn<List>;
    listItem?: MdastVisitorFn<ListItem>;
    html?: MdastVisitorFn<Html>;
    code?: MdastVisitorFn<Code>;
    definition?: MdastVisitorFn<Definition>;
    text?: MdastVisitorFn<Text>;
    emphasis?: MdastVisitorFn<Emphasis>;
    strong?: MdastVisitorFn<Strong>;
    inlineCode?: MdastVisitorFn<InlineCode>;
    break?: MdastVisitorFn<Break>;
    link?: MdastVisitorFn<Link>;
    image?: MdastVisitorFn<Image>;
    linkReference?: MdastVisitorFn<LinkReference>;
    imageReference?: MdastVisitorFn<ImageReference>;
    footnoteDefinition?: MdastVisitorFn<FootnoteDefinition>;
    footnoteReference?: MdastVisitorFn<FootnoteReference>;
    table?: MdastVisitorFn<Table>;
    tableRow?: MdastVisitorFn<TableRow>;
    tableCell?: MdastVisitorFn<TableCell>;
    delete?: MdastVisitorFn<Delete>;
    yaml?: MdastVisitorFn<Yaml>;
    toml?: MdastVisitorFn<Toml>;
    math?: MdastVisitorFn<MathNode>;
    inlineMath?: MdastVisitorFn<InlineMath>;
    containerDirective?: MdastVisitorFn<ContainerDirective>;
    leafDirective?: MdastVisitorFn<LeafDirective>;
    textDirective?: MdastVisitorFn<TextDirective>;
    superscript?: MdastVisitorFn<Superscript>;
    subscript?: MdastVisitorFn<Subscript>;
    descriptionList?: MdastVisitorFn<DescriptionList>;
    descriptionTerm?: MdastVisitorFn<DescriptionTerm>;
    descriptionDetails?: MdastVisitorFn<DescriptionDetails>;
    mdxJsxFlowElement?: MdastVisitorFn<MdxJsxFlowElement>;
    mdxJsxTextElement?: MdastVisitorFn<MdxJsxTextElement>;
    mdxFlowExpression?: MdastVisitorFn<MdxFlowExpression>;
    mdxTextExpression?: MdastVisitorFn<MdxTextExpression>;
    mdxjsEsm?: MdastVisitorFn<MdxjsEsm>;
    /** Fires for every user-defined node (any node created with a `type` outside
     *  the built-in set). Discriminate with `node.type`. */
    custom?: MdastVisitorFn<Custom>;
}
interface MdastVisitResult {
    /** Binary command buffer containing all mutations. */
    commandBuffer: Uint8Array;
    diagnostics: MdastDiagnostic[];
    hasMutations: boolean;
}
export type { MdastHandle };
interface MdastSubscription {
    nodeType: number;
    visitFn: (node: MdastNode, context: MdastVisitorContext) => unknown;
}
export declare function resolveMdastSubscriptions(plugin: MdastPluginInstance): MdastSubscription[];
/**
 * Walk an MDAST handle in Rust, dispatch matched nodes to JS visitor functions,
 * and apply mutations back to the handle. No arena buffers cross NAPI.
 *
 * Returns MdastVisitResult synchronously if all visitors are sync,
 * or Promise<MdastVisitResult> if any visitor is async.
 */
export declare function visitMdastHandle(handle: MdastHandle, plugin: MdastPluginInstance, subs: MdastSubscription[], source: string | (() => string), fileURL: URL | undefined, data?: Data, sourceFormat?: SourceFormat, diagnostics?: MdastDiagnostic[]): MdastVisitResult | Promise<MdastVisitResult>;
/** Its own pass so the caller can apply what `before` queued before the
 *  visitors walk, and the visitors' mutations before `after` reads the tree. */
export declare function visitMdastHook(handle: MdastHandle, plugin: MdastPluginInstance, hook: MdastHookFn, source: string | (() => string), fileURL: URL | undefined, data?: Data, sourceFormat?: SourceFormat, diagnostics?: MdastDiagnostic[]): MdastVisitResult | Promise<MdastVisitResult>;
