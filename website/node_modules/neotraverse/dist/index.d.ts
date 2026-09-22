import { i as getType, n as TraverseNodeType, r as TraverseOptions, t as TraverseContext } from "./utils-Bfb1gHfd.js";

//#region packages/neotraverse/src/clone.d.ts
/**
 * @example
 * ```js
 * import { clone } from 'neotraverse/modern';
 * const copy = clone({ nested: { n: 1 } });
 * copy.nested.n = 2; // original unchanged
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-clone
 */
declare function clone(obj: any, options?: TraverseOptions): any;
//#endregion
//#region packages/neotraverse/src/context.d.ts
/**
 * Depth-first walk; {@link forEach} and {@link map} use this internally.
 *
 * @example
 * ```js
 * import { walk } from 'neotraverse/modern';
 * walk({ a: { b: 1 } }, (ctx) => {
 *   if (ctx.path.join('.') === 'a.b') ctx.update(2);
 * });
 * // => { a: { b: 2 } }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-walk
 */
declare function walk(root: any, cb: (ctx: TraverseContext, v: any) => void, options?: TraverseOptions): any;
/**
 * Breadth-first {@link forEach}; visit order is level-by-level, not depth-first.
 *
 * @example
 * ```js
 * import { breadthFirst } from 'neotraverse/modern';
 * const order = [];
 * breadthFirst({ a: 1, b: { c: 2 } }, (ctx) => order.push(ctx.path.join('.')));
 * // order => ['', 'a', 'b', 'b.c']
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-bfs
 */
declare function breadthFirst(obj: any, cb: (ctx: TraverseContext, v: any) => void, options?: TraverseOptions): any;
/**
 * Breadth-first {@link map} (immutable clone with callback writeback).
 *
 * @example
 * ```js
 * import { mapBfs } from 'neotraverse/modern';
 * mapBfs({ items: [{ n: 1 }, { n: 2 }] }, (ctx, v) => {
 *   if (typeof v === 'number') ctx.update(v * 10);
 * });
 * // => { items: [{ n: 10 }, { n: 20 }] }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-bfs
 */
declare function mapBfs(obj: any, cb: (ctx: TraverseContext, v: any) => void, options?: TraverseOptions): any;
/**
 * Callback helper: calls {@link TraverseContext.block} when `pred` is truthy.
 * Compose with other callbacks in a single {@link forEach} / {@link map} pass.
 *
 * @example
 * ```js
 * import { forEach, skipWhere } from 'neotraverse/modern';
 * forEach({ a: 1, b: 2 }, skipWhere((ctx) => ctx.key === 'a'));
 * // visits only { b: 2 }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-skip-where
 */
declare function skipWhere(pred: (ctx: TraverseContext, value: any) => unknown): (ctx: TraverseContext, value: any) => void;
/**
 * Bucket every visited value by `keyFn(ctx, value)` in one walk.
 *
 * @example
 * ```js
 * import { groupBy } from 'neotraverse/modern';
 * groupBy({ a: 1, b: 2, c: 3 }, (_, v) => (v % 2 ? 'odd' : 'even'));
 * // Map { 'odd' => [1, 3], 'even' => [2] }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-group-by
 */
declare function groupBy(obj: any, keyFn: (ctx: TraverseContext, value: any) => PropertyKey, options?: TraverseOptions): Map<PropertyKey, any[]>;
/**
 * @example
 * ```js
 * import { map } from 'neotraverse/modern';
 * map({ count: 1 }, (ctx, v) => {
 *   if (typeof v === 'number') ctx.update(v + 1);
 * });
 * // => { count: 2 }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-map
 */
