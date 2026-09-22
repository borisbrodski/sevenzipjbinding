import { A as paths, B as clone, E as map, N as reduce, S as forEach, a as has, k as nodes, p as set, r as get } from "./path-Bxt08XGL.js";
//#region packages/neotraverse/src/modern.ts
/**
* @deprecated The `Traverse` class is deprecated and will be removed in v2. Import the
* standalone functions from `neotraverse` instead. See the migration guide.
*/
var Traverse = class {
	#value;
	#options;
	/** @deprecated Use standalone functions from `neotraverse` instead. */
	constructor(obj, options = {}) {
		this.#value = obj;
		this.#options = options;
	}
	/** @deprecated Use `get(obj, path, options)` instead. */
	get(paths) {
		return get(this.#value, paths, this.#options);
	}
	/** @deprecated Use `has(obj, path, options)` instead. */
	has(paths) {
		return has(this.#value, paths, this.#options);
	}
	/** @deprecated Use `set(obj, path, value, options)` instead. */
	set(path, value) {
		return set(this.#value, path, value, this.#options);
	}
	/** @deprecated Use `map(obj, cb, options)` instead. */
	map(cb) {
		return map(this.#value, cb, this.#options);
	}
	/** @deprecated Use `forEach(obj, cb, options)` instead. */
	forEach(cb) {
		this.#value = forEach(this.#value, cb, this.#options);
		return this.#value;
	}
	/** @deprecated Use `reduce(obj, cb, init?, options?)` instead. */
	reduce(cb, init) {
		if (arguments.length === 1) {
			let acc = this.#value;
			forEach(this.#value, (ctx, x) => {
				if (!ctx.isRoot) acc = cb(ctx, acc, x);
			}, this.#options);
			return acc;
		}
		return reduce(this.#value, cb, init, this.#options);
	}
	/** @deprecated Use `paths(obj, options)` instead. */
	paths() {
		return paths(this.#value, this.#options);
	}
	/** @deprecated Use `nodes(obj, options)` instead. */
	nodes() {
		return nodes(this.#value, this.#options);
	}
	/** @deprecated Use `clone(obj, options)` instead. */
	clone() {
		return clone(this.#value, this.#options);
	}
};
//#endregion
export { Traverse };
