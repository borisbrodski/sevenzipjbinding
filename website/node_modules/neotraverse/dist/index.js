import { A as paths, B as clone, C as forEachAsync, D as mapAsync, E as map, F as size, G as has_own_property, H as empty_null, I as skipWhere, J as object_keys, K as is_array, L as some, M as pruneDeep, N as reduce, O as mapBfs, P as sanitize, R as values, S as forEach, T as groupBy, U as getType, V as assert_within_depth, W as get_own_property_symbols, X as same_value_zero, Y as safe_set, _ as deleteWhere, a as has, b as filter, c as parseGlob, d as pointerPath, f as select, g as count, h as breadthFirst, i as getPath, j as prune, k as nodes, l as parseJsonPointer, m as setPath, n as findPaths, o as hasPath, p as set, q as is_boxed_primitive, r as get, s as parseDotPath, t as filterPaths, u as parsePath, v as entries, w as freeze, x as find, y as every, z as walk } from "./path-Bxt08XGL.js";
//#region packages/neotraverse/src/ops.ts
function mergeable_object_type(t) {
	return t === "object" || t === "array" || t === "map";
}
function merge_pair(target, source, options, cloneOpts, depth, seen) {
	if (source === void 0) return clone(target, cloneOpts);
	const st = getType(source);
	const tt = getType(target);
	if (st === "null" || st === "primitive" || st === "function") return clone(source, cloneOpts);
	if (!mergeable_object_type(st) || !mergeable_object_type(tt) || st !== tt) return clone(source, cloneOpts);
	if (options.maxDepth !== void 0 && depth >= options.maxDepth) return clone(source, cloneOpts);
	if (seen.has(source)) return clone(source, cloneOpts);
	seen.add(source);
	let out;
	if (st === "array") if (options.array === "concat") {
		out = new Array(target.length + source.length);
		for (let i = 0; i < target.length; i++) out[i] = clone(target[i], cloneOpts);
		for (let i = 0; i < source.length; i++) out[target.length + i] = clone(source[i], cloneOpts);
	} else {
		out = new Array(source.length);
		for (let i = 0; i < source.length; i++) if (i < target.length && mergeable_object_type(getType(target[i])) && mergeable_object_type(getType(source[i]))) out[i] = merge_pair(target[i], source[i], options, cloneOpts, depth + 1, seen);
		else out[i] = clone(source[i], cloneOpts);
	}
	else if (st === "map") {
		out = /* @__PURE__ */ new Map();
		for (const [k, v] of target) if (source.has(k)) {
			const sv = source.get(k);
			out.set(k, mergeable_object_type(getType(v)) && mergeable_object_type(getType(sv)) ? merge_pair(v, sv, options, cloneOpts, depth + 1, seen) : clone(sv, cloneOpts));
		} else out.set(k, clone(v, cloneOpts));
		for (const [k, v] of source) if (!target.has(k)) out.set(k, clone(v, cloneOpts));
	} else {
		out = {};
		const tkeys = object_keys(target);
		for (let i = 0; i < tkeys.length; i++) {
			const k = tkeys[i];
			if (has_own_property.call(source, k)) if (mergeable_object_type(getType(target[k])) && mergeable_object_type(getType(source[k]))) safe_set(out, k, merge_pair(target[k], source[k], options, cloneOpts, depth + 1, seen));
			else safe_set(out, k, clone(source[k], cloneOpts));
			else safe_set(out, k, clone(target[k], cloneOpts));
		}
		const skeys = object_keys(source);
		for (let i = 0; i < skeys.length; i++) {
			const k = skeys[i];
			if (!has_own_property.call(target, k)) safe_set(out, k, clone(source[k], cloneOpts));
		}
	}
	seen.delete(source);
	return out;
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
function merge(target, source, options) {
	const opts = options ?? empty_null;
	return merge_pair(target, source, opts, opts.maxDepth !== void 0 ? {
		...opts,
		maxDepth: void 0
	} : opts, 0, /* @__PURE__ */ new WeakSet());
}
function is_ref_object(node) {
	return node !== null && typeof node === "object" && !is_array(node) && typeof node.$ref === "string" && object_keys(node).length === 1 && get_own_property_symbols(node).length === 0;
}
/**
* Resolve local JSON Pointer `$ref` objects (`"#/…"`) on a cloned tree.
* External / URL refs are left unchanged.
*
* @example
* ```js
* import { dereference } from 'neotraverse/modern';
* dereference({ defs: { Pet: { type: 'object' } }, pet: { $ref: '#/defs/Pet' } });
* // => { defs: { Pet: { type: 'object' } }, pet: { type: 'object' } }
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/walk#t-dereference
*/
const NOT_RESOLVED = Symbol("unresolved");
function dereference(obj, options) {
	const localOnly = options?.localOnly !== false;
	const targetCache = /* @__PURE__ */ new Map();
	const resolveTarget = (ref0) => {
		if (targetCache.has(ref0)) return targetCache.get(ref0);
		const seen = /* @__PURE__ */ new Set();
		const chain = [];
		let ref = ref0;
		let result = NOT_RESOLVED;
		for (;;) {
			if (targetCache.has(ref)) {
				result = targetCache.get(ref);
				break;
			}
			if (localOnly && !ref.startsWith("#")) break;
			const pointer = ref.startsWith("#") ? ref.slice(1) : ref;
			if (!pointer.startsWith("/")) break;
			if (seen.has(pointer)) break;
			seen.add(pointer);
			chain.push(ref);
			const target = get(obj, parseJsonPointer(pointer), options);
			if (target === void 0) break;
			if (is_ref_object(target)) {
				ref = target.$ref;
				continue;
			}
			result = target;
			break;
		}
		for (let i = 0; i < chain.length; i++) targetCache.set(chain[i], result);
		targetCache.set(ref0, result);
		return result;
	};
	return map(obj, (ctx) => {
		if (is_ref_object(ctx.node)) {
			const target = resolveTarget(ctx.node.$ref);
			if (target !== NOT_RESOLVED) {
				ctx.update(clone(target, options));
				ctx.block();
			}
		}
	}, options);
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
function deepEqual(a, b, options) {
	const seen = /* @__PURE__ */ new Map();
	return deepEqualPair(a, b, options?.compareFn, seen, options?.maxDepth, 0);
}
function deepEqualPair(a, b, compareFn, seen, maxDepth, depth) {
	if (a === b) return true;
	assert_within_depth(depth, maxDepth);
	if (compareFn) {
		const custom = compareFn(a, b);
		if (custom !== void 0) return custom;
	}
	if (typeof a !== typeof b) return false;
	if (a === null || b === null) return a === b;
	if (is_boxed_primitive(a) || is_boxed_primitive(b)) {
		if (!is_boxed_primitive(a) || !is_boxed_primitive(b)) return false;
		return same_value_zero(Object(a).valueOf(), Object(b).valueOf());
	}
	const ta = getType(a);
	if (ta !== getType(b)) return false;
	if (ta === "primitive" || ta === "null") return same_value_zero(a, b);
	if (typeof a === "object" && typeof b === "object") {
		let pairs = seen.get(a);
		if (pairs?.has(b)) return true;
		if (!pairs) {
			pairs = /* @__PURE__ */ new Map();
			seen.set(a, pairs);
		}
		pairs.set(b, true);
	}
	switch (ta) {
		case "date": return a.getTime() === b.getTime();
		case "regexp": return a.source === b.source && a.flags === b.flags;
		case "error": return a.message === b.message && a.name === b.name;
		case "arraybuffer": {
			if (a.byteLength !== b.byteLength) return false;
			const va = new Uint8Array(a);
			const vb = new Uint8Array(b);
			for (let i = 0; i < va.length; i++) if (va[i] !== vb[i]) return false;
			return true;
		}
		case "dataview":
			if (a.byteLength !== b.byteLength) return false;
			for (let i = 0; i < a.byteLength; i++) if (a.getUint8(i) !== b.getUint8(i)) return false;
			return true;
		case "typed-array":
			if (a.length !== b.length) return false;
			for (let i = 0; i < a.length; i++) if (!same_value_zero(a[i], b[i])) return false;
			return true;
		case "map":
			if (a.size !== b.size) return false;
			for (const [k, v] of a) if (!b.has(k) || !deepEqualPair(v, b.get(k), compareFn, seen, maxDepth, depth + 1)) return false;
			return true;
		case "set": {
			if (a.size !== b.size) return false;
			let allPrimitive = true;
			for (const v of a) if (v !== null && (typeof v === "object" || typeof v === "function")) {
				allPrimitive = false;
				break;
			}
			if (allPrimitive) {
				for (const v of a) if (!b.has(v)) return false;
				return true;
			}
			const used = /* @__PURE__ */ new Set();
			for (const v of a) {
				let matched = false;
				let i = 0;
				for (const w of b) {
					if (used.has(i)) {
						i++;
						continue;
					}
					if (deepEqualPair(v, w, compareFn, seen, maxDepth, depth + 1)) {
						used.add(i);
						matched = true;
						break;
					}
					i++;
				}
				if (!matched) return false;
			}
			return true;
		}
		case "weakmap":
			if (a === b) return true;
			return false;
		case "weakset": return a === b;
		case "function": return a === b;
		case "array":
			if (a.length !== b.length) return false;
			for (let i = 0; i < a.length; i++) if (!deepEqualPair(a[i], b[i], compareFn, seen, maxDepth, depth + 1)) return false;
			return true;
		default: {
			const keysA = Object.keys(a);
			const keysB = Object.keys(b);
			if (keysA.length !== keysB.length) return false;
			for (let i = 0; i < keysA.length; i++) {
				const k = keysA[i];
				if (!has_own_property.call(b, k)) return false;
				if (!deepEqualPair(a[k], b[k], compareFn, seen, maxDepth, depth + 1)) return false;
			}
			return true;
		}
	}
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
function toJSON(obj, options) {
	const cycle = options?.cycle ?? null;
	const prepared = map(obj, (ctx) => {
		if (ctx.circular) {
			ctx.update(cycle);
			ctx.block();
			return;
		}
		const n = ctx.node;
		if (typeof n === "function" || typeof n === "symbol") ctx.update(void 0);
		else if (typeof n === "bigint") ctx.update(String(n));
	}, options);
	return JSON.stringify(prepared);
}
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
function diff(a, b, options) {
	const ops = [];
	diffPair(a, b, [], ops, /* @__PURE__ */ new WeakSet(), options?.maxDepth, 0, /* @__PURE__ */ new Map());
	return ops;
}
function diffPair(a, b, path, ops, stack, maxDepth, depth, equalCache) {
	if (a === b) return;
	assert_within_depth(depth, maxDepth);
	const ta = getType(a);
	if (ta !== getType(b) || ta === "primitive" || ta === "null") {
		ops.push({
			op: "replace",
			path: pointerPath(path),
			value: clone(b)
		});
		return;
	}
	const tracked = typeof a === "object" && a !== null;
	if (tracked) {
		if (stack.has(a)) return;
		const bs = equalCache.get(a);
		if (bs !== void 0 && bs.has(b)) return;
		stack.add(a);
	}
	const opsBefore = ops.length;
	if (ta === "array") {
		const min = Math.min(a.length, b.length);
		for (let i = 0; i < min; i++) diffPair(a[i], b[i], path.concat(i), ops, stack, maxDepth, depth + 1, equalCache);
		for (let i = a.length; i < b.length; i++) ops.push({
			op: "add",
			path: pointerPath(path.concat(i)),
			value: clone(b[i])
		});
		for (let i = a.length - 1; i >= b.length; i--) ops.push({
			op: "remove",
			path: pointerPath(path.concat(i))
		});
	} else if (ta === "map" || ta === "set" || ta === "weakmap" || ta === "weakset" || ta === "date" || ta === "regexp" || ta === "error" || ta === "typed-array" || ta === "arraybuffer" || ta === "dataview" || ta === "function") {
		if (!deepEqual(a, b, { maxDepth })) ops.push({
			op: "replace",
			path: pointerPath(path),
			value: clone(b)
		});
	} else {
		const keys = new Set([...Object.keys(a), ...Object.keys(b)]);
		for (const k of keys) {
			const p = path.concat(k);
			const ha = has_own_property.call(a, k);
			const hb = has_own_property.call(b, k);
			if (ha && !hb) ops.push({
				op: "remove",
				path: pointerPath(p)
			});
			else if (!ha && hb) ops.push({
				op: "add",
				path: pointerPath(p),
				value: clone(b[k])
			});
			else if (ha && hb) diffPair(a[k], b[k], p, ops, stack, maxDepth, depth + 1, equalCache);
		}
	}
	if (tracked) {
		stack.delete(a);
		if (ops.length === opsBefore) {
			let bs = equalCache.get(a);
			if (bs === void 0) {
				bs = /* @__PURE__ */ new Set();
				equalCache.set(a, bs);
			}
			bs.add(b);
		}
	}
}
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
function patch(obj, ops) {
	let root = clone(obj);
	for (let i = 0; i < ops.length; i++) {
		const op = ops[i];
		const keys = parsePath(op.path.startsWith("/") ? op.path : `/${op.path}`);
		if (op.op === "remove") root = removeAt(root, keys);
		else root = setAt(root, keys, op.value, op.op === "add");
	}
	return root;
}
function setAt(root, keys, value, insert) {
	if (keys.length === 0) return value;
	const parentKeys = keys.slice(0, -1);
	const last = keys[keys.length - 1];
	let parent = parentKeys.length ? get(root, parentKeys) : root;
	if (parent === void 0 || parent === null) {
		parent = typeof last === "number" ? [] : {};
		if (parentKeys.length) root = set(root, parentKeys, parent);
		else root = parent;
	}
	if (typeof last === "number" && is_array(parent)) if (insert) parent.splice(last, 0, value);
	else parent[last] = value;
	else safe_set(parent, last, value);
	return root;
}
function removeAt(root, keys) {
	if (keys.length === 0) return root;
	const parentKeys = keys.slice(0, -1);
	const last = keys[keys.length - 1];
	const parent = parentKeys.length ? get(root, parentKeys) : root;
	if (parent == null) return root;
	if (is_array(parent) && typeof last === "number") parent.splice(last, 1);
	else delete parent[last];
	return root;
}
//#endregion
export { breadthFirst, clone, count, deepEqual, deleteWhere, dereference, diff, entries, every, filter, filterPaths, find, findPaths, forEach, forEachAsync, freeze, get, getPath, getType, groupBy, has, hasPath, map, mapAsync, mapBfs, merge, nodes, parseDotPath, parseGlob, parseJsonPointer, parsePath, patch, paths, pointerPath, prune, pruneDeep, reduce, sanitize, select, set, setPath, size, skipWhere, some, toJSON, values, walk };
