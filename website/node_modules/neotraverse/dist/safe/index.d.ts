//#region packages/neotraverse/src/safe/kernel/keys.d.ts
/** Shared option bits understood by every traversing op (law 4). */
interface KeyOptions {
  /** Include own enumerable symbol keys. @default false */
  symbols?: boolean;
  /** Descend into Map/Set recursively at every level. @default false */
  mapSet?: boolean;
  /** RangeError past this depth — the untrusted-input bound. Unlimited when omitted. */
  maxDepth?: number;
}
//#endregion
//#region packages/neotraverse/src/safe/kernel/pattern.d.ts
/** Opaque live-state set: bitmask int on the fast path, Set<number> beyond 30 segments. */
type MatchStates = number | Set<number>;
//#endregion
//#region packages/neotraverse/src/safe/kernel/cursor.d.ts
declare const ENTER = 1;
declare const EXIT = 2;
type Phase = typeof ENTER | typeof EXIT;
interface Frame {
  node: any;
  keys: PropertyKey[] | null;
  /** -1 = freshly pushed (needs ENTER); otherwise the next child index. */
  index: number;
  depth: number;
  key: PropertyKey | undefined;
  parent: Frame | undefined;
  /** opaque stash for the consumer's Visit record (parent linkage + lazy path). */
  view: any;
  /** opaque stash for transform's per-frame fold state (kernel never reads it). */
  tx: any;
  /** the ancestor frame this node points back to, or undefined. */
  circular: Frame | undefined;
  /** pattern live-states entering this node (0 / empty when no matcher). */
  states: MatchStates;
  /** matcher.accepts(states) — true when no matcher (match-all). */
  matched: boolean;
  /** descent suppressed (leaf, cycle, pattern-pruned, or consumer skip()). */
  skipped: boolean;
  isLeaf: boolean;
}
interface CursorOptions extends KeyOptions {
  /** Glob pattern; only matched frames are surfaced and descent prunes to viable prefixes. */
  match?: string;
  /**
   * Surface EXIT events. Post-order reads and transform need them; a plain
   * pre-order read does not, so it sets this false and the cursor does the
   * EXIT bookkeeping internally — halving step() calls per node. @default true
   */
  exits?: boolean;
}
/**
 * Depth-first ENTER/EXIT cursor. Pull one event at a time via {@link step};
 * after it returns true, read {@link phase} and {@link frame}. ENTER fires when
 * a node is first reached (parents before children); EXIT fires once all of a
 * node's descendants have been processed (children before parents).
 */
declare class Cursor {
  phase: Phase;
  frame: Frame;
  private readonly stack;
  private readonly ancestors;
  private readonly symbols;
  private readonly mapSet;
  private readonly maxDepth;
  private readonly matcher;
  private readonly emitExits;
  private readonly pool;
  private recyclable;
  constructor(root: any, options?: CursorOptions);
  private acquire;
  /** Advance to the next ENTER/EXIT event. Returns false when traversal is done. */
  step(): boolean;
  /** Mark the current node so its descendants are not visited. */
  skip(): void;
  /**
   * Swap the current node for `value` and descend into the REPLACEMENT's
   * children instead of the original's (transform's `replace(v, {descend:true})`).
   * The replacement node itself is not re-entered. Must be called during the
   * current frame's ENTER, before the next {@link step}.
   */
  replaceCurrent(value: any): void;
  private enter;
  get hasMatcher(): boolean;
}
//#endregion
//#region packages/neotraverse/src/safe/visit.d.ts
interface VisitOptions extends KeyOptions {
  /** Traversal order. @default 'pre' (parents before children). */
  order?: 'pre' | 'post' | 'breadth';
  /** Glob pattern; yields only matching nodes and prunes descent to viable prefixes. */
  match?: string;
}
/**
 * One visited node. Created fresh per node and safe to retain. The control
 * method {@link skip} is meaningful only during the current iteration step.
 */