declare function map(obj: any, cb: (ctx: TraverseContext, v: any) => void, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { forEach } from 'neotraverse/modern';
 * forEach([5, -3], (ctx, x) => { if (x < 0) ctx.update(x + 128); });
 * // => [5, 125]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-forEach
 */
declare function forEach(obj: any, cb: (ctx: TraverseContext, v: any) => void, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { reduce } from 'neotraverse/modern';
 * reduce({ a: 1, b: 2 }, (acc, ctx, x) => acc + (typeof x === 'number' ? x : 0), 0);
 * // => 3
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-reduce
 */
declare function reduce(obj: any, cb: (ctx: TraverseContext, acc: any, v: any) => any, init?: any, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { find } from 'neotraverse/modern';
 * find({ a: 1, b: 5 }, (_, v) => v > 3);
 * // => 5
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/query#t-query
 */
declare function find(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { filter } from 'neotraverse/modern';
 * filter({ a: 1, b: 2, c: 3 }, (_, v) => typeof v === 'number' && v % 2 === 0);
 * // => [2]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/query#t-query
 */
declare function filter(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): any[];
/**
 * @example
 * ```js
 * import { some } from 'neotraverse/modern';
 * some({ a: 1, b: 2 }, (_, v) => v > 1);
 * // => true
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/query#t-query
 */
declare function some(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): boolean;
/**
 * @example
 * ```js
 * import { every } from 'neotraverse/modern';
 * every({ a: 2, b: 4 }, (_, v) => typeof v !== 'number' || v % 2 === 0);
 * // => true
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/query#t-query
 */
declare function every(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): boolean;
/**
 * @example
 * ```js
 * import { paths } from 'neotraverse/modern';
 * paths({ a: { b: 1 } });
 * // => [[], ['a'], ['a', 'b']]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-paths-nodes
 */
declare function paths(obj: any, options?: TraverseOptions): PropertyKey[][];
/**
 * @example
 * ```js
 * import { nodes } from 'neotraverse/modern';
 * nodes({ x: 1, y: { z: 2 } }).filter((v) => typeof v === 'number');
 * // => [1, 2]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-paths-nodes
 */
declare function nodes(obj: any, options?: TraverseOptions): any[];
/**
 * Lazy `[path, node]` pairs in depth-first order.
 *
 * @example
 * ```js
 * import { entries } from 'neotraverse/modern';
 * [...entries({ a: 1 })];
 * // => [[[], { a: 1 }], [['a'], 1]]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/iteration#t-entries
 */
declare function entries(obj: any, options?: TraverseOptions): Generator<[PropertyKey[], any]>;
/**
 * Lazy node values in depth-first order.
 *
 * @example
 * ```js
 * import { values } from 'neotraverse/modern';
 * [...values({ a: 1, b: 2 })];
 * // => [{ a: 1, b: 2 }, 1, 2]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/iteration#t-values
 */
declare function values(obj: any, options?: TraverseOptions): Generator<any>;
/**
 * @example
 * ```js
 * import { values } from 'neotraverse/modern';
 * [...values({ a: 1, b: 2 })].filter((v) => typeof v === 'number');
 * // => [1, 2]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/async#t-async
 */
declare function forEachAsync(obj: any, cb: (ctx: TraverseContext, v: any) => void | Promise<void>, options?: TraverseOptions): Promise<any>;
/**
 * @example
 * ```js
 * import { mapAsync } from 'neotraverse/modern';
 * await mapAsync({ n: 1 }, async (ctx, v) => {
 *   if (typeof v === 'number') ctx.update(v * 2);
 * });
 * // => { n: 2 }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/async#t-async
 */
declare function mapAsync(obj: any, cb: (ctx: TraverseContext, v: any) => void | Promise<void>, options?: TraverseOptions): Promise<any>;
/**
 * @example
 * ```js
 * import { count } from 'neotraverse/modern';
 * count({ a: 1, b: 2, c: 3 }, (_, v) => typeof v === 'number' && v > 1);
 * // => 2
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-count
 */
declare function count(obj: any, fn?: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): number;
/**
 * @example
 * ```js
 * import { size } from 'neotraverse/modern';
 * size({ a: { b: 1 }, c: 2 }); // => 4 (root + a + b + c)
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-count
 */
declare function size(obj: any, options?: TraverseOptions): number;
/**
 * @example
 * ```js
 * import { deleteWhere } from 'neotraverse/modern';
 * deleteWhere({ token: 'secret', ok: true }, (_, x) => x === 'secret');
 * // => { ok: true }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-prune
 */
declare function deleteWhere(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { prune } from 'neotraverse/modern';
 * prune({ a: 1, b: 2 }, (_, v) => typeof v !== 'number' || v < 2);
 * // => { a: 1 }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-prune
 */
declare function prune(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): any;
/**
 * Return a deep clone of `obj` with every own `__proto__` / `constructor` / `prototype`
 * key removed at every level. Use this at a **trust boundary**: before handing untrusted
 * parsed JSON to code that is NOT prototype-pollution-hardened (a naive deep-merge, an ORM,
 * a template engine). neotraverse's own operations already neutralize these keys, so you
 * don't need `sanitize` for them.
 *
 * Scope: this removes the **key-injection** pollution vector only. It does NOT bound depth
 * or size, and it does not sanitize path-based writes (`set(obj, untrustedPath, v)` is a
 * separate vector). It is not a general "make this object safe" guarantee.
 *
 * @example
 * ```js
 * import { sanitize } from 'neotraverse/modern';
 * const safe = sanitize(JSON.parse(untrustedBody));
 * naiveDeepMerge(target, safe); // can't pollute via __proto__/constructor/prototype
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-sanitize
 */
declare function sanitize(obj: any, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { pruneDeep } from 'neotraverse/modern';
 * pruneDeep({ a: { b: { c: 1 } } }, 2);
 * // => { a: { b: null } }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-prune-deep
 */
declare function pruneDeep(obj: any, maxDepth: number, replacement?: any, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { freeze } from 'neotraverse/modern';
 * const o = freeze({ nested: { n: 1 } });
 * Object.isFrozen(o.nested); // => true
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-freeze
 */
declare function freeze(obj: any, options?: TraverseOptions): any;
//#endregion
//#region packages/neotraverse/src/ops.d.ts
/**
 * Options for {@link merge}.
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-merge
 */
interface MergeOptions extends TraverseOptions {
  /**
   * How to combine two arrays at the same path.
   *
   * @default `'replace'`
   *
   * @see https://neotraverse.puruvj.dev/guide/api/walk#t-merge
   */
  array?: 'replace' | 'concat';
}
/**
 * Deep-merge `source` into a clone of `target` (does not mutate `target`).
 * Plain objects and Map entries merge recursively; arrays replace index-by-index
 * unless `array: 'concat'`. Other types are replaced from `source`.
 *
 * @example
 * ```js
 * import { merge } from 'neotraverse/modern';
 * merge({ x: 1, nested: { a: 1 } }, { y: 2, nested: { b: 2 } });
 * // => { x: 1, y: 2, nested: { a: 1, b: 2 } }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-merge
 */
declare function merge(target: any, source: any, options?: MergeOptions): any;
/**
 * Options for {@link dereference}.
 *
 * @see https://neotraverse.puruvj.dev/guide/api/walk#t-dereference
 */
interface DereferenceOptions extends TraverseOptions {
  /**
   * Only resolve refs whose string starts with `#` (JSON Pointer).
   *
   * @default true
   *
   * @see https://neotraverse.puruvj.dev/guide/api/walk#t-dereference
   */
  localOnly?: boolean;
}
declare function dereference(obj: any, options?: DereferenceOptions): any;
/**
 * Options for {@link deepEqual}.
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-deep-equal
 */
interface DeepEqualOptions {
  /**
   * Custom per-pair comparator; return `undefined` to fall back to structural equality.
   *
   * @see https://neotraverse.puruvj.dev/guide/api/structural#t-deep-equal
   */
  compareFn?: (a: any, b: any) => boolean | undefined;
  /**
   * Maximum comparison depth. When set, comparing values nested deeper throws a
   * catchable `RangeError` instead of overflowing the call stack — bound this for
   * untrusted input. Unlimited when omitted.
   */
  maxDepth?: number;
}
/**
 * Structural equality with an explicit per-type contract (not identical to `clone()`).
 *
 * @example
 * ```js
 * import { deepEqual } from 'neotraverse/modern';
 * deepEqual({ a: [1] }, { a: [1] }); // => true
 * deepEqual(new Date('2020-01-01'), new Date('2020-01-01')); // => true
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-deep-equal
 */
declare function deepEqual(a: any, b: any, options?: DeepEqualOptions): boolean;
/**
 * Options for {@link toJSON}.
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-to-json
 */
interface ToJSONOptions extends TraverseOptions {
  /**
   * Value inserted where a circular reference is detected.
   *
   * @default null
   *
   * @see https://neotraverse.puruvj.dev/guide/api/structural#t-to-json
   */
  cycle?: null | string;
}
/**
 * JSON.stringify after a walk; does not throw on circular references.
 *
 * @example
 * ```js
 * import { toJSON } from 'neotraverse/modern';
 * const ring = {}; ring.self = ring;
 * toJSON(ring); // => '{"self":null}'
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-to-json
 */
declare function toJSON(obj: any, options?: ToJSONOptions): string;
/**
 * RFC 6902 patch operation (`add` | `remove` | `replace`).
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-diff
 */
type PatchOp = {
  op: 'add';
  path: string;
  value: any;
} | {
  op: 'remove';
  path: string;
} | {
  op: 'replace';
  path: string;
  value: any;
};
/**
 * RFC 6902 subset (`add` / `remove` / `replace`). Circular graphs are not supported.
 *
 * @example
 * ```js
 * import { diff } from 'neotraverse/modern';
 * diff({ a: 1 }, { a: 2, b: 3 });
 * // => [
 * //   { op: 'replace', path: '/a', value: 2 },
 * //   { op: 'add', path: '/b', value: 3 },
 * // ]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-diff
 */
declare function diff(a: any, b: any, options?: {
  maxDepth?: number;
}): PatchOp[];
/**
 * @example
 * ```js
 * import { patch } from 'neotraverse/modern';
 * patch({ a: 1 }, [{ op: 'replace', path: '/a', value: 2 }]);
 * // => { a: 2 }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/structural#t-diff
 */
declare function patch(obj: any, ops: PatchOp[]): any;
//#endregion
//#region packages/neotraverse/src/path.d.ts
/**
 * @example
 * ```js
 * import { get } from 'neotraverse/modern';
 * get({ user: { name: 'Ada' } }, ['user', 'name']);
 * // => 'Ada'
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-get-set-has
 */
declare function get(obj: any, paths: PropertyKey[], options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { has } from 'neotraverse/modern';
 * has({ a: 1 }, ['a']); // => true
 * has({ a: 1 }, ['b']); // => false
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-get-set-has
 */
declare function has(obj: any, paths: PropertyKey[], options?: TraverseOptions): boolean;
/**
 * @example
 * ```js
 * import { set } from 'neotraverse/modern';
 * const o = {};
 * set(o, ['user', 'id'], 42);
 * // o => { user: { id: 42 } }
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/core#t-get-set-has
 */
declare function set(obj: any, path: PropertyKey[], value: any, _options?: TraverseOptions): any;
/**
 * Dot notation (`a.b.0`). Use a leading `/` for JSON Pointer (`/a/b/0`).
 *
 * @example
 * ```js
 * import { parsePath } from 'neotraverse/modern';
 * parsePath('user.name'); // => ['user', 'name']
 * parsePath('/user/0'); // => ['user', 0]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function parsePath(path: string): PropertyKey[];
/**
 * @example
 * ```js
 * import { parseDotPath } from 'neotraverse/modern';
 * parseDotPath('users[0].name'); // => ['users', '0', 'name']
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function parseDotPath(path: string): PropertyKey[];
/**
 * @example
 * ```js
 * import { parseJsonPointer } from 'neotraverse/modern';
 * parseJsonPointer('/defs/Pet'); // => ['defs', 'Pet']
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function parseJsonPointer(pointer: string): PropertyKey[];
/**
 * @example
 * ```js
 * import { pointerPath } from 'neotraverse/modern';
 * pointerPath(['user', 0, 'name']); // => '/user/0/name'
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function pointerPath(path: PropertyKey[]): string;
/**
 * @example
 * ```js
 * import { getPath } from 'neotraverse/modern';
 * getPath({ user: { id: 1 } }, 'user.id'); // => 1
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function getPath(obj: any, path: string, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { hasPath } from 'neotraverse/modern';
 * hasPath({ user: { id: 1 } }, 'user.email'); // => false
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function hasPath(obj: any, path: string, options?: TraverseOptions): boolean;
/**
 * @example
 * ```js
 * import { setPath } from 'neotraverse/modern';
 * const cfg = { server: { port: 3000 } };
 * setPath(cfg, 'server.port', 8080);
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
 */
declare function setPath(obj: any, path: string, value: any, options?: TraverseOptions): any;
/**
 * @example
 * ```js
 * import { findPaths } from 'neotraverse/modern';
 * findPaths({ users: [{ flag: true }] }, (_, x) => x === true);
 * // => ['users', '0', 'flag']
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-find-paths
 */
declare function findPaths(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): PropertyKey[] | undefined;
/**
 * A `path` and `node` pair returned by path query helpers.
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-find-paths
 */
interface PathNode {
  /**
   * Key path from the root to the node.
   *
   * @see https://neotraverse.puruvj.dev/guide/api/paths#t-find-paths
   */
  path: PropertyKey[];
  /**
   * Value at {@link path}.
   *
   * @see https://neotraverse.puruvj.dev/guide/api/paths#t-find-paths
   */
  node: any;
}
/**
 * @example
 * ```js
 * import { filterPaths } from 'neotraverse/modern';
 * filterPaths({ a: 1, b: 2 }, (_, v) => typeof v === 'number' && v > 1);
 * // => [{ path: ['b'], node: 2 }]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-find-paths
 */
declare function filterPaths(obj: any, fn: (ctx: TraverseContext, v: any) => unknown, options?: TraverseOptions): PathNode[];
type GlobSeg = {
  kind: 'any';
} | {
  kind: 'literal';
  key: string;
} | {
  kind: 'keyAnyIndex';
  key: string;
};
/**
 * @example
 * ```js
 * import { parseGlob } from 'neotraverse/modern';
 * parseGlob('users[*].name');
 * // => [{ kind: 'literal', key: 'users' }, { kind: 'keyAnyIndex', key: 'name' }]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-select
 */
declare function parseGlob(glob: string): GlobSeg[];
/**
 * Glob path query (`*`, `key[*]`, dot segments). Predicate search → `filterPaths`.
 *
 * @example
 * ```js
 * import { select } from 'neotraverse/modern';
 * select({ users: [{ name: 'Ada' }, { name: 'Bob' }] }, 'users[*].name');
 * // => [{ path: ['users', 0, 'name'], node: 'Ada' }, { path: ['users', 1, 'name'], node: 'Bob' }]
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-select
 */
declare function select(obj: any, glob: string, options?: TraverseOptions): PathNode[];
//#endregion
export { type DeepEqualOptions, type DereferenceOptions, type MergeOptions, type PatchOp, type PathNode, type ToJSONOptions, type TraverseContext, type TraverseNodeType, type TraverseOptions, breadthFirst, clone, count, deepEqual, deleteWhere, dereference, diff, entries, every, filter, filterPaths, find, findPaths, forEach, forEachAsync, freeze, get, getPath, getType, groupBy, has, hasPath, map, mapAsync, mapBfs, merge, nodes, parseDotPath, parseGlob, parseJsonPointer, parsePath, patch, paths, pointerPath, prune, pruneDeep, reduce, sanitize, select, set, setPath, size, skipWhere, some, toJSON, values, walk };