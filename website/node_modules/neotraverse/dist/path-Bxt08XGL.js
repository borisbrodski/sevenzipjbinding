//#region packages/neotraverse/src/utils.ts
const to_string = (obj) => Object.prototype.toString.call(obj);
const is_typed_array = (value) => ArrayBuffer.isView(value) && to_string(value) !== "[object DataView]";
const is_array = Array.isArray;
const is_boxed_primitive = (obj) => {
	const tag = to_string(obj);
	if (tag !== "[object Boolean]" && tag !== "[object Number]" && tag !== "[object String]") return false;
	try {
		return typeof obj.valueOf() !== "object";
	} catch {
		return false;
	}
};
const gopd = Object.getOwnPropertyDescriptor;
const is_property_enumerable = Object.prototype.propertyIsEnumerable;
const get_own_property_symbols = Object.getOwnPropertySymbols;
const has_own_property = Object.prototype.hasOwnProperty;
const object_keys = Object.keys;
const object_proto = Object.prototype;
const get_proto = Object.getPrototypeOf;
const INT_RE = /^\d+$/;
const GLOB_INDEX_RE = /^(.+)\[\*\]$/;
const PTR_UNESCAPE_SLASH_RE = /~1/g;
const PTR_UNESCAPE_TILDE_RE = /~0/g;
const TILDE_RE = /~/g;
const SLASH_RE = /\//g;
const is_unsafe_key = (key) => {
	const k = typeof key === "object" && key !== null ? String(key) : key;
	return k === "__proto__" || k === "constructor" || k === "prototype";
};
function safe_set(dst, key, value) {
	if (typeof key === "object" && key !== null) key = String(key);
	if (key === "__proto__") Object.defineProperty(dst, key, {
		value,
		writable: true,
		enumerable: true,
		configurable: true
	});
	else dst[key] = value;
}
function assert_within_depth(depth, max_depth) {
	if (max_depth !== void 0 && depth > max_depth) throw new RangeError(`neotraverse: maximum traversal depth (${max_depth}) exceeded`);
}
function own_enumerable_keys(obj) {
	const res = object_keys(obj);
	const symbols = get_own_property_symbols(obj);
	for (let i = 0; i < symbols.length; i++) if (is_property_enumerable.call(obj, symbols[i])) res.push(symbols[i]);
	return res;
}
function is_non_writable(object, key) {
	return !gopd(object, key)?.writable;
}
const empty_null = {
	includeSymbols: false,
	immutable: false
};
function clamp_concurrency(c) {
	return typeof c === "number" && c >= 1 ? Math.floor(c) : 1;
}
function array_numeric(keys) {
	for (let i = 0; i < keys.length; i++) {
		const k = keys[i];
		if (typeof k === "string" && "" + +k === k) keys[i] = +k;
	}
	return keys;
}
function array_keys(node, keys) {
	const len = node.length;
	if (keys.length === len) {
		for (let i = 0; i < len; i++) keys[i] = i;
		return keys;
	}
	return array_numeric(keys);
}
function map_set_child_keys(node) {
	if (node instanceof Map) {
		const keys = [];
		for (const k of node.keys()) keys.push(k);
		return keys;
	}
	const keys = [];
	let i = 0;
	for (const _ of node) {
		keys.push(i);
		i++;
	}
	return keys;
}
function get_child_at(node, key, descend_map_set) {
	if (descend_map_set && node instanceof Map) return node.get(key);
	if (descend_map_set && node instanceof Set) return [...node][key];
	return node[key];
}
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
function getType(value) {
	if (value === null) return "null";
	const t = typeof value;
	if (t === "function") return "function";
	if (t !== "object") return "primitive";
	if (is_array(value)) return "array";
	if (value instanceof Date) return "date";
	if (value instanceof RegExp) return "regexp";
	if (value instanceof Map) return "map";
	if (value instanceof Set) return "set";
	if (value instanceof WeakMap) return "weakmap";
	if (value instanceof WeakSet) return "weakset";
	if (is_typed_array(value)) return "typed-array";
	if (value instanceof ArrayBuffer) return "arraybuffer";
	if (value instanceof DataView) return "dataview";
	if (value instanceof Error) return "error";
	return "object";
}
function coerceKey(segment) {
	if (INT_RE.test(segment)) {
		const n = Number(segment);
		if (Number.isSafeInteger(n) && String(n) === segment) return n;
	}
	return segment;
}
function assertSafePath(keys) {
	for (let i = 0; i < keys.length; i++) if (is_unsafe_key(keys[i])) throw new Error(`neotraverse: unsafe path segment "${String(keys[i])}"`);
}
const same_value_zero = (x, y) => x === y || x !== x && y !== y;
//#endregion
//#region packages/neotraverse/src/clone.ts
function clone_node(src, seen, options, depth) {
	if (typeof src !== "object" || src === null) return src;
	assert_within_depth(depth, options.maxDepth);
	const existing = seen.get(src);
	if (existing !== void 0) return existing;
	if (src instanceof Map) {
		const dst = /* @__PURE__ */ new Map();
		seen.set(src, dst);
		for (const [k, v] of src) dst.set(clone_node(k, seen, options, depth + 1), clone_node(v, seen, options, depth + 1));
		seen.delete(src);
		return dst;
	}
	if (src instanceof Set) {
		const dst = /* @__PURE__ */ new Set();
		seen.set(src, dst);
		for (const v of src) dst.add(clone_node(v, seen, options, depth + 1));
		seen.delete(src);
		return dst;
	}
	if (src instanceof WeakMap || src instanceof WeakSet) {
		seen.set(src, src);
		return src;
	}
	if (src instanceof ArrayBuffer) {
		const dst = src.slice(0);
		seen.set(src, dst);
		return dst;
	}
	if (src instanceof DataView) {
		const dst = new DataView(src.buffer.slice(src.byteOffset, src.byteOffset + src.byteLength), 0, src.byteLength);
		seen.set(src, dst);
		return dst;
	}
	const dst = make_shell(src);
	if (!shell_keyed) return dst;
	seen.set(src, dst);
	const keys = options.includeSymbols ? own_enumerable_keys(src) : object_keys(src);
	for (let i = 0; i < keys.length; i++) {
		const key = keys[i];
		safe_set(dst, key, clone_node(src[key], seen, options, depth + 1));
	}
	if (dst instanceof Error && "cause" in src) dst.cause = clone_node(src.cause, seen, options, depth + 1);
	seen.delete(src);
	return dst;
}
let shell_keyed = false;
function make_shell(src) {
	if (is_array(src)) {
		shell_keyed = true;
		return new Array(src.length);
	}
	shell_keyed = false;
	if (src instanceof ArrayBuffer) return src.slice(0);
	if (src instanceof DataView) return new DataView(src.buffer.slice(src.byteOffset, src.byteOffset + src.byteLength), 0, src.byteLength);
	if (is_typed_array(src)) return src.slice();
	if (is_boxed_primitive(src)) return Object(src);
	if (src instanceof Map) return new Map(src);
	if (src instanceof Set) return new Set(src);
	if (src instanceof WeakMap || src instanceof WeakSet) return src;
	const tag = to_string(src);
	if (tag === "[object Date]" && typeof src.getTime === "function") {
		shell_keyed = true;
		return new Date(src.getTime());
	}
	if (tag === "[object RegExp]" && typeof src.source === "string") try {
		const re = new RegExp(src.source, src.flags);
		re.lastIndex = src.lastIndex;
		shell_keyed = true;
		return re;
	} catch {}
	if (tag === "[object Error]") {
		const Ctor = typeof src.constructor === "function" ? src.constructor : Error;
		let dst;
		try {
			dst = new Ctor(src.message);
		} catch {
			dst = new Error(src.message);
		}
		if (src.name !== dst.name) dst.name = src.name;
		if (src.stack !== void 0) dst.stack = src.stack;
		if ("cause" in src) dst.cause = src.cause;
		shell_keyed = true;
		return dst;
	}
	if (tag === "[object Map]") {
		shell_keyed = false;
		return new Map(src);
	}
	if (tag === "[object Set]") {
		shell_keyed = false;
		return new Set(src);
	}
	if (tag === "[object ArrayBuffer]") {
		shell_keyed = false;
		return src.slice(0);
	}
	if (tag === "[object DataView]") {
		shell_keyed = false;
		const dv = src;
		return new DataView(dv.buffer.slice(dv.byteOffset, dv.byteOffset + dv.byteLength), 0, dv.byteLength);
	}
	shell_keyed = true;
	const proto = get_proto(src);
	return proto === object_proto ? {} : Object.create(proto);
}
function copy(src, options) {
	if (typeof src === "object" && src !== null) {
		const dst = make_shell(src);
		if (!shell_keyed) return dst;
		const keys = options.includeSymbols ? own_enumerable_keys(src) : object_keys(src);
		for (let i = 0; i < keys.length; i++) {
			const key = keys[i];
			safe_set(dst, key, src[key]);
		}
		return dst;
	}
	return src;
}
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
function clone(obj, options) {
	if (typeof obj !== "object" || obj === null) return obj;
	return clone_node(obj, /* @__PURE__ */ new Map(), options ?? empty_null, 0);
}
//#endregion
//#region packages/neotraverse/src/context.ts
function* iterate(node, path, iter, seen, max_depth, depth) {
	yield [path, node];
	if (typeof node !== "object" || node === null) return;
	if (seen.has(node)) return;
	assert_within_depth(depth, max_depth);
	seen.add(node);
	const keys = is_array(node) ? array_keys(node, iter(node)) : iter(node);
	for (let i = 0; i < keys.length; i++) yield* iterate(node[keys[i]], path.concat(keys[i]), iter, seen, max_depth, depth + 1);
	seen.delete(node);
}
/**
* The traversal context. Every method lives on the prototype, so visiting a
* node allocates a *single* object — not a context object plus a fresh closure
* for each of `update`/`remove`/`before`/… and a separate `modifiers` object.
* That, plus the lazily-derived {@link path}, is what makes the modern build
* dramatically faster and lighter on the GC than the classic design.
*
* @see https://neotraverse.puruvj.dev/guide/context
*/
var WalkContext = class {
	node;
	node_;
	parent;
	key;
	isRoot;
	isLeaf = false;
	isFirst = false;
	isLast = false;
	level;
	circular = void 0;
	keys = null;
	w;
	keep_going = true;
	removed = false;
	mods = null;
	constructor(w, node_, node) {
		const path = w.path;
		const level = path.length;
		this.w = w;
		this.node = node;
		this.node_ = node_;
		this.parent = w.parents[level - 1];
		this.key = path[level - 1];
		this.isRoot = level === 0;
		this.level = level;
	}
	get parents() {
		return this.w.parents;
	}
	get notRoot() {
		return !this.isRoot;
	}
	get notLeaf() {
		return !this.isLeaf;
	}
	get path() {
		const out = new Array(this.level);
		let c = this;
		for (let i = this.level - 1; i >= 0; i--) {
			out[i] = c.key;
			c = c.parent;
		}
		return out;
	}
	update(x, stopHere = false) {
		if (!this.isRoot) safe_set(this.parent.node, this.key, x);
		this.node = x;
		if (stopHere) this.keep_going = false;
	}
	delete(stopHere) {
		delete this.parent.node[this.key];
		this.removed = true;
		if (stopHere) this.keep_going = false;
	}
	remove(stopHere) {
		const parent = this.parent.node;
		if (is_array(parent)) parent.splice(this.key, 1);
		else delete parent[this.key];
		this.removed = true;
		if (stopHere) this.keep_going = false;
	}
	before(f) {
		(this.mods ??= {}).before = f;
	}
	after(f) {
		(this.mods ??= {}).after = f;
	}
	pre(f) {
		(this.mods ??= {}).pre = f;
	}
	post(f) {
		(this.mods ??= {}).post = f;
	}
	stop() {
		this.w.alive = false;
	}
	block() {
		this.keep_going = false;
	}
	nextSibling() {
		const parent = this.parent;
		if (!parent?.keys || this.key === void 0) return void 0;
		const keys = parent.keys;
		const idx = keys.indexOf(this.key);
		if (idx < 0 || idx >= keys.length - 1) return void 0;
		return make_sibling_ctx(this, keys[idx + 1]);
	}
	prevSibling() {
		const parent = this.parent;
		if (!parent?.keys || this.key === void 0) return void 0;
		const keys = parent.keys;
		const idx = keys.indexOf(this.key);
		if (idx <= 0) return void 0;
		return make_sibling_ctx(this, keys[idx - 1]);
	}
};
function make_sibling_ctx(self, sibKey) {
	const w = self.w;
	const parent = self.parent;
	const sibNode = get_child_at(parent.node, sibKey, w.descend_map_set);
	const sib = new WalkContext(w, sibNode, sibNode);
	sib.parent = parent;
	sib.key = sibKey;
	sib.level = self.level;
	sib.isRoot = self.level === 0;
	if (typeof sibNode === "object" && sibNode !== null) {
		sib.keys = initial_keys(w, sibNode, w.iter);
		sib.isLeaf = sib.keys.length === 0;
	} else sib.isLeaf = true;
	return sib;
}
function make_walk_state(options = empty_null, immutable) {
	return {
		alive: true,
		immutable: immutable ?? !!options.immutable,
		iter: options.includeSymbols ? own_enumerable_keys : object_keys,
		max_depth: options.maxDepth,
		path: [],
		parents: [],
		descend_map_set: !!options.descendIntoMapSet,
		concurrency: clamp_concurrency(options.concurrency)
	};
}
function descend_children(w, ctx, node, walker, immutable, mods, fresh) {
	const { path } = w;
	const pre = mods !== null ? mods.pre : void 0;
	const post = mods !== null ? mods.post : void 0;
	if (w.descend_map_set && node instanceof Map) {
		const entries = [...node.entries()];
		const last = entries.length - 1;
		for (let index = 0; index <= last; index++) {
			if (!w.alive && !immutable) break;
			const [key, val] = entries[index];
			path.push(key);
			if (pre !== void 0) pre(ctx, val, key);
			const child = walker(val);
			if (immutable && child.node !== val) node.set(key, child.node);
			child.isLast = index === last;
			child.isFirst = index === 0;
			if (post !== void 0) post(ctx, child);
			path.pop();
		}
		return;
	}
	if (w.descend_map_set && node instanceof Set) {
		const vals = [...node];
		const last = vals.length - 1;
		for (let index = 0; index <= last; index++) {
			if (!w.alive && !immutable) break;
			path.push(index);
			if (pre !== void 0) pre(ctx, vals[index], index);
			const child = walker(vals[index]);
			if (immutable && child.node !== vals[index]) {
				node.delete(vals[index]);
				node.add(child.node);
			}
			child.isLast = index === last;
			child.isFirst = index === 0;
			if (post !== void 0) post(ctx, child);
			path.pop();
		}
		return;
	}
	const keys = ctx.keys;
	const node_is_array = is_array(node);
	let last = keys.length - 1;
	for (let index = 0; index <= last; index++) {
		if (!w.alive && !immutable) break;
		const key = keys[index];
		const childVal = node[key];
		const len_before = node_is_array ? node.length : 0;
		path.push(key);
		if (pre !== void 0) pre(ctx, childVal, key);
		const child = walker(childVal);
		if (immutable && !child.removed && node[key] !== child.node) {
			if (fresh || has_own_property.call(node, key) && !is_non_writable(node, key)) safe_set(node, key, child.node);
		}
		child.isLast = index === last;
		child.isFirst = index === 0;
		if (post !== void 0) post(ctx, child);
		path.pop();
		if (node_is_array && node.length < len_before) {
			index--;
			last--;
		}
	}
}
function update_state(ctx) {
	const node = ctx.node;
	if (typeof node === "object" && node !== null) {
		if (!ctx.keys || ctx.node_ !== node) if (ctx.w.descend_map_set && node instanceof Map) ctx.keys = map_set_child_keys(node);
		else if (ctx.w.descend_map_set && node instanceof Set) ctx.keys = map_set_child_keys(node);
		else {
			const ks = ctx.w.iter(node);
			ctx.keys = is_array(node) ? array_keys(node, ks) : ks;
		}
		ctx.isLeaf = ctx.keys.length === 0;
	} else {
		ctx.isLeaf = true;
		ctx.keys = null;
	}
}
function initial_keys(w, node0, iter) {
	if (w.descend_map_set && node0 instanceof Map) return map_set_child_keys(node0);
	if (w.descend_map_set && node0 instanceof Set) return map_set_child_keys(node0);
	const keys = iter(node0);
	return is_array(node0) ? array_keys(node0, keys) : keys;
}
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
function walk(root, cb, options = empty_null) {
	const w = make_walk_state(options);
	const { immutable, max_depth, parents, iter } = w;
	const walker = (node_) => {
		assert_within_depth(w.path.length, max_depth);
		const node0 = immutable ? copy(node_, options) : node_;
		const ctx = new WalkContext(w, node_, node0);
		if (!w.alive) return ctx;
		const node0_is_obj = typeof node0 === "object" && node0 !== null;
		if (node0_is_obj) {
			const keys0 = initial_keys(w, node0, iter);
			ctx.keys = keys0;
			ctx.isLeaf = keys0.length === 0;
			for (let i = 0; i < parents.length; i++) if (parents[i].node_ === node_) {
				ctx.circular = parents[i];
				break;
			}
		} else ctx.isLeaf = true;
		const ret = cb(ctx, node0);
		if (ret !== void 0) ctx.update(ret);
		const mods = ctx.mods;
		if (mods !== null && mods.before !== void 0) mods.before(ctx, ctx.node);
		if (!ctx.keep_going) return ctx;
		const node = ctx.node;
		const fresh = node === node0;
		if ((fresh ? node0_is_obj : typeof node === "object" && node !== null) && ctx.circular === void 0) {
			parents.push(ctx);
			if (!fresh) update_state(ctx);
			descend_children(w, ctx, node, walker, immutable, mods, fresh);
			parents.pop();
		}
		if (mods !== null && mods.after !== void 0) mods.after(ctx, ctx.node);
		return ctx;
	};
	return walker(root).node;
}
async function walk_async(root, cb, options = empty_null) {
	const w = make_walk_state(options);
	const { immutable, iter } = w;
	const signal = options.signal;
	const walker = async (node_, state = w) => {
		signal?.throwIfAborted();
		assert_within_depth(state.path.length, state.max_depth);
		const node0 = immutable ? copy(node_, options) : node_;
		const ctx = new WalkContext(state, node_, node0);
		if (!state.alive) return ctx;
		const node0_is_obj = typeof node0 === "object" && node0 !== null;
		if (node0_is_obj) {
			const keys0 = initial_keys(state, node0, iter);
			ctx.keys = keys0;
			ctx.isLeaf = keys0.length === 0;
			for (let i = 0; i < state.parents.length; i++) if (state.parents[i].node_ === node_) {
				ctx.circular = state.parents[i];
				break;
			}
		} else ctx.isLeaf = true;
		const ret = await cb(ctx, node0);
		if (ret !== void 0) ctx.update(ret);
		const mods = ctx.mods;
		if (mods !== null && mods.before !== void 0) mods.before(ctx, ctx.node);
		if (!ctx.keep_going) return ctx;
		const node = ctx.node;
		if ((node === node0 ? node0_is_obj : typeof node === "object" && node !== null) && ctx.circular === void 0) {
			state.parents.push(ctx);
			if (node !== node0) update_state(ctx);
			await descend_children_async(state, ctx, node, walker, immutable, mods);
			state.parents.pop();
		}
		if (mods !== null && mods.after !== void 0) mods.after(ctx, ctx.node);
		return ctx;
	};
	return (await walker(root)).node;
}
async function descend_children_async(w, ctx, node, walker, immutable, mods) {
	const { path, parents } = w;
	const pre = mods !== null ? mods.pre : void 0;
	const post = mods !== null ? mods.post : void 0;
	const limit = w.concurrency;
	const visitOne = async (key, childVal, index, last) => {
		const childState = {
			alive: w.alive,
			immutable: w.immutable,
			iter: w.iter,
			max_depth: w.max_depth,
			path: path.slice(),
			parents: parents.slice(),
			descend_map_set: w.descend_map_set,
			concurrency: w.concurrency
		};
		childState.path.push(key);
		if (pre !== void 0) pre(ctx, childVal, key);
		const child = await walker(childVal, childState);
		if (immutable && !child.removed) {
			if (node instanceof Map) {
				if (node.get(key) !== child.node) node.set(key, child.node);
			} else if (node instanceof Set) {
				if (childVal !== child.node) {
					node.delete(childVal);
					node.add(child.node);
				}
			} else if (node[key] !== child.node && has_own_property.call(node, key) && !is_non_writable(node, key)) safe_set(node, key, child.node);
		}
		child.isLast = index === last;
		child.isFirst = index === 0;
		if (post !== void 0) post(ctx, child);
	};
	if (w.descend_map_set && node instanceof Map) {
		const entries = [...node.entries()];
		const last = entries.length - 1;
		for (let start = 0; start <= last; start += limit) {
			if (!w.alive && !immutable) break;
			const end = Math.min(last, start + limit - 1);
			const tasks = [];
			for (let index = start; index <= end; index++) {
				const [key, val] = entries[index];
				tasks.push(visitOne(key, val, index, last));
			}
			await Promise.all(tasks);
		}
		return;
	}
	if (w.descend_map_set && node instanceof Set) {
		const vals = [...node];
		const last = vals.length - 1;
		for (let start = 0; start <= last; start += limit) {
			if (!w.alive && !immutable) break;
			const end = Math.min(last, start + limit - 1);
			const tasks = [];
			for (let index = start; index <= end; index++) tasks.push(visitOne(index, vals[index], index, last));
			await Promise.all(tasks);
		}
		return;
	}
	const keys = ctx.keys;
	const last = keys.length - 1;
	for (let start = 0; start <= last; start += limit) {
		const end = Math.min(last, start + limit - 1);
		const tasks = [];
		for (let index = start; index <= end; index++) {
			const key = keys[index];
			tasks.push(visitOne(key, node[key], index, last));
		}
		await Promise.all(tasks);
	}
}
function walk_bfs(root, cb, options = empty_null) {
	const w = make_walk_state(options);
	const immutable = w.immutable;
	const iter = w.iter;
	const queue = [{
		node_: root,
		parents: [],
		parent: void 0,
		key: void 0,
		level: 0
	}];
	let head = 0;
	let rootOut = root;
	while (head < queue.length && w.alive) {
		const { node_, parents, parent, key, level } = queue[head++];
		assert_within_depth(level, w.max_depth);
		w.parents = parents;
		const node0 = immutable ? copy(node_, options) : node_;
		const ctx = new WalkContext(w, node_, node0);
		ctx.parent = parent;
		ctx.key = key;
		ctx.level = level;
		ctx.isRoot = level === 0;
		const node0_is_obj = typeof node0 === "object" && node0 !== null;
		if (node0_is_obj) {
			const keys0 = initial_keys(w, node0, iter);
			ctx.keys = keys0;
			ctx.isLeaf = keys0.length === 0;
			for (let i = 0; i < parents.length; i++) if (parents[i].node_ === node_) {
				ctx.circular = parents[i];
				break;
			}
		} else ctx.isLeaf = true;
		const ret = cb(ctx, node0);
		if (ret !== void 0) ctx.update(ret);
		if (level === 0) rootOut = ctx.node;
		if (immutable && parent !== void 0) {
			const pk = key;
			if (parent.node[pk] !== ctx.node && has_own_property.call(parent.node, pk) && !is_non_writable(parent.node, pk)) safe_set(parent.node, pk, ctx.node);
		}
		const mods = ctx.mods;
		if (mods !== null && mods.before !== void 0) mods.before(ctx, ctx.node);
		if (!ctx.keep_going) {
			if (mods !== null && mods.after !== void 0) mods.after(ctx, ctx.node);
			continue;
		}
		const node = ctx.node;
		if (!(node === node0 ? node0_is_obj : typeof node === "object" && node !== null) || ctx.circular !== void 0) {
			if (mods !== null && mods.after !== void 0) mods.after(ctx, ctx.node);
			continue;
		}
		if (node !== node0) update_state(ctx);
		const keys = ctx.keys;
		const childParents = parents.concat(ctx);
		const childLevel = level + 1;
		const last = keys.length - 1;
		const pre = mods !== null ? mods.pre : void 0;
		if (w.descend_map_set && node instanceof Map) {
			const entries = [...node.entries()];
			for (let index = 0; index <= last; index++) {
				const [k, val] = entries[index];
				if (pre !== void 0) pre(ctx, val, k);
				queue.push({
					node_: val,
					parents: childParents,
					parent: ctx,
					key: k,
					level: childLevel
				});
			}
		} else if (w.descend_map_set && node instanceof Set) {
			const vals = [...node];
			for (let index = 0; index <= last; index++) {
				if (pre !== void 0) pre(ctx, vals[index], index);
				queue.push({
					node_: vals[index],
					parents: childParents,
					parent: ctx,
					key: index,
					level: childLevel
				});
			}
		} else for (let index = 0; index <= last; index++) {
			const k = keys[index];
			const childVal = node[k];
			if (pre !== void 0) pre(ctx, childVal, k);
			queue.push({
				node_: childVal,
				parents: childParents,
				parent: ctx,
				key: k,
				level: childLevel
			});
		}
		if (mods !== null && mods.after !== void 0) mods.after(ctx, ctx.node);
	}
	return rootOut;
}
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
function breadthFirst(obj, cb, options) {
	return walk_bfs(obj, cb, options);
}
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
function mapBfs(obj, cb, options) {
	return walk_bfs(obj, cb, {
		...options,
		immutable: true
	});
}
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
function skipWhere(pred) {
	return (ctx, value) => {
		if (pred(ctx, value)) ctx.block();
	};
}
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
function groupBy(obj, keyFn, options) {
	const buckets = /* @__PURE__ */ new Map();
	forEach(obj, (ctx, v) => {
		const k = keyFn(ctx, v);
		let arr = buckets.get(k);
		if (!arr) {
			arr = [];
			buckets.set(k, arr);
		}
		arr.push(v);
	}, options);
	return buckets;
}
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
function map(obj, cb, options) {
	return walk(obj, cb, {
		...options,
		immutable: true
	});
}
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
function forEach(obj, cb, options) {
	return walk(obj, cb, options);
}
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
function reduce(obj, cb, init, options) {
	const skip = arguments.length === 2;
	let acc = skip ? obj : init;
	forEach(obj, (ctx, x) => {
		if (!ctx.isRoot || !skip) acc = cb(ctx, acc, x);
	}, options);
	return acc;
}
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
function find(obj, fn, options) {
	let result;
	forEach(obj, (ctx, x) => {
		if (fn(ctx, x)) {
			result = x;
			ctx.stop();
		}
	}, options);
	return result;
}
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
function filter(obj, fn, options) {
	const acc = [];
	forEach(obj, (ctx, x) => {
		if (fn(ctx, x)) acc.push(x);
	}, options);
	return acc;
}
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
function some(obj, fn, options) {
	let result = false;
	forEach(obj, (ctx, x) => {
		if (fn(ctx, x)) {
			result = true;
			ctx.stop();
		}
	}, options);
	return result;
}
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
function every(obj, fn, options) {
	let result = true;
	forEach(obj, (ctx, x) => {
		if (!fn(ctx, x)) {
			result = false;
			ctx.stop();
		}
	}, options);
	return result;
}
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
function paths(obj, options) {
	const acc = [];
	forEach(obj, (ctx) => {
		acc.push(ctx.path);
	}, options);
	return acc;
}
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
function nodes(obj, options) {
	const acc = [];
	forEach(obj, (ctx) => {
		acc.push(ctx.node);
	}, options);
	return acc;
}
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
function* entries(obj, options) {
	yield* iterate(obj, [], options?.includeSymbols ? own_enumerable_keys : object_keys, /* @__PURE__ */ new Set(), options?.maxDepth, 0);
}
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
function* values(obj, options) {
	yield* iterate_values(obj, options?.includeSymbols ? own_enumerable_keys : object_keys, /* @__PURE__ */ new Set(), options?.maxDepth, 0);
}
function* iterate_values(node, iter, seen, max_depth, depth) {
	yield node;
	if (typeof node !== "object" || node === null) return;
	if (seen.has(node)) return;
	assert_within_depth(depth, max_depth);
	seen.add(node);
	const keys = iter(node);
	for (let i = 0; i < keys.length; i++) yield* iterate_values(node[keys[i]], iter, seen, max_depth, depth + 1);
	seen.delete(node);
}
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
async function forEachAsync(obj, cb, options) {
	return walk_async(obj, cb, options);
}
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
async function mapAsync(obj, cb, options) {
	return walk_async(obj, cb, {
		...options,
		immutable: true
	});
}
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
function count(obj, fn, options) {
	let n = 0;
	forEach(obj, (ctx, x) => {
		if (!fn || fn(ctx, x)) n++;
	}, options);
	return n;
}
/**
* @example
* ```js
* import { size } from 'neotraverse/modern';
* size({ a: { b: 1 }, c: 2 }); // => 4 (root + a + b + c)
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/paths#t-count
*/
function size(obj, options) {
	return count(obj, void 0, options);
}
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
function deleteWhere(obj, fn, options) {
	return map(obj, (ctx, x) => {
		if (fn(ctx, x)) ctx.remove();
	}, options);
}
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
function prune(obj, fn, options) {
	return deleteWhere(obj, (ctx, x) => !fn(ctx, x), options);
}
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
function sanitize(obj, options) {
	return deleteWhere(obj, (ctx) => is_unsafe_key(ctx.key), options);
}
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
function pruneDeep(obj, maxDepth, replacement = null, options) {
	return map(obj, (ctx) => {
		if (ctx.level > maxDepth) {
			ctx.update(replacement);
			ctx.block();
		}
	}, options);
}
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
function freeze(obj, options) {
	forEach(obj, (ctx) => {
		ctx.after(() => {
			const n = ctx.node;
			if (typeof n === "object" && n !== null) Object.freeze(n);
		});
	}, options);
	return obj;
}
//#endregion
//#region packages/neotraverse/src/path.ts
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
function get(obj, paths, options) {
	let node = obj;
	const symbols = options?.includeSymbols;
	for (let i = 0; i < paths.length; i++) {
		if (node === null || node === void 0) return void 0;
		const key = paths[i];
		if (!symbols && typeof key === "symbol" || !has_own_property.call(node, key)) return;
		node = node[key];
	}
	return node;
}
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
function has(obj, paths, options) {
	let node = obj;
	const symbols = options?.includeSymbols;
	for (let i = 0; i < paths.length; i++) {
		if (node === null || node === void 0) return false;
		const key = paths[i];
		if (!symbols && typeof key === "symbol" || !has_own_property.call(node, key)) return false;
		node = node[key];
	}
	return true;
}
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
function set(obj, path, value, _options) {
	const n = path.length;
	if (n === 0) return value;
	const keys = new Array(n);
	for (let i = 0; i < n; i++) {
		const k = path[i];
		keys[i] = typeof k === "object" && k !== null ? String(k) : k;
	}
	for (let i = 0; i < n; i++) if (is_unsafe_key(keys[i])) return value;
	let node = obj;
	for (let i = 0; i < n - 1; i++) {
		const key = keys[i];
		if (!has_own_property.call(node, key)) node[key] = typeof keys[i + 1] === "number" ? [] : {};
		node = node[key];
	}
	safe_set(node, keys[n - 1], value);
	return value;
}
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
function parsePath(path) {
	if (path.startsWith("/")) return parseJsonPointer(path);
	return parseDotPath(path);
}
/**
* @example
* ```js
* import { parseDotPath } from 'neotraverse/modern';
* parseDotPath('users[0].name'); // => ['users', '0', 'name']
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
*/
function parseDotPath(path) {
	const keys = [];
	let cur = "";
	for (let i = 0; i < path.length; i++) {
		if (path[i] === "\\" && path[i + 1] === ".") {
			cur += ".";
			i++;
			continue;
		}
		if (path[i] === ".") {
			if (cur.length) keys.push(coerceKey(cur));
			cur = "";
			continue;
		}
		cur += path[i];
	}
	if (cur.length) keys.push(coerceKey(cur));
	assertSafePath(keys);
	return keys;
}
/**
* @example
* ```js
* import { parseJsonPointer } from 'neotraverse/modern';
* parseJsonPointer('/defs/Pet'); // => ['defs', 'Pet']
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
*/
function parseJsonPointer(pointer) {
	if (pointer === "") return [];
	if (!pointer.startsWith("/")) throw new Error("neotraverse: JSON Pointer must start with \"/\"");
	const keys = pointer.slice(1).split("/").map((seg) => coerceKey(seg.replace(PTR_UNESCAPE_SLASH_RE, "/").replace(PTR_UNESCAPE_TILDE_RE, "~")));
	assertSafePath(keys);
	return keys;
}
/**
* @example
* ```js
* import { pointerPath } from 'neotraverse/modern';
* pointerPath(['user', 0, 'name']); // => '/user/0/name'
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
*/
function pointerPath(path) {
	let out = "";
	for (let i = 0; i < path.length; i++) {
		let s = String(path[i]);
		if (s.indexOf("~") !== -1 || s.indexOf("/") !== -1) s = s.replace(TILDE_RE, "~0").replace(SLASH_RE, "~1");
		out += "/" + s;
	}
	return out;
}
/**
* @example
* ```js
* import { getPath } from 'neotraverse/modern';
* getPath({ user: { id: 1 } }, 'user.id'); // => 1
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
*/
function getPath(obj, path, options) {
	return get(obj, parsePath(path), options);
}
/**
* @example
* ```js
* import { hasPath } from 'neotraverse/modern';
* hasPath({ user: { id: 1 } }, 'user.email'); // => false
* ```
*
* @see https://neotraverse.puruvj.dev/guide/api/paths#t-string-paths
*/
function hasPath(obj, path, options) {
	return has(obj, parsePath(path), options);
}
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
function setPath(obj, path, value, options) {
	return set(obj, parsePath(path), value, options);
}
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
function findPaths(obj, fn, options) {
	let found;
	forEach(obj, (ctx, x) => {
		if (fn(ctx, x)) {
			found = ctx.path;
			ctx.stop();
		}
	}, options);
	return found;
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
function filterPaths(obj, fn, options) {
	const acc = [];
	forEach(obj, (ctx, x) => {
		if (fn(ctx, x)) acc.push({
			path: ctx.path,
			node: ctx.node
		});
	}, options);
	return acc;
}
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
function parseGlob(glob) {
	return glob.split(".").filter((s) => s.length > 0).map((part) => {
		if (part === "*") return { kind: "any" };
		const m = part.match(GLOB_INDEX_RE);
		if (m) return {
			kind: "keyAnyIndex",
			key: m[1]
		};
		return {
			kind: "literal",
			key: part
		};
	});
}
function pathMatches(path, segs) {
	let i = 0;
	for (let s = 0; s < segs.length; s++) {
		const seg = segs[s];
		if (seg.kind === "any") {
			if (i >= path.length) return false;
			i++;
			continue;
		}
		if (seg.kind === "literal") {
			if (i >= path.length || String(path[i]) !== seg.key) return false;
			i++;
			continue;
		}
		if (i >= path.length || String(path[i]) !== seg.key) return false;
		i++;
		if (i >= path.length) return false;
		i++;
	}
	return i === path.length;
}
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
function select(obj, glob, options) {
	const segs = parseGlob(glob);
	const acc = [];
	forEach(obj, (ctx) => {
		if (pathMatches(ctx.path, segs)) acc.push({
			path: ctx.path,
			node: ctx.node
		});
	}, options);
	return acc;
}
//#endregion
export { paths as A, clone as B, forEachAsync as C, mapAsync as D, map as E, size as F, has_own_property as G, empty_null as H, skipWhere as I, object_keys as J, is_array as K, some as L, pruneDeep as M, reduce as N, mapBfs as O, sanitize as P, values as R, forEach as S, groupBy as T, getType as U, assert_within_depth as V, get_own_property_symbols as W, same_value_zero as X, safe_set as Y, deleteWhere as _, has as a, filter as b, parseGlob as c, pointerPath as d, select as f, count as g, breadthFirst as h, getPath as i, prune as j, nodes as k, parseJsonPointer as l, setPath as m, findPaths as n, hasPath as o, set as p, is_boxed_primitive as q, get as r, parseDotPath as s, filterPaths as t, parsePath as u, entries as v, freeze as w, find as x, every as y, walk as z };