declare class Visit<V = unknown> {
  /** The node's value. */
  readonly value: V;
  /** The node's key in its parent (`undefined` at the root). Map keys appear as-is. */
  readonly key: PropertyKey | undefined;
  /** The parent node's Visit (`undefined` at the root). */
  readonly parent: Visit | undefined;
  /** Depth from the root (`0` at the root). */
  readonly depth: number;
  /** No traversable children under the current options. */
  readonly isLeaf: boolean;
  /** The ancestor Visit this node points back to, if it is a back-edge. */
  readonly circular: Visit | undefined;
  /** @internal owning session, for {@link skip}. */
  private readonly s;
  constructor(session: Session, value: V, key: PropertyKey | undefined, parent: Visit | undefined, depth: number, isLeaf: boolean, circular: Visit | undefined);
  /** Key path from the root. Lazily rebuilt from the parent chain on each read. */
  get path(): PropertyKey[];
  /** RFC 6901 JSON Pointer to this node (`/users/0/name`). Lazy. */
  get pointer(): string;
  /**
   * Do not descend into this node's children (pre/breadth order only).
   * Throws in post-order, where children are already visited. A no-op when
   * called on a stale (already-passed) record.
   */
  skip(): void;
}
interface Session {
  cursor: Cursor | undefined;
  post: boolean;
  current: Visit | undefined;
}
/** Iterator of {@link Visit} records; native iterator helpers attach. */
type Visits<V = unknown> = IteratorObject<Visit<V>, undefined, unknown>;
/**
 * Lazily walk every node of `root`. Pass a glob string as the second argument
 * as shorthand for `{ match }`.
 *
 * @example
 * ```js
 * for (const v of visit(doc)) console.log(v.path, v.value);
 * visit(doc, '**.email').map(v => v.value).toArray();
 * visit(doc, { order: 'post' });
 * ```
 */
declare function visit<T>(root: T, options?: VisitOptions): Visits;
declare function visit<T>(root: T, pattern: string, options?: Omit<VisitOptions, 'match'>): Visits;
//#endregion
//#region packages/neotraverse/src/safe/transform.d.ts
declare const COMMAND: unique symbol;
/** Opaque edit/control instruction produced by the {@link Edits} factory. */
interface Command {
  readonly [COMMAND]: 'replace' | 'remove' | 'skip' | 'stop';
}
/**
 * The visitor's second argument: a stateless, `this`-free factory so it can be
 * destructured (`(v, { replace, remove }) => ...`).
 */
interface Edits {
  /** Substitute `value`. Final by default; `{ descend: true }` recurses into the replacement's children. */
  replace(value: any, opts?: {
    descend?: boolean;
  }): Command;
  /** Delete this entry from its parent (arrays splice, never holes). */
  remove(): Command;
  /** Keep the value but do not descend. */
  skip(): Command;
  /** End the whole pass; edits already made still fold up. */
  stop(): Command;
}
type Visitor<V = unknown> = (v: Visit<V>, edit: Edits) => Command | undefined | void;
type Rules = Record<string, Visitor>;
interface TransformOptions extends KeyOptions {
  /** Visitor order. @default 'pre'. No 'breadth' — BFS rewrites are incoherent. */
  order?: 'pre' | 'post';
  /** Glob pattern scoping a function/array visitor; a TypeError with the Rules form. */
  match?: string;
  /** Edit the input in place instead of copy-on-write. @default false. */
  mutate?: boolean;
}
/**
 * Rewrite `root` copy-on-write. Untouched subtrees are shared with the input,
 * and `transform(x, () => {}) === x`. Pass a single visitor, an array pipeline,
 * or a Record of pattern→rule.
 *
 * @example
 * ```js
 * transform(payload, (v, { replace }) => v.key === 'password' ? replace('***') : undefined);
 * transform(doc, { '**.url': (v, { replace }) => replace(secure(v.value)) });
 * ```
 */
