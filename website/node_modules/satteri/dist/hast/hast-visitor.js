import { materializeHastNode } from "./hast-materializer.js";
import { HastReader, HAST_ROOT, HAST_ELEMENT, HAST_TEXT, HAST_COMMENT, HAST_RAW, HAST_MDX_JSX_ELEMENT, HAST_MDX_JSX_TEXT_ELEMENT, HAST_MDX_FLOW_EXPRESSION, HAST_MDX_TEXT_EXPRESSION, HAST_MDX_ESM, } from "./hast-reader.js";
import { TYPE_NAMES, NAME_TO_TYPE, VISITOR_KEYS, HAST_OPSTREAM_TYPES, } from "./generated/node-types.js";
import { acquireCommandBuffer, releaseCommandBuffer, CommandBuffer, STRUCTURAL_CMD, } from "../command-buffer.js";
import { CMD_SET_CHILDREN } from "../generated/wire-constants.js";
import { OpWriter, OF_VALUE, OF_TAGNAME, OF_NAME, OF_EXPLICIT, PROP_STRING, PROP_BOOL_TRUE, PROP_BOOL_FALSE, PROP_SPACE_SEP, PROP_INT, emitMdxAttr, } from "../op-stream.js";
import { restorePhantomSpaces } from "../phantom.js";
import { decodeMdxJsxAttr } from "../mdx-attr.js";
import { decodeElementProp } from "./element-props.js";
import { readPosition, rstr } from "../wire-read.js";
import { walkHandle, applyCommandsToHandle, textContentHandle, parseExpression as napiParseExpression, parseEsm as napiParseEsm, } from "#binding";
import { asArray, makeRequireNid, mergeAndReset, ROOT_NODE_ID, requireRootReplacement, rootReplacementError, crossPipelineForeign, FOREIGN_REF, unencodableContentError, } from "../visitor-shared.js";
import { LazyChildResolver, markHandleMutated, registerEpochCacheSlot, } from "../lazy-child-resolver.js";
import { HastChildStub } from "./child-stub.js";
/** Attach `parseExpression()` to an MDX expression or ESM node. */
function attachParseExpression(node, parseFn) {
    Object.defineProperty(node, "parseExpression", {
        value() {
            const value = this.value;
            if (typeof value !== "string")
                return null;
            const json = parseFn(value);
            if (json == null)
                return null;
            return JSON.parse(json);
        },
        writable: false,
        enumerable: false,
        configurable: true,
    });
}
/**
 * Arena identity of a node, rejecting impostors — the one place the
 * spread/identity invariant is enforced. A spread copy of a matched node or
 * stub must read as NEW content: trusting a copied id would splice the
 * original in as a ref and drop the copy's edits. Walk elements carry their
 * id in a private field behind `instanceof` (spread copies fail the check);
 * other walk-built nodes are keyed in the WeakMap (invisible to spread);
 * `HastChildStub`s (enumerable `_id`, but that key is ignored on plain
 * objects) are recognized by `instanceof`. Plain objects are trusted only via
 * the WeakMap or a NON-enumerable `_nodeId` (the materializers' convention,
 * which spread cannot copy).
 */
function nid(node, refs) {
    if (node instanceof WalkElement)
        return node._refs === refs ? node._nid : FOREIGN_REF;
    if (node instanceof HastChildStub)
        return node._refs === refs ? node._id : FOREIGN_REF;
    const id = refs.get(node);
    if (id !== undefined)
        return id;
    const d = Object.getOwnPropertyDescriptor(node, "_nodeId");
    if (d !== undefined && !d.enumerable)
        return FOREIGN_REF;
    return crossPipelineForeign(node);
}
const requireNid = makeRequireNid(nid);
/** `wrapNode` allowlist: an unlisted type fails loud instead of silently
 *  mis-wrapping. */
const HAST_PARENT_TYPES = ["element", "mdxJsxFlowElement", "mdxJsxTextElement"];
const HAST_PARENT_TYPE_SET = new Set(HAST_PARENT_TYPES);
/** A leaf wrapper would make the patch engine drop or displace the wrapped node. */
function assertHastWrapParent(parentNode) {
    const type = parentNode.type;
    if (typeof type === "string" && HAST_PARENT_TYPE_SET.has(type))
        return;
    throw new Error(`wrapNode: "${String(type)}" nodes cannot hold children, so they cannot wrap a node. ` +
        'Wrap in an element instead, e.g. { type: "element", tagName: "div", properties: {}, children: [] } ' +
        'or { raw: "<div></div>" }.');
}
function hastReusedId(node, refs) {
    if (node === null || typeof node !== "object")
        return undefined;
    const id = nid(node, refs);
    return id !== undefined && id !== FOREIGN_REF ? id : undefined;
}
/** Emit a set-children command in place: a root-wrapped child list, the shape
 *  `Patch::SetChildren` splices in. Reused children become refs. */
