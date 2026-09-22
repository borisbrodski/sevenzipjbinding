import { flatByTag, installStubDescriptors, stubDescriptors, } from "../child-stub.js";
import { MDAST_LAYOUT_KEYS } from "./generated/layout.js";
import { NAME_TO_TYPE, TYPE_NAMES } from "./generated/node-types.js";
import { LEAF_TYPES } from "./mdast-materializer.js";
const N = NAME_TO_TYPE;
/** Per-type stub fields for the types `addTypeProperties` hand-writes; the
 *  fixed-layout types come from the generated `MDAST_LAYOUT_KEYS`. */
const HAND_WRITTEN_FIELDS = {
    [N.list]: ["ordered", "start", "spread"],
    [N.listItem]: ["spread", "checked"],
    [N.descriptionDetails]: ["spread"],
    [N.table]: ["align"],
    [N.containerDirective]: ["name", "attributes"],
    [N.leafDirective]: ["name", "attributes"],
    [N.textDirective]: ["name", "attributes"],
    [N.mdxJsxFlowElement]: ["name", "attributes"],
    [N.mdxJsxTextElement]: ["name", "attributes"],
};
const TYPE_NAME_BY_TAG = flatByTag(TYPE_NAMES);
/** Internal tag for user-defined nodes; its stored `name` field is folded into
 *  the open `node.type`, so the stub exposes `value` (leaf content) but no
 *  separate `name`. */
const MDAST_CUSTOM = NAME_TO_TYPE.custom;
const MDAST_STUB_DESCRIPTORS = [];
for (const tag of Object.keys(TYPE_NAMES)) {
    const nodeType = Number(tag);
    const fields = nodeType === MDAST_CUSTOM
        ? ["value"]
        : [...(MDAST_LAYOUT_KEYS[nodeType] ?? HAND_WRITTEN_FIELDS[nodeType] ?? [])];
    // `custom` gets its own `children` getter: leafness is per node, not per type.
    if (nodeType !== MDAST_CUSTOM && !LEAF_TYPES.has(nodeType))
        fields.push("children");
    MDAST_STUB_DESCRIPTORS[nodeType] = stubDescriptors(fields);
}
/** Unknown node types still expose the prelude-backed lazy fields. */
const FALLBACK_DESCRIPTORS = stubDescriptors([]);
/**
 * Walk-path child stub: arena id + `type` eagerly, every other field a lazy
 * forward to the materialized node (first read snapshots the arena via
 * `materializeOne`, which enforces the handle epoch). Spread/identity rules
 * are enforced by `nid()` (authoritative doc in hast-visitor.ts).
 *
 * A user-defined node's `type` is the one exception: it lives in the arena,
 * not the tag, so it joins the lazy fields rather than making every sibling
 * list snapshot the arena up front.
 */
export class MdastChildStub {
    _resolver;
    _id;
    type;
    constructor(resolver, id, nodeType) {
        this._resolver = resolver;
        this._id = id;
        if (nodeType === MDAST_CUSTOM) {
            installLazyCustomType(this);
            installLazyCustomChildren(this);
        }
        else {
            this.type = TYPE_NAME_BY_TAG[nodeType] ?? `unknown(${nodeType})`;
        }
        installStubDescriptors(this, MDAST_STUB_DESCRIPTORS[nodeType] ?? FALLBACK_DESCRIPTORS);
    }
    /** @internal */
    get _refs() {
        return this._resolver.refs;
    }
}
/** `children` as a self-replacing accessor, and self-*removing* for a leaf: the
 *  stub only knows the tag, so it cannot tell a leaf from a parent until the
 *  arena snapshot says which shape this node has. */
function installLazyCustomChildren(stub) {
    Object.defineProperty(stub, "children", {
        get() {
            const real = this._resolver.materializeOne(this._id);
            const value = real.children;
            if (value === undefined) {
                delete this.children;
                return undefined;
            }
            Object.defineProperty(this, "children", {
                value,
                writable: false,
                enumerable: true,
                configurable: false,
            });
            return value;
        },
        enumerable: true,
        configurable: true,
    });
}
/** `type` as a self-replacing accessor: the materialized node folds the stored
 *  name into `type`. Enumerable so a spread copy still carries it. */
function installLazyCustomType(stub) {
    Object.defineProperty(stub, "type", {
        get() {
            const value = this._resolver.materializeOne(this._id).type;
            Object.defineProperty(this, "type", {
                value,
                writable: true,
                enumerable: true,
                configurable: true,
            });
            return value;
        },
        enumerable: true,
        configurable: true,
    });
}