declare function transform<T>(root: T, visitor: Visitor | readonly Visitor[] | Rules, options?: TransformOptions): T;
//#endregion
//#region packages/neotraverse/src/safe/transform-async.d.ts
type AsyncVisitor<V = unknown> = (v: Visit<V>, edit: Edits) => Command | undefined | void | Promise<Command | undefined | void>;
type AsyncRules = Record<string, AsyncVisitor>;
interface TransformAsyncOptions extends KeyOptions {
  order?: 'pre' | 'post';
  match?: string;
  mutate?: boolean;
  /** Reject with the signal's reason at the next visited node. */
  signal?: AbortSignal;
  /** Max parallel sibling visits. @default 1 */
  concurrency?: number;
}
/**
 * Async copy-on-write transform. Visitors may be async; `concurrency` bounds
 * parallel sibling visits; `signal` cancels at the next node.
 */
declare function transformAsync<T>(root: T, visitor: AsyncVisitor | readonly AsyncVisitor[] | AsyncRules, options?: TransformAsyncOptions): Promise<T>;
//#endregion
//#region packages/neotraverse/src/safe/path.d.ts
type Path = string | readonly PropertyKey[];
declare function get<T, const P extends Path>(obj: T, path: P): Get<T, P>;
declare function get<T, const P extends Path, F>(obj: T, path: P, fallback: F): Get<T, P> | F;
declare function has(obj: unknown, path: Path): boolean;
interface WriteOptions {
  /** Edit the input in place instead of copy-on-write. @default false. */
  mutate?: boolean;
}
declare function set<T, const P extends Path>(obj: T, path: P, value: SetValue<T, P>, options?: WriteOptions): T;
type ParseKey<S extends string> = S extends `${infer N extends number}` ? N : S;
type ReplaceAll<S extends string, From extends string, To extends string> = S extends `${infer A}${From}${infer B}` ? `${A}${To}${ReplaceAll<B, From, To>}` : S;
type PtrUnescape<S extends string> = ReplaceAll<ReplaceAll<S, '~1', '/'>, '~0', '~'>;
type SplitDot<P extends string> = P extends `${infer H}.${infer R}` ? [ParseKey<H>, ...SplitDot<R>] : [ParseKey<P>];
type SplitPtr<P extends string> = P extends `${infer H}/${infer R}` ? [ParseKey<PtrUnescape<H>>, ...SplitPtr<R>] : [ParseKey<PtrUnescape<P>>];
type Keys<P extends Path> = P extends readonly PropertyKey[] ? P : P extends string ? string extends P ? PropertyKey[] : P extends `${string}\\${string}` ? PropertyKey[] : P extends '' ? [] : P extends `/${infer R}` ? SplitPtr<R> : SplitDot<P> : never;
type Step<T, K> = T extends null | undefined ? undefined : K extends keyof T ? T[K] : T extends readonly (infer E)[] ? K extends number | `${number}` ? E | undefined : undefined : T extends ReadonlyMap<infer MK, infer MV> ? K extends MK ? MV | undefined : undefined : T extends object ? string extends keyof T ? T[string & keyof T] | undefined : undefined : undefined;
type GetIn<T, K extends readonly unknown[]> = K extends readonly [infer H, ...infer R extends readonly unknown[]] ? GetIn<Step<T, H>, R> : T;
type Get<T, P extends Path> = PropertyKey[] extends Keys<P> ? unknown : GetIn<T, Keys<P>>;
type SetValue<T, P extends Path> = unknown extends Get<T, P> ? unknown : [Get<T, P>] extends [undefined] ? unknown : Get<T, P>;
//#endregion
//#region packages/neotraverse/src/safe/clone.d.ts
interface CloneOptions {
  /** Include own enumerable symbol keys. @default false */
  symbols?: boolean;
  /** Max clone depth; RangeError beyond. Unlimited when omitted. */
  maxDepth?: number;
}
/**
 * Deep clone `value`. Cycle- and prototype-preserving; shares nothing with the
 * input.
 *
 * @example
 * ```js
 * const copy = clone({ nested: { n: 1 } });
 * copy.nested.n = 2; // original unchanged
 * ```
 */