function emitHastChildrenCommand(buffer, id, children, refs) {
    if (!Array.isArray(children))
        return false;
    return buffer.emitOpstreamCommand(CMD_SET_CHILDREN, id, () => {
        buffer.open(HAST_ROOT);
        for (const c of children) {
            if (!emitHastOp(buffer, c, false, refs))
                return false;
        }
        buffer.close();
        return true;
    });
}
/** Encode `node` as the `op` structural command, emitting the op-stream
 *  payload directly into the command buffer (no intermediate copy). HAST
 *  content is always a declarative node (no raw escape hatch), so it
 *  compiles or it's a hard error. */
function emitHastTree(buffer, op, id, node, refs) {
    const ok = buffer.emitOpstreamCommand(STRUCTURAL_CMD[op], id, () => emitHastOp(buffer, node, true, refs));
    if (!ok)
        throw unencodableContentError(node);
}
/** Separate from the per-node encoder, which rejects a `root` payload. */
function emitHastRootReplace(buffer, root, refs) {
    const ok = buffer.emitOpstreamCommand(STRUCTURAL_CMD.replace, ROOT_NODE_ID, () => emitHastRootOp(buffer, root, refs));
    if (!ok)
        throw unencodableContentError(root);
}
function emitHastRootOp(w, n, refs) {
    w.open(HAST_ROOT);
    if (n.data != null)
        w.data(n.data);
    const children = n.children;
    if (Array.isArray(children)) {
        for (const c of children)
            if (!emitHastOp(w, c, false, refs))
                return false;
    }
    w.close();
    return true;
}
function emitHastOp(w, node, isRoot, refs) {
    if (node === null || typeof node !== "object")
        return false;
    if (!isRoot) {
        const id = hastReusedId(node, refs);
        if (id !== undefined) {
            w.ref(id);
            return true;
        }
    }
    const n = node;
    const type = HAST_OPSTREAM_TYPES[n.type];
    if (type === undefined)
        return false;
    w.open(type);
    if (type === HAST_ELEMENT) {
        w.str(OF_TAGNAME, typeof n.tagName === "string" ? n.tagName : "div");
        const props = n.properties;
        if (props !== null && typeof props === "object") {
            for (const key in props) {
                emitHastProp(w, key, props[key]);
            }
        }
    }
    else if (type === HAST_MDX_JSX_ELEMENT || type === HAST_MDX_JSX_TEXT_ELEMENT) {
        // Name falls back to tagName, matching `encode_hast_js_node_data`.
        const name = typeof n.name === "string" ? n.name : typeof n.tagName === "string" ? n.tagName : "";
        if (name !== "")
            w.str(OF_NAME, name);
        if (Array.isArray(n.attributes)) {
            for (const a of n.attributes)
                emitMdxAttr(w, a);
        }
        if (n.data?._mdxExplicitJsx === true) {
            w.bool(OF_EXPLICIT, true);
        }
    }
    else {
        w.str(OF_VALUE, typeof n.value === "string" ? n.value : "");
    }
    if (n.data != null)
        w.data(n.data);
    const children = n.children;
    if (Array.isArray(children)) {
        for (const c of children)
            if (!emitHastOp(w, c, false, refs))
                return false;
    }
    w.close();
    return true;
}
/** Emit one element property, mirroring `encode_hast_js_node_data` exactly:
 *  bool/string/number/array → kind; null/object → skip. */
