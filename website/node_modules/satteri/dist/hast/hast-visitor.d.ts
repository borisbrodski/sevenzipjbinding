import { type HastNode } from "./hast-materializer.js";
import type { HastRaw, Data, SourceFormat } from "../types.js";
import type { Element, Text, Comment, Doctype, Parents as HastParents, Root as HastRoot } from "hast";
import type { Program } from "estree-jsx";
import type { MdxJsxFlowElementHast, MdxJsxTextElementHast } from "../mdx-types.js";
import type { MdxFlowExpressionHast, MdxTextExpressionHast } from "../mdx-types.js";
import type { MdxjsEsmHast } from "../mdx-types.js";
import { type PluginOptions } from "../visitor-shared.js";
import type { HastHandle } from "../handles.js";
export type { HastHandle };
/** ESTree-compatible Program node returned by `parseExpression()`. */
export type EstreeProgram = Program;
export interface HastDiagnostic {
    message: string;
    nodeId?: number | undefined;
    severity: "error" | "warning" | "info";
}
export interface HastVisitorContext {
    readonly source: string;
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
    removeNode(node: Readonly<HastNode>): void;
    /**
     * Swap `node` for one node, or for an array of nodes placed in order at its
     * position. An empty array drops the node, the same as `removeNode`.
     * The document root takes a `root` and nothing else: the one place a `root`
     * is accepted as content.
     */
    replaceNode(node: Readonly<HastNode>, newNode: HastContent | HastContent[]): void;
    insertBefore(node: Readonly<HastNode>, newNode: HastContent | HastContent[]): void;
    insertAfter(node: Readonly<HastNode>, newNode: HastContent | HastContent[]): void;
    /**
     * Wrap `node` in `parentNode`, making it `parentNode`'s first child. Any
     * children `parentNode` declares are kept after it, so a `div` with an anchor
     * child wraps a heading as `div > [heading, anchor]`. `parentNode` is an
     * element, an MDX JSX element, or `{ raw }` HTML parsing to exactly one
     * element, never a void element, whose children would not render.
     */
    wrapNode(node: Readonly<HastNode>, parentNode: HastParentContent | RawHastContent | RawHtmlHastContent): void;
    prependChild(node: Readonly<HastNode>, childNode: HastContent | HastContent[]): void;
    appendChild(node: Readonly<HastNode>, childNode: HastContent | HastContent[]): void;
    /** Insert one node or an array at `index`; clamps (`0` or less prepends, past the end appends). */
    insertChildAt(node: Readonly<HastNode>, index: number, childNode: HastContent | HastContent[]): void;
    /** Remove the `index`-th child of `node`; a no-op when there is no such child. */
    removeChildAt(node: Readonly<HastNode>, index: number): void;
    setProperty(node: Readonly<HastNode>, key: string, value: unknown): void;
    /** Collect the concatenated text of all descendant text nodes (like DOM textContent). */
    textContent(node: Readonly<HastNode>): string;
    /**
     * The parent of a node, or `undefined` at the root. Within a pass the same
     * parent is always the same object, so visitors on sibling nodes can dedupe
     * by identity.
     */
    parent<N extends Exclude<HastNode, HastRoot>>(node: Readonly<N>): Readonly<HastParents>;
    parent(node: Readonly<HastNode>): Readonly<HastParents> | undefined;
    /**
     * Index of `node` within its parent's children, or `undefined` at the root.
     * Use this rather than `parent.children.indexOf(node)`, which won't find it.
     */
    indexOf(node: Readonly<HastNode>): number | undefined;
    report(opts: {
        message: string;
        node?: Readonly<HastNode>;
        severity?: "error" | "warning" | "info";
    }): void;
    getDiagnostics(): HastDiagnostic[];
}
/** New content for a HAST structural mutation. Unlike [`MdastContent`], HAST has
 *  a `raw` node type, so it needs no raw/rawHtml escape hatch. */
export type HastContent = HastNode;
/** A `wrapNode` wrapper: node types that can hold children. */
export type HastParentContent = Exclude<Extract<HastNode, {
    children: unknown[];
}>, HastRoot>;
/** Raw `wrapNode` wrapper: the HTML is parsed at apply time (not call time)
 *  and must yield exactly one non-void element, which becomes the wrapper.
 *  `mdxExpressions` is accepted for parity with the MDAST phase and has no
 *  effect: braces in HTML text are always literal. */
