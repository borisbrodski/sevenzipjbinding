import type { LazyChildResolver } from "../lazy-child-resolver.js";
import type { HastNode } from "../types.js";
import type { HastReader } from "./hast-reader.js";
type HastResolver = LazyChildResolver<HastReader, HastNode>;
/**
 * Walk-path child stub: arena id + `type` eagerly, every other field a lazy
 * forward to the materialized node (first read snapshots the arena via
 * `materializeOne`, which enforces the handle epoch). Spread/identity rules
 * are enforced by `nid()` in hast-visitor.ts.
 */
export declare class HastChildStub {
    _resolver: HastResolver;
    _id: number;
    type: string;
    constructor(resolver: HastResolver, id: number, nodeType: number);
}
export {};