function emitHastProp(w, name, value) {
    if (value === true)
        w.prop(name, PROP_BOOL_TRUE, "");
    else if (value === false)
        w.prop(name, PROP_BOOL_FALSE, "");
    else if (typeof value === "string")
        w.prop(name, PROP_STRING, value);
    else if (typeof value === "number")
        w.prop(name, PROP_INT, String(value));
    else if (Array.isArray(value))
        w.prop(name, PROP_SPACE_SEP, value.filter((v) => typeof v === "string").join(" "));
}
class HastVisitorContextImpl {
    #commandBuffer = acquireCommandBuffer();
    #diagnostics;
    /** Track accumulated node state for multiple setProperty calls on the same node. */
    #pendingNodes = new Map();
    #handle;
    #getSource;
    #resolver;
    #refs;
    /** One canonical object per parent id, so visitors can dedupe by identity.
     *  Null until the first `parent()` call; most passes never make one. */
    #parentsById = null;
    fileURL;
    data;
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
    replaceNode(node, newNode) {
        const id = requireNid(node, "replaceNode", this.#refs);
        if (Array.isArray(newNode)) {
            if (id === ROOT_NODE_ID && newNode.length > 1)
                throw rootReplacementError(newNode);
            // The last node carries the `replace` so refs back to the target still splice.
            let previous;
            for (const n of newNode) {
                if (previous !== undefined)
                    emitHastTree(this.#commandBuffer, "insertBefore", id, previous, this.#refs);
                previous = n;
            }
            if (previous === undefined) {
                // Replacing with nothing drops the node, like removeNode.
                this.removeNode(node);
            }
            else if (id === ROOT_NODE_ID) {
                emitHastRootReplace(this.#commandBuffer, requireRootReplacement(previous), this.#refs);
            }
            else {
                emitHastTree(this.#commandBuffer, "replace", id, previous, this.#refs);
            }
            // A stale queued replacement would win: setProperty folds into it, landing last.
            this.#pendingNodes.delete(id);
            return;
        }
        if (id === ROOT_NODE_ID) {
            emitHastRootReplace(this.#commandBuffer, requireRootReplacement(newNode), this.#refs);
            return;
        }
        emitHastTree(this.#commandBuffer, "replace", id, newNode, this.#refs);
        // Track the replacement so a later mdxJsx setProperty can fold into it.
        this.#pendingNodes.set(id, newNode);
    }
    insertBefore(node, newNode) {
        const id = requireNid(node, "insertBefore", this.#refs);
        for (const n of asArray(newNode))
            emitHastTree(this.#commandBuffer, "insertBefore", id, n, this.#refs);
    }
    insertAfter(node, newNode) {
        const id = requireNid(node, "insertAfter", this.#refs);
        for (const n of asArray(newNode))
            emitHastTree(this.#commandBuffer, "insertAfter", id, n, this.#refs);
    }
    wrapNode(node, parentNode) {
        const id = requireNid(node, "wrapNode", this.#refs);
        if (typeof parentNode.raw === "string" ||
            typeof parentNode.rawHtml === "string") {
            this.#commandBuffer.wrapNode(id, parentNode);
            return;
        }
        assertHastWrapParent(parentNode);
        emitHastTree(this.#commandBuffer, "wrapNode", id, parentNode, this.#refs);
    }
    prependChild(node, childNode) {
        const id = requireNid(node, "prependChild", this.#refs);
        for (const n of asArray(childNode))
            emitHastTree(this.#commandBuffer, "prependChild", id, n, this.#refs);
    }
    appendChild(node, childNode) {
        const id = requireNid(node, "appendChild", this.#refs);
        for (const n of asArray(childNode))
            emitHastTree(this.#commandBuffer, "appendChild", id, n, this.#refs);
    }
    insertChildAt(node, index, childNode) {
        const children = "children" in node ? node.children : [];
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
    removeChildAt(node, index) {
        const child = "children" in node ? node.children[index] : undefined;
        if (child)
            this.removeNode(child);
    }
    setProperty(node, key, value) {
        const id = requireNid(node, "setProperty", this.#refs);
        if (key === "children") {
            // children is structural: set-children keeps the node and swaps only its
            // child list (reused children keep their id).
            if (!emitHastChildrenCommand(this.#commandBuffer, id, value, this.#refs)) {
                throw unencodableContentError(value);
            }
            return;
        }
        if (key === "data") {
            this.#commandBuffer.setProperty(id, key, value != null ? JSON.stringify(value) : null);
            return;
        }
        if (node.type === "element") {
            this.#commandBuffer.setProperty(id, key, value);
            return;
        }
        if (node.type === "mdxJsxFlowElement" || node.type === "mdxJsxTextElement") {
            // MDX JSX nodes carry `attributes`, not `properties`. If a replacement is
            // already queued for this node, fold the attribute into it so the change
            // survives the rebuild. This spreads the queued replacement object, not
            // the matched node, so it never forces the matched node's children to
            // materialize.
            const pending = this.#pendingNodes.get(id);
            if (pending !== undefined) {
                const updated = { ...pending };
                const attrs = [...(updated.attributes ?? [])];
                const idx = attrs.findIndex((a) => a.type === "mdxJsxAttribute" && a.name === key);
                if (idx !== -1)
                    attrs.splice(idx, 1);
                // Arrays space-join, matching the binary path's PROP_SPACE_SEP encoding
                // (hast convention for list-valued properties like className).
                const attrValue = value === true || value === null || value === undefined
                    ? null
                    : typeof value === "string"
                        ? value
                        : Array.isArray(value)
                            ? value.join(" ")
                            : String(value);
                attrs.push({ type: "mdxJsxAttribute", name: key, value: attrValue });
                updated.attributes = attrs;
                this.replaceNode(node, updated);
                return;
            }
            // Binary attribute upsert in the arena's type_data — no child
            // materialization. Rust maps the value-type to a boolean (true/null) or
            // literal (string/number/false) attribute, mirroring the fold path above.
            this.#commandBuffer.setProperty(id, key, value);
            return;
        }
        // Text-like nodes (text, comment, raw, expressions, esm): Rust handles
        // `value` directly on these types.
        this.#commandBuffer.setProperty(id, key, value);
    }
    textContent(node) {
        return textContentHandle(this.#handle, requireNid(node, "textContent", this.#refs));
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
    indexOf(node) {
        return this.#resolver.indexInParent(requireNid(node, "indexOf", this.#refs));
    }
    report({ message, node, severity = "error", }) {
        const id = node ? nid(node, this.#refs) : undefined;
        this.#diagnostics.push({
            message,
            nodeId: id === FOREIGN_REF ? undefined : id,
            severity,
        });
    }
    getCommandBuffer() {
        return this.#commandBuffer;
    }
    getDiagnostics() {
        return this.#diagnostics;
    }
}
/** Node types that use filtered visitors (have tag/component names). */
const FILTERED_METHODS = new Set(["element", "mdxJsxFlowElement", "mdxJsxTextElement"]);
const subscriptionCache = new WeakMap();
export function resolveSubscriptions(plugin) {
    const cached = subscriptionCache.get(plugin);
    if (cached !== undefined)
        return cached.subs;
    const built = buildSubscriptions(plugin);
    subscriptionCache.set(plugin, built);
    return built.subs;
}
/** Get the (cached) Rust-side projection of `subs` that strips visitFn so it
 *  can cross NAPI. Computed once per plugin object alongside `subs`. */
function getRustSubs(plugin) {
    const cached = subscriptionCache.get(plugin);
    if (cached !== undefined)
        return cached.rustSubs;
    const built = buildSubscriptions(plugin);
    subscriptionCache.set(plugin, built);
    return built.rustSubs;
}
function isFilteredVisitor(value) {
    return (value !== null &&
        typeof value === "object" &&
        Array.isArray(value.filter) &&
        typeof value.visit === "function");
}
/** Caught pre-wire so the failure names the API shape, not the internal `tagFilter` field. */
function malformedFilteredVisitorError(plugin, methodName) {
    const name = plugin.name;
    const pluginName = typeof name === "string" && name !== "" ? name : "(unnamed)";
    return new Error(`hast plugin "${pluginName}": "${methodName}" visitors filter by tag/component name, ` +
        `so each must be an object { filter: string[], visit: function } (or an array of those). ` +
        `Use filter: [] to visit every "${methodName}" node.`);
}
function buildSubscriptions(plugin) {
    const subs = [];
    for (const [methodName, nodeType] of Object.entries(METHOD_TO_TYPE)) {
        const value = plugin[methodName];
        if (value === undefined)
            continue;
        if (FILTERED_METHODS.has(methodName)) {
            const items = Array.isArray(value) ? value : [value];
            for (const fv of items) {
                if (!isFilteredVisitor(fv))
                    throw malformedFilteredVisitorError(plugin, methodName);
                subs.push({
                    nodeType,
                    tagFilter: fv.filter,
                    visitFn: fv.visit,
                });
            }
        }
        else {
            // Bare function, empty filter matches all nodes of this type
            subs.push({ nodeType, tagFilter: [], visitFn: value });
        }
    }
    const rustSubs = subs.map((s) => ({ nodeType: s.nodeType, tagFilter: s.tagFilter }));
    return { subs, rustSubs };
}
/** Visitor method name → node-type tag (method names are the subscribable AST names). */
const METHOD_TO_TYPE = Object.fromEntries([...VISITOR_KEYS].map((name) => [name, NAME_TO_TYPE[name]]));
function decodeProperties(view, buf, pos) {
    const propCount = view.getUint16(pos, true);
    pos += 2;
    const properties = {};
    for (let i = 0; i < propCount; i++) {
        const nameLen = view.getUint16(pos, true);
        pos += 2;
        const name = rstr(buf, pos, nameLen);
        pos += nameLen;
        const kind = buf[pos];
        pos += 1;
        const valLen = view.getUint16(pos, true);
        pos += 2;
        const valStr = rstr(buf, pos, valLen);
        pos += valLen;
        properties[name] = decodeElementProp(kind, valStr);
    }
    return properties;
}
/** Build the child-stub list for a matched node from the wire's `[child_ids]
 *  [child_types]` blocks, no arena snapshot. Stale ids are caught at
 *  materialization: the resolver's epoch check refuses a snapshot once the
 *  arena has mutated or been dropped. */
function readChildStubs(view, buf, idsPos, typesPos, count, resolver) {
    // With a hot snapshot a stub's deferral buys nothing; real nodes skip its per-field getters.
    if (resolver.hasHotSnapshot()) {
        const nodes = new Array(count);
        for (let i = 0; i < count; i++) {
            nodes[i] = resolver.materializeOne(view.getUint32(idsPos + i * 4, true));
        }
        return nodes;
    }
    const stubs = new Array(count);
    for (let i = 0; i < count; i++) {
        stubs[i] = new HastChildStub(resolver, view.getUint32(idsPos + i * 4, true), buf[typesPos + i]);
    }
    return stubs;
}
// Shared own-getter descriptors for WalkElement's lazy fields, populated in
// its static block so the getters can read the private wire fields.
let WALK_PROPS_DESC;
let WALK_CHILDREN_DESC;
/**
 * Walk-path element. Spread-correctness requires `properties`/`children` as
 * own enumerable keys (`{ ...node }` copies nothing else), but construction
 * runs per matched element, so everything stays off the expensive paths:
 * wire state in private fields (plain stores, invisible to spread — a WeakMap
 * entry per element caused major-GC ephemeron stalls at this volume), shared
 * getter functions instead of per-node closures, at most one define per lazy
 * field, and `instanceof` gating identity so copies read as new content.
 */
class WalkElement {
    type = "element";
    tagName;
    #nodeId;
    #wire;
    #propsPos;
    #childIdsPos;
    #childTypesPos;
    #childCount;
    constructor(tagName, nodeId, wire, propsPos, propCount, childIdsPos, childTypesPos, childCount) {
        this.tagName = tagName;
        this.#nodeId = nodeId;
        this.#wire = wire;
        this.#propsPos = propsPos;
        this.#childIdsPos = childIdsPos;
        this.#childTypesPos = childTypesPos;
        this.#childCount = childCount;
        if (propCount === 0) {
            this.properties = {};
        }
        else {
            Object.defineProperty(this, "properties", WALK_PROPS_DESC);
        }
        if (childCount === 0) {
            this.children = [];
        }
        else {
            Object.defineProperty(this, "children", WALK_CHILDREN_DESC);
        }
    }
    /** @internal */
    get _nid() {
        return this.#nodeId;
    }
    /** @internal */
    get _refs() {
        return this.#wire.resolver.refs;
    }
    static {
        WALK_PROPS_DESC = {
            enumerable: true,
            configurable: true,
            get() {
                const w = this.#wire;
                const val = decodeProperties(w.view, w.buf, this.#propsPos);
                Object.defineProperty(this, "properties", {
                    value: val,
                    writable: true,
                    enumerable: true,
                    configurable: true,
                });
                return val;
            },
        };
        WALK_CHILDREN_DESC = {
            enumerable: true,
            configurable: true,
            get() {
                const w = this.#wire;
                const val = readChildStubs(w.view, w.buf, this.#childIdsPos, this.#childTypesPos, this.#childCount, w.resolver);
                Object.defineProperty(this, "children", {
                    value: val,
                    writable: true,
                    enumerable: true,
                    configurable: true,
                });
                return val;
            },
        };
    }
}
/** Read the tail of a matched element node (tag + properties).
 *  Common prelude (data/position/children) is already consumed by `readMatchedNode`. */
function readElementFromBinary(wire, offset, nodeId, position, childIdsPos, childTypesPos, childCount, data) {
    let pos = offset;
    // Eager: tagName (almost always accessed by visitors)
    const tagLen = wire.view.getUint16(pos, true);
    pos += 2;
    const tagName = rstr(wire.buf, pos, tagLen);
    pos += tagLen;
    const propCount = wire.view.getUint16(pos, true);
    const node = new WalkElement(tagName, nodeId, wire, pos, propCount, childIdsPos, childTypesPos, childCount);
    if (position !== undefined)
        node.position = position;
    if (data !== null)
        node.data = data;
    return node;
}
/** Value-carrying types read by `readTextFromBinary` (tag → AST name). */
const TEXT_NODE_TYPES = Object.fromEntries(["text", "comment", "raw", "mdxFlowExpression", "mdxTextExpression", "mdxjsEsm"].map((name) => [NAME_TO_TYPE[name], name]));
function readTextFromBinary(view, buf, offset, nodeId, nodeType, position, data, refs) {
    const valLen = view.getUint32(offset, true);
    const rawValue = rstr(buf, offset + 4, valLen);
    // MDX flow/text expressions store phantom-space sentinels; restore them so
    // the value matches the reader path. ESM and plain text keep their value.
    const value = nodeType === HAST_MDX_FLOW_EXPRESSION || nodeType === HAST_MDX_TEXT_EXPRESSION
        ? restorePhantomSpaces(rawValue)
        : rawValue;
    const base = { type: TEXT_NODE_TYPES[nodeType], value };
    if (position !== undefined)
        base.position = position;
    if (data !== null)
        base.data = data;
    const node = base;
    refs.set(node, nodeId);
    if (nodeType === HAST_MDX_FLOW_EXPRESSION || nodeType === HAST_MDX_TEXT_EXPRESSION) {
        attachParseExpression(node, napiParseExpression);
    }
    else if (nodeType === HAST_MDX_ESM) {
        attachParseExpression(node, napiParseEsm);
    }
    return node;
}
function readMdxJsxFromBinary(view, buf, offset, nodeId, nodeType, resolver, position, childIdsPos, childTypesPos, childCount, data) {
    let pos = offset;
    const nameLen = view.getUint16(pos, true);
    pos += 2;
    const name = nameLen > 0 ? rstr(buf, pos, nameLen) : null;
    pos += nameLen;
    // Attributes: [kind: u8][nameLen: u16][name][valLen: u32][val]
    const attrCount = view.getUint16(pos, true);
    pos += 2;
    const attributes = [];
    for (let i = 0; i < attrCount; i++) {
        const kind = buf[pos];
        pos += 1;
        const attrNameLen = view.getUint16(pos, true);
        pos += 2;
        const attrName = rstr(buf, pos, attrNameLen);
        pos += attrNameLen;
        const attrValLen = view.getUint32(pos, true);
        pos += 4;
        const attrVal = rstr(buf, pos, attrValLen);
        pos += attrValLen;
        attributes.push(decodeMdxJsxAttr(kind, attrName, attrVal));
    }
    const typeName = nodeType === HAST_MDX_JSX_ELEMENT ? "mdxJsxFlowElement" : "mdxJsxTextElement";
    const base = { type: typeName, name, attributes };
    if (position !== undefined)
        base.position = position;
    if (data !== null)
        base.data = data;
    resolver.refs.set(base, nodeId);
    makeLazyChildren(base, view, buf, childIdsPos, childTypesPos, childCount, resolver);
    return base;
}
function readMatchedNode(wire, offset, nodeId, nodeType) {
    const { view, buf, resolver } = wire;
    let pos = offset;
    // Shared prelude (matches serialize_hast_node_inline / serialize_mdast_node_inline):
    //   [data_len: u32][data_bytes][position: 24B][child_count: u32][child_ids: N×u32][child_types: N×u8]
    const dataLen = view.getUint32(pos, true);
    pos += 4;
    let data = null;
    if (dataLen > 0) {
        const jsonStr = rstr(buf, pos, dataLen);
        try {
            data = JSON.parse(jsonStr);
        }
        catch (err) {
            if (process.env.NODE_ENV !== "production") {
                console.warn(`readMatchedNode: malformed node_data for nodeId=${nodeId}`, err);
            }
        }
        pos += dataLen;
    }
    const position = readPosition(view, pos);
    pos += 24;
    const childCount = view.getUint32(pos, true);
    pos += 4;
    // Ids/types decode lazily with `.children` — most matched nodes never read them.
    const childIdsPos = pos;
    pos += childCount * 4;
    const childTypesPos = pos;
    pos += childCount;
    // Dispatch to type-specific tail (pos now sits at the type-specific section)
    if (nodeType === HAST_ELEMENT) {
        return readElementFromBinary(wire, pos, nodeId, position, childIdsPos, childTypesPos, childCount, data);
    }
    else if (nodeType === HAST_TEXT ||
        nodeType === HAST_COMMENT ||
        nodeType === HAST_RAW ||
        nodeType === HAST_MDX_FLOW_EXPRESSION ||
        nodeType === HAST_MDX_TEXT_EXPRESSION ||
        nodeType === HAST_MDX_ESM) {
        return readTextFromBinary(view, buf, pos, nodeId, nodeType, position, data, resolver.refs);
    }
    else if (nodeType === HAST_MDX_JSX_ELEMENT || nodeType === HAST_MDX_JSX_TEXT_ELEMENT) {
        return readMdxJsxFromBinary(view, buf, pos, nodeId, nodeType, resolver, position, childIdsPos, childTypesPos, childCount, data);
    }
    // Fallback: root and doctype.
    const base = { type: TYPE_NAMES[nodeType] ?? `unknown(${nodeType})` };
    if (position !== undefined)
        base.position = position;
    if (data !== null)
        base.data = data;
    if (nodeType === HAST_ROOT) {
        // `...root.children` has to work in a hook.
        if (childCount > 0) {
            makeLazyChildren(base, view, buf, childIdsPos, childTypesPos, childCount, resolver);
        }
        else {
            base.children = [];
        }
    }
    const node = base;
    resolver.refs.set(node, nodeId);
    return node;
}
const HAST_EPOCH_CACHE = registerEpochCacheSlot(new WeakMap());
class HastLazyChildResolver extends LazyChildResolver {
    cacheSlot() {
        return HAST_EPOCH_CACHE;
    }
    createReader(wire) {
        return new HastReader(wire);
    }
    materializeNode(reader, nodeId, refs) {
        return materializeHastNode(reader, nodeId, true, refs);
    }
    readParentId(reader, nodeId) {
        return reader.getParentId(nodeId);
    }
    readChildIds(reader, nodeId) {
        return reader.getChildIds(nodeId);
    }
}
/** Install `children` as an own enumerable getter (spread must carry it),
 *  self-replacing with the one stable stub array on first read. One closure
 *  and one define per node — installing the wire locals as hidden slots
 *  instead measurably regressed every matching pipeline. */
function makeLazyChildren(node, view, buf, childIdsPos, childTypesPos, childCount, resolver) {
    Object.defineProperty(node, "children", {
        get() {
            const val = readChildStubs(view, buf, childIdsPos, childTypesPos, childCount, resolver);
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
/** A result that is the same object as the input node is a no-op, so context
 *  mutations (e.g. setProperty) are not clobbered.
 *
 *  A same-type text-like node carrying only a new `value` becomes a
 *  setProperty("value") rather than a structural replace, which would force
 *  the arena into a full rebuild for a shape that didn't change. */
function applyHastVisitResult(result, nodeId, returnBuffer, originalNode, refs) {
    if (result == null)
        return;
    if (result === originalNode)
        return;
    if (isTextValueSwap(result, originalNode)) {
        returnBuffer.setProperty(nodeId, "value", result.value);
        return;
    }
    emitHastTree(returnBuffer, "replace", nodeId, result, refs);
}
function handleVisitResult(result, nodeId, returnBuffer, deferred, originalNode, refs) {
    if (result instanceof Promise) {
        const list = deferred ?? [];
        list.push({ nodeId, promise: result, originalNode });
        return list;
    }
    applyHastVisitResult(result, nodeId, returnBuffer, originalNode, refs);
    return deferred;
}
/** True when `result` is a same-type text-like node carrying only `type` +
 *  `value`. The explicit `=== undefined` checks avoid the array alloc of
 *  `Object.keys().length` on this per-text-node hot path. */
function isTextValueSwap(result, original) {
    if (result.type !== original.type)
        return false;
    if (result.type !== "text" && result.type !== "comment" && result.type !== "raw")
        return false;
    const r = result;
    if (typeof r.value !== "string")
        return false;
    return (r.children === undefined &&
        r.position === undefined &&
        r.data === undefined &&
        r.tagName === undefined &&
        r.properties === undefined &&
        r.name === undefined &&
        r.attributes === undefined);
}
function dispatchMatches(matchBuf, subs, ctx, returnBuffer, resolver) {
    const matchView = new DataView(matchBuf.buffer, matchBuf.byteOffset, matchBuf.byteLength);
    const matchCount = matchView.getUint32(0, true);
    const wire = { view: matchView, buf: matchBuf, resolver };
    let deferred = null;
    for (let i = 0; i < matchCount; i++) {
        const indexBase = 4 + i * 10;
        const nodeId = matchView.getUint32(indexBase, true);
        const subIndex = matchBuf[indexBase + 4];
        const dataOffset = matchView.getUint32(indexBase + 6, true);
        const sub = subs[subIndex];
        const node = readMatchedNode(wire, dataOffset, nodeId, sub.nodeType);
        const result = sub.visitFn(node, ctx);
        deferred = handleVisitResult(result, nodeId, returnBuffer, deferred, node, resolver.refs);
    }
    return deferred;
}
/**
 * Walk a handle's arena in Rust, dispatch matched nodes to JS visitor functions,
 * and apply mutations back to the handle. No arena buffers cross NAPI.
 *
 * Returns the number of patches dropped because their target was removed or
 * replaced earlier in the same pass (the caller warns when non-zero), or a
 * Promise of that count if any visitor is async.
 */
export function visitHastHandle(handle, plugin, subs, source, fileURL, data = {}, sourceFormat = "markdown", diagnostics = []) {
    const result = visitHastHandleCollect(handle, plugin, subs, source, fileURL, data, sourceFormat, diagnostics);
    if (result instanceof Promise) {
        return result.then((commands) => applyCollectedCommands(handle, commands));
    }
    return applyCollectedCommands(handle, result);
}
/** Apply commands collected by `visitHastHandleCollect`; returns the number of
 *  patches dropped as stranded (0 when none). */
function applyCollectedCommands(handle, commands) {
    if (commands.length === 0)
        return 0;
    markHandleMutated(handle);
    return applyCommandsToHandle(handle, commands);
}
/** Run a HAST visitor, build the command buffer, but do NOT apply it. Returns
 *  the merged commands so the caller can choose how to dispatch: either via
 *  `applyCommandsToHandle` (intermediate plugins in a chain) or via a fused
 *  NAPI call like `applyCommandsAndRenderHandle` (final plugin, saves one
 *  apply + one render + one drop crossing). Empty result means no mutations.
 */
export function visitHastHandleCollect(handle, plugin, subs, source, fileURL, data = {}, sourceFormat = "markdown", diagnostics = []) {
    const getSource = typeof source === "function" ? source : () => source;
    const resolver = new HastLazyChildResolver(handle);
    const ctx = new HastVisitorContextImpl(handle, getSource, fileURL, resolver, data, sourceFormat, diagnostics);
    const returnBuffer = acquireCommandBuffer();
    const rustSubs = getRustSubs(plugin);
    const deferred = dispatchMatches(walkHandle(handle, rustSubs), subs, ctx, returnBuffer, resolver);
    if (deferred) {
        return Promise.all(deferred.map((d) => d.promise.then((result) => ({ nodeId: d.nodeId, result, originalNode: d.originalNode })))).then((results) => {
            for (const { nodeId, result, originalNode } of results) {
                applyHastVisitResult(result, nodeId, returnBuffer, originalNode, resolver.refs);
            }
            return collectCommands(returnBuffer, ctx);
        });
    }
    return collectCommands(returnBuffer, ctx);
}
const HAST_ROOT_SUBS = [
    { nodeType: HAST_ROOT, tagFilter: [] },
];
/** Its own pass so the caller can apply what `before` queued before the
 *  visitors walk, and the visitors' mutations before `after` reads the tree. */
export function visitHastHookCollect(handle, plugin, hook, source, fileURL, data = {}, sourceFormat = "markdown", diagnostics = []) {
    const getSource = typeof source === "function" ? source : () => source;
    const resolver = new HastLazyChildResolver(handle);
    const ctx = new HastVisitorContextImpl(handle, getSource, fileURL, resolver, data, sourceFormat, diagnostics);
    const returnBuffer = acquireCommandBuffer();
    const matchBuf = walkHandle(handle, HAST_ROOT_SUBS);
    const matchView = new DataView(matchBuf.buffer, matchBuf.byteOffset, matchBuf.byteLength);
    if (matchView.getUint32(0, true) === 0)
        return collectCommands(returnBuffer, ctx);
    const wire = { view: matchView, buf: matchBuf, resolver };
    const root = readMatchedNode(wire, matchView.getUint32(10, true), matchView.getUint32(4, true), HAST_ROOT);
    const result = hook.call(plugin, root, ctx);
    if (result instanceof Promise)
        return result.then(() => collectCommands(returnBuffer, ctx));
    return collectCommands(returnBuffer, ctx);
}
export function visitHastHook(handle, plugin, hook, source, fileURL, data = {}, sourceFormat = "markdown", diagnostics = []) {
    const result = visitHastHookCollect(handle, plugin, hook, source, fileURL, data, sourceFormat, diagnostics);
    if (result instanceof Promise) {
        return result.then((commands) => applyCollectedCommands(handle, commands));
    }
    return applyCollectedCommands(handle, result);
}
function collectCommands(returnBuffer, ctx) {
    const { merged } = mergeAndReset(returnBuffer, ctx);
    // Return the buffers to the pool. The merged Uint8Array above already
    // copied the bytes out, so the underlying ArrayBuffers can be reused.
    releaseCommandBuffer(returnBuffer);
    releaseCommandBuffer(ctx.getCommandBuffer());
    return merged;
}
