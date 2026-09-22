import { r as TraverseOptions, t as TraverseContext } from "./utils-Bfb1gHfd.js";

//#region packages/neotraverse/src/modern.d.ts
/**
 * @deprecated The `Traverse` class is deprecated and will be removed in v2. Import the
 * standalone functions from `neotraverse` instead. See the migration guide.
 */
declare class Traverse {
  #private;
  /** @deprecated Use standalone functions from `neotraverse` instead. */
  constructor(obj: any, options?: TraverseOptions);
  /** @deprecated Use `get(obj, path, options)` instead. */
  get(paths: PropertyKey[]): any;
  /** @deprecated Use `has(obj, path, options)` instead. */
  has(paths: PropertyKey[]): boolean;
  /** @deprecated Use `set(obj, path, value, options)` instead. */
  set(path: PropertyKey[], value: any): any;
  /** @deprecated Use `map(obj, cb, options)` instead. */
  map(cb: (ctx: TraverseContext, v: any) => void): any;
  /** @deprecated Use `forEach(obj, cb, options)` instead. */
  forEach(cb: (ctx: TraverseContext, v: any) => void): any;
  /** @deprecated Use `reduce(obj, cb, init?, options?)` instead. */
  reduce(cb: (ctx: TraverseContext, acc: any, v: any) => any, init?: any): any;
  /** @deprecated Use `paths(obj, options)` instead. */
  paths(): PropertyKey[][];
  /** @deprecated Use `nodes(obj, options)` instead. */
  nodes(): any[];
  /** @deprecated Use `clone(obj, options)` instead. */
  clone(): any;
}
//#endregion
export { Traverse, type TraverseContext, type TraverseOptions };