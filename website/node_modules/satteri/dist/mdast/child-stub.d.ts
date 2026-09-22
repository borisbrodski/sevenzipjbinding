import type { LazyChildResolver } from "../lazy-child-resolver.js";
import type { MdastNode } from "../types.js";
import type { MdastReader } from "./mdast-reader.js";
type MdastResolver = LazyChildResolver<MdastReader, MdastNode>;
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
export declare class MdastChildStub {
    _resolver: MdastResolver;
    _id: number;
    type: string;
    constructor(resolver: MdastResolver, id: number, nodeType: number);
}
export {};
