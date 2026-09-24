import { LEAF_TYPES, isCustomLeaf, materializeNode } from "./mdast-materializer.js";
import { MdastReader } from "./mdast-reader.js";
import { acquireCommandBuffer, releaseCommandBuffer, classifyReturn, CommandBuffer, STRUCTURAL_CMD, } from "../command-buffer.js";
import { CMD_SET_CHILDREN } from "../generated/wire-constants.js";
import { ru32, rstr, readPosition } from "../wire-read.js";
import { decodeMdastTypeData } from "./generated/layout.js";
import { TYPE_NAMES, NAME_TO_TYPE, VISITOR_KEYS, MDAST_OPSTREAM_TYPES, } from "./generated/node-types.js";
import { OpWriter, OF_VALUE, OF_URL, OF_TITLE, OF_ALT, OF_LANG, OF_META, OF_IDENTIFIER, OF_LABEL, OF_NAME, OF_REFERENCE_TYPE, OF_DEPTH, OF_CHECKED, OF_START, OF_ORDERED, OF_SPREAD, OF_EXPLICIT, PROP_STRING, emitMdxAttr, } from "../op-stream.js";
import { walkMdastHandle, mdastTextContentHandle } from "#binding";
import { asArray, makeRequireNid, mergeAndReset, crossPipelineForeign, FOREIGN_REF, ROOT_NODE_ID, requireRootReplacement, rootReplacementError, unencodableContentError, } from "../visitor-shared.js";
import { LazyChildResolver, registerEpochCacheSlot, } from "../lazy-child-resolver.js";
import { MdastChildStub } from "./child-stub.js";
function nid(node, refs) {
    // Genuine stubs carry their id as a plain field; a spread copy is not
    // `instanceof` and has no `_nodeId`, so it correctly reads as new content.
    if (node instanceof MdastChildStub)
        return node._refs === refs ? node._id : FOREIGN_REF;
    const id = refs.get(node);
    if (id !== undefined)
        return id;
    // Plain objects are trusted only via this tree's refs or a NON-enumerable
    // `_nodeId` (the materializer's convention, which spread cannot copy) — an
    // enumerable one rode in on a copy and must read as new content.
    const d = Object.getOwnPropertyDescriptor(node, "_nodeId");
    if (d !== undefined && !d.enumerable)
        return FOREIGN_REF;
    return crossPipelineForeign(node);
}
const requireNid = makeRequireNid(nid);
export class MdastVisitorContext {
    #commandBuffer = acquireCommandBuffer();
    #diagnostics;
    #handle;
    #getSource;
    #resolver;
    #refs;
    /** One canonical object per parent id, so visitors can dedupe by identity.
     *  Null until the first `parent()` call; most passes never make one. */
    #parentsById = null;
    /**
     * The URL of the document being processed (the compile `fileURL` option),
     * or `undefined` when none was given. Use `fileURLToPath(ctx.fileURL)` for a
     * decoded filesystem path.
     */
    fileURL;
    /**
     * Document-level data bag, shared across every plugin in the compile and
     * across the mdast→hast phase boundary. Mutate keys directly
     * (`ctx.data.foo = x`); the bag itself isn't reassignable. Values are kept
     * on the JS side, so any value is allowed, including functions and class
     * instances. Returned to the caller as `result.data`.
     */
    data;
    /**
     * The source format this compile is processing: `"markdown"` for a plain
     * Markdown compile, `"mdx"` for an MDX one. Lets a plugin shared between both
     * pipelines branch on which it is handling.
     */
    sourceFormat;
    constructor(handle, getSource, fileURL, resolver, data, sourceFormat, diagnostics) {
        this.#handle = handle;
        this.#getSource = getSource;
        this.fileURL = fileURL;
        this.#resolver = resolver;
        this.#refs = resolver.refs;
        this.data = data;
        this.sourceFormat = sourceFormat;
        this.#diagnostics = diagnostics;
    }
    get source() {
        const value = this.#getSource();
        Object.defineProperty(this, "source", { value, writable: false, enumerable: true });
        return value;
    }
    removeNode(node) {
        this.#commandBuffer.removeNode(requireNid(node, "removeNode", this.#refs));
    }
    insertBefore(node, newNode) {
        const id = requireNid(node, "insertBefore", this.#refs);
        for (const n of asArray(newNode))
            emitMdastTree(this.#commandBuffer, "insertBefore", id, n, false, this.#refs);
    }
    insertAfter(node, newNode) {
        const id = requireNid(node, "insertAfter", this.#refs);
        for (const n of asArray(newNode))
            emitMdastTree(this.#commandBuffer, "insertAfter", id, n, false, this.#refs);
    }
    /**
     * Wrap `node` in `parentNode`, making it `parentNode`'s first child. Any
     * children `parentNode` declares are kept after it. `parentNode` must be a
     * node type that can hold children, or a raw string parsing to exactly one
     * such block (`{ raw: "> " }` wraps in a blockquote); to surround a node
     * with raw HTML tags, use `replaceNode(node, [openTag, node, closeTag])`
     * instead.
     */
    wrapNode(node, parentNode) {
        const id = requireNid(node, "wrapNode", this.#refs);
        assertMdastWrapParent(parentNode);
        emitMdastTree(this.#commandBuffer, "wrapNode", id, parentNode, false, this.#refs);
    }
    prependChild(node, childNode) {
        const id = requireNid(node, "prependChild", this.#refs);
        for (const n of asArray(childNode))
            emitMdastTree(this.#commandBuffer, "prependChild", id, n, false, this.#refs);
    }
    appendChild(node, childNode) {
        const id = requireNid(node, "appendChild", this.#refs);
        for (const n of asArray(childNode))
            emitMdastTree(this.#commandBuffer, "appendChild", id, n, false, this.#refs);
    }
    /** Insert one node or an array at `index`; clamps (`0` or less prepends, past the end appends). */
    insertChildAt(node, index, childNode) {
        const children = ("children" in node ? node.children : undefined) ?? [];
        if (index <= 0 || children.length === 0) {
            this.prependChild(node, childNode);
        }
        else if (index >= children.length) {
            this.appendChild(node, childNode);
        }
        else {
            this.insertBefore(children[index], childNode);
        }
    }
    /** Remove the `index`-th child of `node`; a no-op when there is no such child. */
    removeChildAt(node, index) {
        const child = "children" in node ? node.children?.[index] : undefined;
        if (child)
            this.removeNode(child);
    }
    /**
     * Swap `node` for one node, or for an array of nodes placed in order at its
     * position. An empty array drops the node, the same as `removeNode`.
     * The document root takes a `root`, the one place a `root` is accepted as
     * content, or a raw string, which parses to a root of its own.
     */
    replaceNode(node, newNode) {
        const id = requireNid(node, "replaceNode", this.#refs);
        if (Array.isArray(newNode)) {
            if (id === ROOT_NODE_ID && newNode.length > 1)
                throw rootReplacementError(newNode);
            // The last node carries the `replace` so refs back to the target still splice.
            let previous;
            for (const n of newNode) {
                if (previous !== undefined) {
                    emitMdastTree(this.#commandBuffer, "insertBefore", id, previous, false, this.#refs);
                }
                previous = n;
            }
            if (previous === undefined) {
                // Replacing with nothing drops the node, like removeNode.
                this.removeNode(node);
            }
            else if (id === ROOT_NODE_ID && !isRawMdastContent(previous)) {
                emitMdastRootReplace(this.#commandBuffer, requireRootReplacement(previous), this.#refs);
            }
            else {
                emitMdastTree(this.#commandBuffer, "replace", id, previous, true, this.#refs);
            }
            return;
        }
        if (id === ROOT_NODE_ID && !isRawMdastContent(newNode)) {
            emitMdastRootReplace(this.#commandBuffer, requireRootReplacement(newNode), this.#refs);
            return;
        }
        emitMdastTree(this.#commandBuffer, "replace", id, newNode, true, this.#refs);
    }
    setProperty(node, key, value) {
        if (key === "children") {
            // children is structural: set-children keeps the node and swaps only its
            // child list (reused children keep their id).
            const id = requireNid(node, "setProperty", this.#refs);
            if (!emitMdastChildrenCommand(this.#commandBuffer, id, value, this.#refs)) {
                throw unencodableContentError(value);
            }
            return;
        }
        if (key === "data") {
            // data is stored as JSON in the arena, serialize it for the command buffer
            this.#commandBuffer.setProperty(requireNid(node, "setProperty", this.#refs), key, value != null ? JSON.stringify(value) : null);
            return;
        }
        this.#commandBuffer.setProperty(requireNid(node, "setProperty", this.#refs), key, value);
    }
    /** Collect the concatenated text of all descendant text nodes (like mdast-util-to-string). */
    textContent(node, options) {
        return mdastTextContentHandle(this.#handle, requireNid(node, "textContent", this.#refs), options);
    }
    parent(node) {
        const parentId = this.#resolver.parentIdOf(requireNid(node, "parent", this.#refs));
        if (parentId === undefined)
            return undefined;
        const byId = (this.#parentsById ??= new Map());
        let parent = byId.get(parentId);
        if (parent === undefined) {
            parent = this.#resolver.materializeOne(parentId);
            byId.set(parentId, parent);
        }
        return parent;
    }
    /**
     * Index of `node` within its parent's children, or `undefined` at the root.
     * Use this rather than `parent.children.indexOf(node)`, which won't find it.
     */
    indexOf(node) {
        return this.#resolver.indexInParent(requireNid(node, "indexOf", this.#refs));
    }
    report({ message, node, severity = "error", }) {
        const id = node ? nid(node, this.#refs) : undefined;
        this.#diagnostics.push({
            message,
            nodeId: id === FOREIGN_REF ? undefined : id,
            position: node?.position,
            severity,
        });
    }
    /** Get the binary command buffer for all mutations recorded via context methods. */
    getCommandBuffer() {
        return this.#commandBuffer;
    }
    getDiagnostics() {
        return this.#diagnostics;
    }
}
const mdastSubscriptionCache = new WeakMap();
export function resolveMdastSubscriptions(plugin) {
    const cached = mdastSubscriptionCache.get(plugin);
    if (cached !== undefined)
        return cached.subs;
    const built = buildMdastSubscriptions(plugin);
    mdastSubscriptionCache.set(plugin, built);
    return built.subs;
}
function getMdastRustSubs(plugin) {
    const cached = mdastSubscriptionCache.get(plugin);
    if (cached !== undefined)
        return cached.rustSubs;
    const built = buildMdastSubscriptions(plugin);
    mdastSubscriptionCache.set(plugin, built);
    return built.rustSubs;
}
function buildMdastSubscriptions(plugin) {
    const subs = [];
    for (const [name, fn] of Object.entries(plugin)) {
        if (VISITOR_KEYS.has(name) && typeof fn === "function") {
            const nodeType = NAME_TO_TYPE[name];
            if (nodeType !== undefined) {
                subs.push({
                    nodeType,
                    visitFn: fn,
                });
            }
        }
    }
    const rustSubs = subs.map((s) => ({ nodeType: s.nodeType, tagFilter: [] }));
    return { subs, rustSubs };
}
const MDAST_EPOCH_CACHE = registerEpochCacheSlot(new WeakMap());
class MdastLazyChildResolver extends LazyChildResolver {
    cacheSlot() {
        return MDAST_EPOCH_CACHE;
    }
    createReader(wire) {
        return new MdastReader(wire);
    }
    materializeNode(reader, nodeId, refs) {
        return materializeNode(reader, nodeId, true, refs);
    }
    readParentId(reader, nodeId) {
        return reader.getParentId(nodeId);
    }
    readChildIds(reader, nodeId) {
        return reader.getChildIds(nodeId);
    }
}
/** Build the child-stub list for a matched node from the wire's `[child_ids]
 *  [child_types]` blocks, no arena snapshot. Stale ids are caught at
 *  materialization: the resolver's epoch check refuses a snapshot once the
 *  arena has mutated or been dropped. */
function readMdastChildStubs(view, buf, idsPos, typesPos, count, resolver) {
    // With a hot snapshot a stub's deferral buys nothing; real nodes skip its per-field getters.
    if (resolver.hasHotSnapshot()) {
        const nodes = new Array(count);
        for (let i = 0; i < count; i++) {
            nodes[i] = resolver.materializeOne(ru32(view, idsPos + i * 4));
        }
        return nodes;
    }
    const stubs = new Array(count);
    for (let i = 0; i < count; i++) {
        stubs[i] = new MdastChildStub(resolver, ru32(view, idsPos + i * 4), buf[typesPos + i]);
    }
    return stubs;
}
/** Install `children` as an own enumerable getter (spread must carry it),
 *  self-replacing with the one stable stub array on first read. One closure
 *  and one define per node — installing the wire locals as hidden slots
 *  instead measurably regressed every matching pipeline. */
function makeLazyChildren(node, view, buf, childIdsPos, childTypesPos, childCount, resolver) {
    Object.defineProperty(node, "children", {
        get() {
            const val = readMdastChildStubs(view, buf, childIdsPos, childTypesPos, childCount, resolver);
            Object.defineProperty(this, "children", {
                value: val,
                writable: true,
                enumerable: true,
                configurable: true,
            });
            return val;
        },
        enumerable: true,
        configurable: true,
    });
}
/**
 * Read an MDAST node from the inline data in a match buffer entry.
 *
 * Inline format (from Rust serialize_mdast_node_inline):
 *   [node_data: u32+bytes][position: 6×u32 = 24B][child_count: u32][child_ids: N×u32]
 *   [child_types: N×u8][type-specific data]
 */
function readMdastMatchedNode(view, buf, dataOffset, nodeId, nodeType, resolver) {
    let pos = dataOffset;
    const dataJsonLen = ru32(view, pos);
    pos += 4;
    let initialData = null;
    if (dataJsonLen > 0) {
        const jsonStr = rstr(buf, pos, dataJsonLen);
        try {
            initialData = JSON.parse(jsonStr);
        }
        catch (err) {
            if (process.env.NODE_ENV !== "production") {
                console.warn(`readMdastMatchedNode: malformed node_data for nodeId=${nodeId}`, err);
            }
        }
        pos += dataJsonLen;
    }
    const position = readPosition(view, pos);
    pos += 24;
    const childCount = ru32(view, pos);
    pos += 4;
    // Ids/types decode lazily with `.children` — most matched nodes never read them.
    const childIdsPos = pos;
    pos += childCount * 4;
    const childTypesPos = pos;
    pos += childCount;
    const typeName = TYPE_NAMES[nodeType] ?? `unknown(${nodeType})`;
    const node = { type: typeName };
    if (position !== undefined)
        node.position = position;
    if (childCount > 0) {
        makeLazyChildren(node, view, buf, childIdsPos, childTypesPos, childCount, resolver);
    }
    // Fixed-field types decode from the generated layout table; the rest
    // (variable-length / cross-field) stay in the hand-written switch.
    if (!decodeMdastTypeData(view, buf, pos, nodeType, node)) {
        switch (nodeType) {
            case 5: {
                // list
                node.start = ru32(view, pos);
                node.ordered = buf[pos + 4] !== 0;
                node.spread = buf[pos + 5] !== 0;
                if (!node.ordered)
                    node.start = null;
                break;
            }
            case 6: {
                // listItem
                const checked = buf[pos];
                node.checked = checked === 2 ? null : checked === 1;
                node.spread = buf[pos + 1] !== 0;
                break;
            }
            case 37: {
                // descriptionDetails
                node.spread = buf[pos] !== 0;
                break;
            }
            // table (21), directives (30/31/32) and mdxJsx elements (100/101) are
            // decoded by the generated `decodeMdastTypeData` from their tails.
            // root(0), paragraph(1), thematicBreak(3), blockquote(4), emphasis(11),
            // strong(12), break(14), tableRow(22), tableCell(23), delete(24): no extra data
        }
    }
    // User-defined node: the stored `name` field holds the author's public type
    // string. Surface it as `node.type` (open type string) instead of the
    // internal `"custom"`, drop the redundant `name`, and drop an empty `value`
    // so a parent node isn't given a spurious leaf field.
    if (nodeType === MDAST_CUSTOM) {
        node.type = node.name;
        delete node.name;
        if (node.value === "")
            delete node.value;
    }
    const leafCustom = nodeType === MDAST_CUSTOM && isCustomLeaf({ value: node.value, data: initialData }, childCount);
    if (childCount === 0 && !LEAF_TYPES.has(nodeType) && !leafCustom) {
        node.children = [];
    }
    resolver.refs.set(node, nodeId);
    if (initialData) {
        node.data = initialData;
    }
    return node;
}
const MDAST_ROOT = NAME_TO_TYPE.root;
/** Internal tag for user-defined nodes. The stored `name` field holds the
 *  author's public `type` string; the read paths surface it as `node.type`
 *  and the emit path routes any unrecognized `type` here. */
const MDAST_CUSTOM = NAME_TO_TYPE.custom;
/** The arena id of a node if it is an existing (materialized) node, else
 *  undefined for a freshly-built one. */
function reusedId(node, refs) {
    if (node === null || typeof node !== "object")
        return undefined;
    const id = nid(node, refs);
    return id !== undefined && id !== FOREIGN_REF ? id : undefined;
}
/** Emit a set-children command in place: a root-wrapped child list, the shape
 *  `Patch::SetChildren` splices in. Reused children become refs. */
function emitMdastChildrenCommand(buffer, id, children, refs) {
    if (!Array.isArray(children))
        return false;
    return buffer.emitOpstreamCommand(CMD_SET_CHILDREN, id, () => {
        buffer.open(MDAST_ROOT);
        for (const c of children) {
            if (!emitMdastOp(buffer, c, false, false, refs))
                return false;
        }
        buffer.close();
        return true;
    });
}
/** Separate from the per-node encoder, which rejects a `root` payload. */
function emitMdastRootReplace(buffer, root, refs) {
    const ok = buffer.emitOpstreamCommand(STRUCTURAL_CMD.replace, ROOT_NODE_ID, () => emitMdastRootOp(buffer, root, refs));
    if (!ok)
        throw unencodableContentError(root);
}
function emitMdastRootOp(w, n, refs) {
    w.open(MDAST_ROOT);
    if (n.data != null)
        w.data(n.data);
    if (n._keepChildren === true) {
        w.keepChildren();
    }
    else {
        const children = n.children;
        if (Array.isArray(children)) {
            for (const c of children)
                if (!emitMdastOp(w, c, false, true, refs))
                    return false;
        }
    }
    w.close();
    return true;
}
function emitMdastOp(w, node, isRoot, forReplace, refs) {
    if (node === null || typeof node !== "object")
        return false;
    if (!isRoot) {
        const id = reusedId(node, refs);
        if (id !== undefined) {
            w.ref(id);
            return true;
        }
    }
    const n = node;
    // Any `type` outside the built-in set is a user-defined node: route it to the
    // internal `custom` tag and carry the author's `type` string as the name.
    let type = MDAST_OPSTREAM_TYPES[n.type];
    let isCustom = false;
    if (type === undefined) {
        if (typeof n.type !== "string" || n.type.length === 0)
            return false;
        // A known built-in that just isn't op-stream-encodable (e.g. `root`) is a
        // real type used wrong, so fail loudly rather than reinterpreting it as a
        // user-defined node. Only genuinely-unknown type strings become custom.
        if (NAME_TO_TYPE[n.type] !== undefined)
            return false;
        type = MDAST_CUSTOM;
        isCustom = true;
    }
    else if (type === MDAST_CUSTOM) {
        // `"custom"` is the internal tag's own public name, so it resolves here
        // instead of falling through as unknown. Still a user-defined node, so carry
        // the `type` string as the name so it round-trips rather than vanishing.
        isCustom = true;
    }
    w.open(type);
    if (isCustom)
        w.str(OF_NAME, n.type);
    if (typeof n.value === "string")
        w.str(OF_VALUE, n.value);
    if (typeof n.url === "string")
        w.str(OF_URL, n.url);
    if (typeof n.title === "string")
        w.str(OF_TITLE, n.title);
    if (typeof n.alt === "string")
        w.str(OF_ALT, n.alt);
    if (typeof n.lang === "string")
        w.str(OF_LANG, n.lang);
    if (typeof n.meta === "string")
        w.str(OF_META, n.meta);
    if (typeof n.identifier === "string")
        w.str(OF_IDENTIFIER, n.identifier);
    if (typeof n.label === "string")
        w.str(OF_LABEL, n.label);
    if (typeof n.referenceType === "string")
        w.str(OF_REFERENCE_TYPE, n.referenceType);
    // Out-of-range numbers compile to null and the caller throws — a visible
    // error instead of silently masking the bits.
    if (typeof n.depth === "number") {
        if (!Number.isInteger(n.depth) || n.depth < 0 || n.depth > 255)
            return false;
        w.u8(OF_DEPTH, n.depth);
    }
    if (typeof n.checked === "boolean")
        w.u8(OF_CHECKED, n.checked ? 1 : 0);
    if (typeof n.start === "number") {
        if (!Number.isInteger(n.start) || n.start < 0 || n.start > 4294967295)
            return false;
        w.u32(OF_START, n.start);
    }
    if (typeof n.ordered === "boolean")
        w.bool(OF_ORDERED, n.ordered);
    if (typeof n.spread === "boolean")
        w.bool(OF_SPREAD, n.spread);
    if (!isCustom && typeof n.name === "string")
        w.str(OF_NAME, n.name);
    const attrs = n.attributes;
    if (Array.isArray(attrs)) {
        for (const a of attrs)
            emitMdxAttr(w, a);
    }
    else if (attrs !== null && typeof attrs === "object") {
        // Directive attributes: a string→string map; non-string values are
        // dropped, since the stored form holds only strings.
        for (const key in attrs) {
            const v = attrs[key];
            if (typeof v === "string")
                w.prop(key, PROP_STRING, v);
        }
    }
    if (Array.isArray(n.align))
        w.align(n.align.map(alignCode));
    if (n.data?._mdxExplicitJsx === true) {
        w.bool(OF_EXPLICIT, true);
    }
    if (n.data != null)
        w.data(n.data);
    if (isRoot && forReplace && n._keepChildren === true) {
        // Replace splices the target's original children, discarding any the
        // replacement declares.
        w.keepChildren();
    }
    else {
        // `_keepChildren` only applies to replace; other ops ignore the marker
        // and emit the declared children.
        const children = n.children;
        if (Array.isArray(children)) {
            for (const c of children)
                if (!emitMdastOp(w, c, false, forReplace, refs))
                    return false;
        }
    }
    w.close();
    return true;
}
/** Map a table `align` entry to its arena code (none=0). */
function alignCode(a) {
    return a === "left" ? 1 : a === "right" ? 2 : a === "center" ? 3 : 0;
}
/** True for the `{raw}` / `{rawHtml}` escape hatches — re-parsed by Rust rather
 *  than compiled to an op-stream, so they ride the RAW_MARKDOWN / RAW_HTML
 *  payloads instead of the declarative encoder. */
function isRawMdastContent(content) {
    const c = content;
    return typeof c.raw === "string" || typeof c.rawHtml === "string";
}
/** Encode `content` as the `op` structural command. Declarative nodes compile
 *  to the op-stream; the `{raw}`/`{rawHtml}` escape hatches ride the raw
 *  re-parse payloads. Anything that compiles to neither is a hard error — the
 *  op-stream is the only declarative encoding. The switches stay inline so the
 *  buffer calls are monomorphic (computed method names defeat inline caches on
 *  this warm path). */
function emitMdastTree(buffer, op, id, content, forReplace, refs) {
    if (isRawMdastContent(content)) {
        switch (op) {
            case "replace":
                return buffer.replace(id, content);
            case "insertBefore":
                return buffer.insertBefore(id, content);
            case "insertAfter":
                return buffer.insertAfter(id, content);
            case "prependChild":
                return buffer.prependChild(id, content);
            case "appendChild":
                return buffer.appendChild(id, content);
            case "wrapNode":
                return buffer.wrapNode(id, content);
        }
    }
    const ok = buffer.emitOpstreamCommand(STRUCTURAL_CMD[op], id, () => emitMdastOp(buffer, content, true, forReplace, refs));
    if (!ok)
        throw unencodableContentError(content);
}
/** A leaf wrapper would make the patch engine drop or displace the wrapped node. */
function assertMdastWrapParent(parentNode) {
    const sandwich = 'replaceNode(node, [{ type: "html", value: "<div>" }, node, { type: "html", value: "</div>" }])';
    // A raw wrapper is only a tree once Rust has parsed it, so it is checked there.
    if (isRawMdastContent(parentNode))
        return;
    const type = parentNode.type;
    const tag = typeof type === "string" ? NAME_TO_TYPE[type] : undefined;
    if (tag === undefined) {
        // User-defined type: only the declared shape distinguishes a parent from
        // a text leaf.
        if (Array.isArray(parentNode.children))
            return;
        throw new Error(`wrapNode: a user-defined "${String(type)}" wrapper must declare a children array. ` +
            "A leaf-shaped custom node renders as text and cannot hold the wrapped node.");
    }
    if (!LEAF_TYPES.has(tag))
        return;
    throw new Error(`wrapNode: "${String(type)}" nodes cannot hold children, so they cannot wrap a node. ` +
        'Pass a parent node such as { type: "blockquote", children: [] }, a raw wrapper ' +
        `such as { raw: "> " }, or surround the node with tags: ${sandwich}.`);
}
/** MDAST node types whose `value` Rust can set in place via setProperty. A
 *  visitor returning one of these as `{type, value}` with no other fields
 *  skips a full arena rebuild. */
const MDAST_VALUE_ONLY_TYPES = new Set([
    "text",
    "html",
    "inlineCode",
    "yaml",
    "toml",
    "inlineMath",
]);
/** True when the visitor returned a same-type MDAST node carrying only `type`
 *  + `value`. Any other field present (children, position, data, lang, meta)
 *  falls back to the full replace path so nothing is silently dropped. */
function isMdastTextValueSwap(result, original) {
    if (original === undefined)
        return false;
    if (result.type !== original.type)
        return false;
    if (!MDAST_VALUE_ONLY_TYPES.has(result.type))
        return false;
    const r = result;
    if (typeof r.value !== "string")
        return false;
    return (r.children === undefined &&
        r.position === undefined &&
        r.data === undefined &&
        r.lang === undefined &&
        r.meta === undefined);
}
/** A result that is the same object as the input node is a no-op, so context
 *  mutations (e.g. setProperty) are not clobbered. */
function applyMdastVisitResult(result, nodeId, returnBuffer, refs, originalNode) {
    if (result === undefined || result === null)
        return;
    if (result === originalNode)
        return;
    const cls = classifyReturn(result);
    switch (cls) {
        case "raw_markdown":
            returnBuffer.replace(nodeId, result);
            break;
        case "raw_html":
            returnBuffer.replace(nodeId, result);
            break;
        case "structured_node": {
            const node = result;
            if (isMdastTextValueSwap(node, originalNode)) {
                returnBuffer.setProperty(nodeId, "value", node.value);
                break;
            }
            emitMdastTree(returnBuffer, "replace", nodeId, node, true, refs);
            break;
        }
    }
}
/**
 * Walk an MDAST handle in Rust, dispatch matched nodes to JS visitor functions,
 * and apply mutations back to the handle. No arena buffers cross NAPI.
 *
 * Returns MdastVisitResult synchronously if all visitors are sync,
 * or Promise<MdastVisitResult> if any visitor is async.
 */
export function visitMdastHandle(handle, plugin, subs, source, fileURL, data = {}, sourceFormat = "markdown", diagnostics = []) {
    const getSource = typeof source === "function" ? source : () => source;
    const resolver = new MdastLazyChildResolver(handle);
    const context = new MdastVisitorContext(handle, getSource, fileURL, resolver, data, sourceFormat, diagnostics);
    const returnBuffer = acquireCommandBuffer();
    const rustSubs = getMdastRustSubs(plugin);
    const matchBuf = walkMdastHandle(handle, rustSubs);
    const matchView = new DataView(matchBuf.buffer, matchBuf.byteOffset, matchBuf.byteLength);
    const matchCount = ru32(matchView, 0);
    let deferred = null;
    for (let i = 0; i < matchCount; i++) {
        const indexBase = 4 + i * 10;
        const nodeId = ru32(matchView, indexBase);
        const subIndex = matchBuf[indexBase + 4];
        const dataOffset = ru32(matchView, indexBase + 6);
        const sub = subs[subIndex];
        const node = readMdastMatchedNode(matchView, matchBuf, dataOffset, nodeId, sub.nodeType, resolver);
        const result = sub.visitFn.call(plugin, node, context);
        if (result instanceof Promise) {
            deferred ??= [];
            deferred.push({ nodeId, promise: result, originalNode: node });
        }
        else {
            applyMdastVisitResult(result, nodeId, returnBuffer, resolver.refs, node);
        }
    }
    if (deferred) {
        return Promise.all(deferred.map((d) => d.promise.then((r) => ({ nodeId: d.nodeId, result: r, originalNode: d.originalNode })))).then((results) => {
            for (const { nodeId, result, originalNode } of results) {
                applyMdastVisitResult(result, nodeId, returnBuffer, resolver.refs, originalNode);
            }
            return finalizeMdastVisit(handle, context, returnBuffer);
        });
    }
    return finalizeMdastVisit(handle, context, returnBuffer);
}
const MDAST_ROOT_SUBS = [
    { nodeType: MDAST_ROOT, tagFilter: [] },
];
/** Its own pass so the caller can apply what `before` queued before the
 *  visitors walk, and the visitors' mutations before `after` reads the tree. */
export function visitMdastHook(handle, plugin, hook, source, fileURL, data = {}, sourceFormat = "markdown", diagnostics = []) {
    const getSource = typeof source === "function" ? source : () => source;
    const resolver = new MdastLazyChildResolver(handle);
    const context = new MdastVisitorContext(handle, getSource, fileURL, resolver, data, sourceFormat, diagnostics);
    const returnBuffer = acquireCommandBuffer();
    const matchBuf = walkMdastHandle(handle, MDAST_ROOT_SUBS);
    const matchView = new DataView(matchBuf.buffer, matchBuf.byteOffset, matchBuf.byteLength);
    if (ru32(matchView, 0) === 0)
        return finalizeMdastVisit(handle, context, returnBuffer);
    const root = readMdastMatchedNode(matchView, matchBuf, ru32(matchView, 10), ru32(matchView, 4), MDAST_ROOT, resolver);
    const result = hook.call(plugin, root, context);
    if (result instanceof Promise) {
        return result.then(() => finalizeMdastVisit(handle, context, returnBuffer));
    }
    return finalizeMdastVisit(handle, context, returnBuffer);
}
function finalizeMdastVisit(handle, context, returnBuffer) {
    const { merged, hasMutations } = mergeAndReset(returnBuffer, context);
    // Return both buffers to the pool. Bytes were copied into `merged` above.
    releaseCommandBuffer(returnBuffer);
    releaseCommandBuffer(context.getCommandBuffer());
    return { commandBuffer: merged, diagnostics: context.getDiagnostics(), hasMutations };
}