export interface RawHastContent {
    raw: string;
    mdxExpressions?: boolean;
}
export interface RawHtmlHastContent {
    /** @deprecated Use the equivalent `{ raw }`. */
    rawHtml: string;
}
/** A filtered visitor: Rust filters by tag/component name, only matched nodes cross the boundary. */
export interface HastFilteredVisitor<N extends HastNode = HastNode> {
    filter: string[];
    visit(node: Readonly<N>, ctx: HastVisitorContext): HastNode | void | Promise<HastNode | void>;
}
type HastVisitorFn<N extends HastNode = HastNode> = (node: Readonly<N>, ctx: HastVisitorContext) => HastNode | void | Promise<HastNode | void>;
export type HastHookFn = (root: Readonly<HastRoot>, ctx: HastVisitorContext) => void | Promise<void>;
export interface HastVisitorInstance {
    /** Plugin-level configuration (e.g. `{ position: true }` to read positions). */
    options?: PluginOptions;
    /** Runs once per document, before the plugin's visitors. Awaited when async. */
    before?: HastHookFn;
    /** Runs once per document, after the plugin's visitors have settled. Awaited
     *  when async. */
    after?: HastHookFn;
    element?: HastFilteredVisitor<Element> | HastFilteredVisitor<Element>[];
    mdxJsxFlowElement?: HastFilteredVisitor<MdxJsxFlowElementHast> | HastFilteredVisitor<MdxJsxFlowElementHast>[];
    mdxJsxTextElement?: HastFilteredVisitor<MdxJsxTextElementHast> | HastFilteredVisitor<MdxJsxTextElementHast>[];
    text?: HastVisitorFn<Text>;
    comment?: HastVisitorFn<Comment>;
    raw?: HastVisitorFn<HastRaw>;
    doctype?: HastVisitorFn<Doctype>;
    mdxFlowExpression?: HastVisitorFn<MdxFlowExpressionHast & {
        parseExpression(): EstreeProgram | null;
    }>;
    mdxTextExpression?: HastVisitorFn<MdxTextExpressionHast & {
        parseExpression(): EstreeProgram | null;
    }>;
    mdxjsEsm?: HastVisitorFn<MdxjsEsmHast & {
        parseExpression(): EstreeProgram | null;
    }>;
}
interface ResolvedSubscription {
    nodeType: number;
    tagFilter: string[];
    visitFn: (node: HastNode, ctx: HastVisitorContext) => HastNode | void;
}
export declare function resolveSubscriptions(plugin: HastVisitorInstance): ResolvedSubscription[];
/**
 * Walk a handle's arena in Rust, dispatch matched nodes to JS visitor functions,
 * and apply mutations back to the handle. No arena buffers cross NAPI.
 *
 * Returns the number of patches dropped because their target was removed or
 * replaced earlier in the same pass (the caller warns when non-zero), or a
 * Promise of that count if any visitor is async.
 */
export declare function visitHastHandle(handle: HastHandle, plugin: HastVisitorInstance, subs: ResolvedSubscription[], source: string | (() => string), fileURL: URL | undefined, data?: Data, sourceFormat?: SourceFormat, diagnostics?: HastDiagnostic[]): number | Promise<number>;
/** Run a HAST visitor, build the command buffer, but do NOT apply it. Returns
 *  the merged commands so the caller can choose how to dispatch: either via
 *  `applyCommandsToHandle` (intermediate plugins in a chain) or via a fused
 *  NAPI call like `applyCommandsAndRenderHandle` (final plugin, saves one
 *  apply + one render + one drop crossing). Empty result means no mutations.
 */
export declare function visitHastHandleCollect(handle: HastHandle, plugin: HastVisitorInstance, subs: ResolvedSubscription[], source: string | (() => string), fileURL: URL | undefined, data?: Data, sourceFormat?: SourceFormat, diagnostics?: HastDiagnostic[]): Uint8Array | Promise<Uint8Array>;
/** Its own pass so the caller can apply what `before` queued before the
 *  visitors walk, and the visitors' mutations before `after` reads the tree. */
export declare function visitHastHookCollect(handle: HastHandle, plugin: HastVisitorInstance, hook: HastHookFn, source: string | (() => string), fileURL: URL | undefined, data?: Data, sourceFormat?: SourceFormat, diagnostics?: HastDiagnostic[]): Uint8Array | Promise<Uint8Array>;
export declare function visitHastHook(handle: HastHandle, plugin: HastVisitorInstance, hook: HastHookFn, source: string | (() => string), fileURL: URL | undefined, data?: Data, sourceFormat?: SourceFormat, diagnostics?: HastDiagnostic[]): number | Promise<number>;
