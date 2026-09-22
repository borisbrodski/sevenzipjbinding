//#region packages/neotraverse/src/utils.d.ts
/**
 * Walk, clone, and async traversal options.
 *
 * @see https://neotraverse.puruvj.dev/guide/options
 */
interface TraverseOptions {
  /**
   * If true, does not alter the original object
   *
   * @see https://neotraverse.puruvj.dev/guide/options
   */
  immutable?: boolean;
  /**
   * If false, removes all symbols from traversed objects
   *
   * @default false
   *
   * @see https://neotraverse.puruvj.dev/guide/options
   */
  includeSymbols?: boolean;
  /**
   * Maximum traversal/clone depth. When set, traversing or cloning an object
   * nested deeper than this throws a `RangeError` instead of overflowing the
   * call stack — useful for bounding untrusted input. Unlimited when omitted.
   *
   * @see https://neotraverse.puruvj.dev/guide/security
   */
  maxDepth?: number;
  /**
   * Cancel an in-flight async walk ({@link forEachAsync} /
   * {@link mapAsync}). When the signal aborts, the walk rejects with
   * the signal's reason on the next visited node. Ignored by the synchronous
   * methods.
   *
   * @see https://neotraverse.puruvj.dev/guide/api/async#t-async
   */
  signal?: AbortSignal;
  /**
   * When true, {@link Map} and {@link Set} are not leaves — their entries are
   * visited (Map: each value at its key; Set: each element at a numeric index).
   * @default false
   *
   * @see https://neotraverse.puruvj.dev/guide/api/iteration#t-entries
   */
  descendIntoMapSet?: boolean;
  /**
   * Max parallel sibling callbacks in {@link forEachAsync} / {@link mapAsync}.
   * @default 1
   *
   * @see https://neotraverse.puruvj.dev/guide/api/async#t-async
   */
  concurrency?: number;
}
/**
 * Callback context (`ctx`) passed to every traversal function.
 *
 * @see https://neotraverse.puruvj.dev/guide/context
 */
interface TraverseContext {
  /**
   * The present node on the recursive walk
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  node: any;
  /**
   * An array of string keys from the root to the present node
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  path: PropertyKey[];
  /**
   * The context of the node's parent.
   * This is `undefined` for the root node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  parent: TraverseContext | undefined;
  /**
   * The contexts of the node's parents.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  parents: TraverseContext[];
  /**
   * The name of the key of the present node in its parent.
   * This is `undefined` for the root node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  key: PropertyKey | undefined;
  /**
   * Whether the present node is the root node
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  isRoot: boolean;
  /**
   * Whether the present node is not the root node
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  notRoot: boolean;
  /**
   * Whether the present node is the last node
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  isLast: boolean;
  /**
   * Whether the present node is the first node
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  isFirst: boolean;
  /**
   * Whether or not the present node is a leaf node (has no children)
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  isLeaf: boolean;
  /**
   * Whether or not the present node is not a leaf node (has children)
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  notLeaf: boolean;
  /**
   * Depth of the node within the traversal
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  level: number;
  /**
   * If the node equals one of its parents, the `circular` attribute is set to the context of that parent and the traversal progresses no deeper.
   *
   * @see https://neotraverse.puruvj.dev/guide/context#context-circular
   */
  circular: TraverseContext | undefined;
  /**
   * Set a new value for the present node.
   *
   * All the elements in `value` will be recursively traversed unless `stopHere` is true (false by default).
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  update(value: any, stopHere?: boolean): void;
  /**
   * Remove the current element from the output. If the node is in an Array it will be spliced off. Otherwise it will be deleted from its parent.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  remove(stopHere?: boolean): void;
  /**
   * Delete the current element from its parent in the output. Calls `delete` even on Arrays.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  delete(stopHere?: boolean): void;
  /**
   * Object keys of the node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  keys: PropertyKey[] | null;
  /**
   * Call this function before all of the children are traversed.
   * You can assign into `ctx.keys` here to traverse in a custom order.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  before(callback: (ctx: TraverseContext, value: any) => void): void;
  /**
   * Call this function after all of the children are traversed.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  after(callback: (ctx: TraverseContext, value: any) => void): void;
  /**
   * Call this function before each of the children are traversed.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  pre(callback: (ctx: TraverseContext, child: any, key: any) => void): void;
  /**
   * Call this function after each of the children are traversed.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  post(callback: (ctx: TraverseContext, child: any) => void): void;
  /**
   * Stops traversal entirely.
   *
   * @see https://neotraverse.puruvj.dev/guide/context
   */
  stop(): void;
  /**
   * Prevents traversing descendents of the current node.
   *
   * @see https://neotraverse.puruvj.dev/guide/context#context-block
   */
  block(): void;
  /**
   * Next sibling context, or `undefined`. Reads live parent state (not `isLast`).
   *
   * @see https://neotraverse.puruvj.dev/guide/context#context-siblings
   */
  nextSibling(): TraverseContext | undefined;
  /**
   * Previous sibling context, or `undefined`. Reads live parent state (not `isFirst`).
   *
   * @see https://neotraverse.puruvj.dev/guide/context#context-siblings
   */
  prevSibling(): TraverseContext | undefined;
}
/**
 * Locked union — do not rename tags after release. Returned by {@link getType}.
 *
 * @see https://neotraverse.puruvj.dev/guide/types#types-and-traversal
 */
type TraverseNodeType = 'null' | 'primitive' | 'function' | 'array' | 'object' | 'date' | 'regexp' | 'map' | 'set' | 'weakmap' | 'weakset' | 'typed-array' | 'arraybuffer' | 'dataview' | 'error';
/**
 * Classify a value for branching inside traversal callbacks.
 * See the [types reference](https://neotraverse.puruvj.dev/guide/types#types-and-traversal) for walk vs clone behaviour per tag.
 *
 * @example
 * ```js
 * import { getType } from 'neotraverse/modern';
 * getType(new Map()); // => 'map'
 * getType([1, 2]); // => 'array'
 * ```
 *
 * @see https://neotraverse.puruvj.dev/guide/api/paths#t-get-type
 */
declare function getType(value: unknown): TraverseNodeType;
//#endregion
export { getType as i, TraverseNodeType as n, TraverseOptions as r, TraverseContext as t };