declare function clone<T>(value: T, options?: CloneOptions): T;
//#endregion
//#region packages/neotraverse/src/safe/equal.d.ts
interface EqualOptions {
  /** Per-pair override; return a boolean to decide, or `undefined` to fall through. */
  compare?: (a: unknown, b: unknown) => boolean | undefined;
  /** Include own enumerable symbol keys. @default false */
  symbols?: boolean;
  /** Max comparison depth; RangeError beyond. Unlimited when omitted. */
  maxDepth?: number;
}
/**
 * Structural equality.
 *
 * @example
 * ```js
 * equal({ a: [1] }, { a: [1] }); // true
 * equal(new Date(0), new Date(0)); // true
 * ```
 */
declare function equal(a: unknown, b: unknown, options?: EqualOptions): boolean;
//#endregion
//#region packages/neotraverse/src/safe/merge.d.ts
type ArrayStrategy = 'replace' | 'concat' | 'union' | {
  by: string | ((item: unknown) => unknown);
} | ((a: readonly unknown[], b: readonly unknown[], path: PropertyKey[]) => unknown[]);
interface MergeOptions extends KeyOptions {
  /** How to combine two arrays at the same path. @default 'replace' (overlay wins). */
  arrays?: ArrayStrategy;
  /** Per-pattern array-strategy overrides, using the same glob language. */
  at?: Record<string, ArrayStrategy>;
  /** Merge into `base` in place instead of copy-on-write. @default false. */
  mutate?: boolean;
}
type DeepPartial<T> = T extends readonly unknown[] ? T : T extends object ? { [K in keyof T]?: DeepPartial<T[K]> } : T;
type Merge<A, B> = B extends readonly unknown[] ? B : A extends readonly unknown[] ? B : A extends object ? B extends object ? { [K in keyof A | keyof B]: K extends keyof B ? K extends keyof A ? Merge<A[K], B[K]> : B[K] : K extends keyof A ? A[K] : never } : B : B;
declare function merge<T>(base: T, overlay: DeepPartial<T>, options?: MergeOptions): T;
declare function merge<A, B>(base: A, overlay: B, options?: {
  arrays?: 'replace';
  symbols?: boolean;
  maxDepth?: number;
}): Merge<A, B>;
declare function merge(base: unknown, overlay: unknown, options?: MergeOptions): unknown;
//#endregion
//#region packages/neotraverse/src/safe/diff.d.ts
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
interface DiffOptions {
  maxDepth?: number;
}
/**
 * RFC 6902 subset diff (`add`/`remove`/`replace`). The undo patch is `diff(b, a)`.
 *
 * @example
 * ```js
 * diff({ a: 1 }, { a: 2, b: 3 });
 * // [{ op: 'replace', path: '/a', value: 2 }, { op: 'add', path: '/b', value: 3 }]
 * ```
 */
declare function diff(a: unknown, b: unknown, options?: DiffOptions): PatchOp[];
/**
 * Apply RFC 6902 ops copy-on-write. `patch(x, []) === x`; `{ mutate: true }`
 * applies in place. Throws on `move`/`copy`/`test` ops.
 */
declare function patch<T>(value: T, ops: readonly PatchOp[], options?: WriteOptions): T;
//#endregion
//#region packages/neotraverse/src/safe/refs.d.ts
interface RefOptions {
  maxDepth?: number;
}
/**
 * Resolve local `#/json/pointer` `$ref` objects.
 *
 * @example
 * ```js
 * resolveRefs({ defs: { Pet: { type: 'object' } }, pet: { $ref: '#/defs/Pet' } });
 * // => { defs: { Pet: { type: 'object' } }, pet: { type: 'object' } }
 * ```
 */
declare function resolveRefs<T>(root: T, options?: RefOptions): T;
//#endregion
export { type ArrayStrategy, type AsyncRules, type AsyncVisitor, type CloneOptions, type Command, type DeepPartial, type DiffOptions, type Edits, type EqualOptions, type Get, type Merge, type MergeOptions, type PatchOp, type Path, type RefOptions, type Rules, type SetValue, type TransformAsyncOptions, type TransformOptions, Visit, type VisitOptions, type Visitor, type Visits, type WriteOptions, clone, diff, equal, get, has, merge, patch, resolveRefs, set, transform, transformAsync, visit };