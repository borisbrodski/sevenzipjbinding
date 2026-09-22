//#region packages/neotraverse/src/legacy/index.d.ts
/**
 * Walk and clone options (legacy `Traverse` constructor).
 *
 * @see https://neotraverse.puruvj.dev/guide/options.html
 */
interface TraverseOptions {
  /**
   * If true, does not alter the original object
   *
   * @see https://neotraverse.puruvj.dev/guide/options.html
   */
  immutable?: boolean;
  /**
   * If false, removes all symbols from traversed objects
   *
   * @default false
   *
   * @see https://neotraverse.puruvj.dev/guide/options.html
   */
  includeSymbols?: boolean;
  /**
   * Maximum traversal/clone depth. When set, traversing or cloning an object
   * nested deeper than this throws a `RangeError` instead of overflowing the
   * call stack — useful for bounding untrusted input. Unlimited when omitted.
   *
   * @see https://neotraverse.puruvj.dev/guide/security.html
   */
  maxDepth?: number;
}
/**
 * Callback context (`this` / `ctx`) in legacy `Traverse` callbacks.
 *
 * @see https://neotraverse.puruvj.dev/guide/context.html
 */
interface TraverseContext {
  /**
   * The present node on the recursive walk
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  node: any;
  /**
   * An array of string keys from the root to the present node
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  path: PropertyKey[];
  /**
   * The context of the node's parent.
   * This is `undefined` for the root node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  parent: TraverseContext | undefined;
  /**
   * The contexts of the node's parents.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  parents: TraverseContext[];
  /**
   * The name of the key of the present node in its parent.
   * This is `undefined` for the root node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  key: PropertyKey | undefined;
  /**
   * Whether the present node is the root node
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  isRoot: boolean;
  /**
   * Whether the present node is not the root node
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  notRoot: boolean;
  /**
   * Whether the present node is the last node
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  isLast: boolean;
  /**
   * Whether the present node is the first node
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  isFirst: boolean;
  /**
   * Whether or not the present node is a leaf node (has no children)
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  isLeaf: boolean;
  /**
   * Whether or not the present node is not a leaf node (has children)
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  notLeaf: boolean;
  /**
   * Depth of the node within the traversal
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  level: number;
  /**
   * If the node equals one of its parents, the `circular` attribute is set to the context of that parent and the traversal progresses no deeper.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html#context-circular
   */
  circular: TraverseContext | undefined;
  /**
   * Set a new value for the present node.
   *
   * All the elements in `value` will be recursively traversed unless `stopHere` is true (false by default).
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  update(value: any, stopHere?: boolean): void;
  /**
   * Remove the current element from the output. If the node is in an Array it will be spliced off. Otherwise it will be deleted from its parent.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  remove(stopHere?: boolean): void;
  /**
   * Delete the current element from its parent in the output. Calls `delete` even on Arrays.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  delete(stopHere?: boolean): void;
  /**
   * Object keys of the node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  keys: PropertyKey[] | null;
  /**
   * Call this function before all of the children are traversed.
   * You can assign into `this.keys` here to traverse in a custom order.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  before(callback: (this: TraverseContext, value: any) => void): void;
  /**
   * Call this function after all of the children are traversed.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  after(callback: (this: TraverseContext, value: any) => void): void;
  /**
   * Call this function before each of the children are traversed.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  pre(callback: (this: TraverseContext, child: any, key: any) => void): void;
  /**
   * Call this function after each of the children are traversed.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  post(callback: (this: TraverseContext, child: any) => void): void;
  /**
   * Stops traversal entirely.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  stop(): void;
  /**
   * Prevents traversing descendents of the current node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context.html
   */
  block(): void;
}
/**
 * @deprecated Import `Traverse` from `neotraverse/modern` instead
 *
 * @see https://neotraverse.puruvj.dev/legacy.html#methods
 */
declare class Traverse {
  _value: any;
  _options: TraverseOptions;
  constructor(obj: any, options?: TraverseOptions);
  /**
   * Get the element at the array `path`.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  get(paths: PropertyKey[]): any;
  /**
   * Return whether the element at the array `path` exists.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  has(paths: PropertyKey[]): boolean;
  /**
   * Set the element at the array `path` to `value`.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  set(path: PropertyKey[], value: any): any;
  /**
   * Execute `fn` for each node in the object and return a new object with the results of the walk. To update nodes in the result use `this.update(value)`.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  map(cb: (this: TraverseContext, v: any) => void): any;
  /**
   * Execute `fn` for each node in the object but unlike `.map()`, when `this.update()` is called it updates the object in-place.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  forEach(cb: (this: TraverseContext, v: any) => void): any;
  /**
   * For each node in the object, perform a [left-fold](http://en.wikipedia.org/wiki/Fold_(higher-order_function)) with the return value of `fn(acc, node)`.
   *
   * If `init` isn't specified, `init` is set to the root object for the first step and the root element is skipped.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  reduce(cb: (this: TraverseContext, acc: any, v: any) => void, init?: any): any;
  /**
   * Return an `Array` of every possible non-cyclic path in the object.
   * Paths are `Array`s of string keys.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  paths(): PropertyKey[][];
  /**
   * Return an `Array` of every node in the object.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  nodes(): any[];
  /**
   * Create a deep clone of the object.
   *
   * @see https://neotraverse.puruvj.dev/legacy.html#methods
   */
  clone(): any;
}
declare const traverse: {
  (obj: any, options?: TraverseOptions): Traverse;
  get(obj: any, paths: PropertyKey[], options?: TraverseOptions): any;
  set(obj: any, path: PropertyKey[], value: any, options?: TraverseOptions): any;
  has(obj: any, paths: PropertyKey[], options?: TraverseOptions): boolean;
  map(obj: any, cb: (this: TraverseContext, v: any) => void, options?: TraverseOptions): any;
  forEach(obj: any, cb: (this: TraverseContext, v: any) => void, options?: TraverseOptions): any;
  reduce(obj: any, cb: (this: TraverseContext, acc: any, v: any) => void, init?: any, options?: TraverseOptions): any;
  paths(obj: any, options?: TraverseOptions): PropertyKey[][];
  nodes(obj: any, options?: TraverseOptions): any[];
  clone(obj: any, options?: TraverseOptions): any;
};
//#endregion
export { type TraverseContext, type TraverseOptions, traverse as default };