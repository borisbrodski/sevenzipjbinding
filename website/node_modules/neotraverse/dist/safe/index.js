//#region packages/neotraverse/src/safe/kernel/keys.ts
const object_keys$2 = Object.keys;
const has_own = Object.prototype.hasOwnProperty;
const get_proto = Object.getPrototypeOf;
const object_proto = Object.prototype;
const is_array = Array.isArray;
const get_symbols$3 = Object.getOwnPropertySymbols;
const is_enumerable$3 = Object.prototype.propertyIsEnumerable;
const to_string$1 = (x) => Object.prototype.toString.call(x);
const same_value_zero = (x, y) => x === y || x !== x && y !== y;
const is_typed_array = (value) => ArrayBuffer.isView(value) && to_string$1(value) !== "[object DataView]";
function type_tag(value) {
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
const is_unsafe_key = (key) => {
	const k = typeof key === "object" && key !== null ? String(key) : key;
	return k === "__proto__" || k === "constructor" || k === "prototype";
};
/**
* Assign without ever triggering the `__proto__` setter or mutating
* [[Prototype]]. Injected data is neutralized as an inert own key, not dropped.
*/
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
/** Depth bound: catchable RangeError, never a native stack overflow. */
function assert_depth(depth, max_depth) {
	if (max_depth !== void 0 && depth > max_depth) throw new RangeError(`neotraverse: maximum depth (${max_depth}) exceeded`);
}
const INT_RE = /^\d+$/;
function coerce_key(segment) {
	if (INT_RE.test(segment)) {
		const n = Number(segment);
		if (Number.isSafeInteger(n) && String(n) === segment) return n;
	}
	return segment;
}
function own_keys$3(node, symbols) {
	const keys = object_keys$2(node);
	if (symbols) {
		const syms = get_symbols$3(node);
		for (let i = 0; i < syms.length; i++) if (is_enumerable$3.call(node, syms[i])) keys.push(syms[i]);
	}
	return keys;
}
function array_keys(node, keys) {
	const len = node.length;
	if (keys.length === len) {
		for (let i = 0; i < len; i++) keys[i] = i;
		return keys;
	}
	for (let i = 0; i < keys.length; i++) {
		const k = keys[i];
		if (typeof k === "string" && "" + +k === k) keys[i] = +k;
	}
	return keys;
}
/**
* Children of `node` under the one key model, or `null` for a leaf.
* Map children are its values keyed by their Map keys; Set children are its
* elements keyed by insertion index — recursively at every level when `mapSet`.
*/
function list_keys(node, symbols, mapSet) {
	if (typeof node !== "object" || node === null) return null;
	if (node instanceof Map) {
		if (!mapSet) return null;
		return [...node.keys()];
	}
	if (node instanceof Set) {
		if (!mapSet) return null;
		const keys = new Array(node.size);
		for (let i = 0; i < node.size; i++) keys[i] = i;
		return keys;
	}
	if (is_typed_array(node) || node instanceof ArrayBuffer || node instanceof DataView) return null;
	if (node instanceof WeakMap || node instanceof WeakSet) return null;
	const keys = own_keys$3(node, symbols);
	return is_array(node) ? array_keys(node, keys) : keys;
}
/** Child value at `key` under the same model `list_keys` used. */
function child_at(node, key, mapSet) {
	if (mapSet && node instanceof Map) return node.get(key);
	if (mapSet && node instanceof Set) {
		let i = 0;
		for (const v of node) {
			if (i === key) return v;
			i++;
		}
		return;
	}
	return node[key];
}
/**
* A shallow copy of container `src`: same prototype, same exotic identity
* (Date/RegExp/Error/Map/Set), every own enumerable key/entry present with the
* ORIGINAL child references. The COW fold overwrites only changed children.
*/
function shallow_shell(src, symbols) {
	if (is_array(src)) {
		const dst = new Array(src.length);
		const keys = own_keys$3(src, symbols);
		for (let i = 0; i < keys.length; i++) {
			const k = keys[i];
			safe_set(dst, k, src[k]);
		}
		return dst;
	}
	if (src instanceof Map) return new Map(src);
	if (src instanceof Set) return new Set(src);
	const tag = to_string$1(src);
	let dst;
	if (tag === "[object Date]" && typeof src.getTime === "function") dst = new Date(src.getTime());
	else if (tag === "[object RegExp]" && typeof src.source === "string") try {
		dst = new RegExp(src.source, src.flags);
		dst.lastIndex = src.lastIndex;
	} catch {
		dst = void 0;
	}
	else if (tag === "[object Error]") {
		const Ctor = typeof src.constructor === "function" ? src.constructor : Error;
		try {
			dst = new Ctor(src.message);
		} catch {
			dst = new Error(src.message);
		}
		if (src.name !== dst.name) dst.name = src.name;
		if (src.stack !== void 0) dst.stack = src.stack;
		if ("cause" in src) dst.cause = src.cause;
	}
	if (dst === void 0) {
		const proto = get_proto(src);
		dst = proto === object_proto ? {} : Object.create(proto);
	}
	const keys = own_keys$3(src, symbols);
	for (let i = 0; i < keys.length; i++) {
		const k = keys[i];
		safe_set(dst, k, src[k]);
	}
	return dst;
}
//#endregion
//#region packages/neotraverse/src/safe/kernel/pattern.ts
function unescape(raw) {
	if (raw.indexOf("\\") === -1) return raw;
	let out = "";
	for (let i = 0; i < raw.length; i++) if (raw[i] === "\\" && i + 1 < raw.length) out += raw[++i];
	else out += raw[i];
	return out;
}
function split_escaped(src, delim) {
	const parts = [];
	let cur = "";
	for (let i = 0; i < src.length; i++) {
		const c = src[i];
		if (c === "\\" && i + 1 < src.length) {
			cur += c + src[++i];
			continue;
		}
		if (c === delim) {
			parts.push(cur);
			cur = "";
			continue;
		}
		cur += c;
	}
	parts.push(cur);
	return parts;
}
function parse(pattern) {
	if (pattern === "") return [];
	return split_escaped(pattern, ".").map((raw) => {
		if (raw === "*") return { kind: "any" };
		if (raw === "**") return { kind: "deep" };
		if (raw.startsWith("{") && raw.endsWith("}") && raw.length >= 2) {
			const inner = raw.slice(1, -1);
			return {
				kind: "alt",
				keys: new Set(split_escaped(inner, ",").map(unescape).filter((k) => k !== ""))
			};
		}
		return {
			kind: "lit",
			key: unescape(raw)
		};
	});
}
function close_mask(mask, segs) {
	for (let i = 0; i < segs.length; i++) if (segs[i].kind === "deep" && mask & 1 << i) mask |= 1 << i + 1;
	return mask;
}
var MaskMatcher = class {
	initial;
	segs;
	n;
	constructor(segs) {
		this.segs = segs;
		this.n = segs.length;
		this.initial = close_mask(1, segs);
	}
	step(states, key) {
		const segs = this.segs;
		const mask = states;
		const is_symbol = typeof key === "symbol";
		const s = is_symbol ? "" : String(key);
		let next = 0;
		for (let i = 0; i < this.n; i++) {
			if (!(mask & 1 << i)) continue;
			const seg = segs[i];
			switch (seg.kind) {
				case "deep":
					next |= 1 << i;
					break;
				case "any":
					next |= 1 << i + 1;
					break;
				case "lit":
					if (!is_symbol && seg.key === s) next |= 1 << i + 1;
					break;
				case "alt":
					if (!is_symbol && seg.keys.has(s)) next |= 1 << i + 1;
					break;
			}
		}
		return close_mask(next, segs);
	}
	accepts(states) {
		return (states & 1 << this.n) !== 0;
	}
	viable(states) {
		return (states & ~(1 << this.n)) !== 0;
	}
};
var SetMatcher = class {
	initial;
	segs;
	n;
	constructor(segs) {
		this.segs = segs;
		this.n = segs.length;
		this.initial = this.close(new Set([0]));
	}
	close(states) {
		for (let i = 0; i < this.n; i++) if (this.segs[i].kind === "deep" && states.has(i)) states.add(i + 1);
		return states;
	}
	step(states, key) {
		const is_symbol = typeof key === "symbol";
		const s = is_symbol ? "" : String(key);
		const next = /* @__PURE__ */ new Set();
		for (const i of states) {
			if (i >= this.n) continue;
			const seg = this.segs[i];
			if (seg.kind === "deep") next.add(i);
			else if (seg.kind === "any") next.add(i + 1);
			else if (seg.kind === "lit") {
				if (!is_symbol && seg.key === s) next.add(i + 1);
			} else if (!is_symbol && seg.keys.has(s)) next.add(i + 1);
		}
		return this.close(next);
	}
	accepts(states) {
		return states.has(this.n);
	}
	viable(states) {
		for (const i of states) if (i < this.n) return true;
		return false;
	}
};
const CACHE_CAP = 64;
const cache = /* @__PURE__ */ new Map();
function compile_pattern(pattern) {
	if (typeof pattern !== "string") throw new TypeError("neotraverse: pattern must be a string");
	let m = cache.get(pattern);
	if (m !== void 0) return m;
	const segs = parse(pattern);
	m = segs.length <= 30 ? new MaskMatcher(segs) : new SetMatcher(segs);
	if (cache.size >= CACHE_CAP) cache.delete(cache.keys().next().value);
	cache.set(pattern, m);
	return m;
}
function reset_frame(f, node, key, parent, depth) {
	f.node = node;
	f.keys = null;
	f.index = -1;
	f.depth = depth;
	f.key = key;
	f.parent = parent;
	f.view = void 0;
	f.tx = void 0;
	f.circular = void 0;
	f.states = 0;
	f.matched = true;
	f.skipped = false;
	f.isLeaf = true;
	return f;
}
function ancestor_of(node, from) {
	for (let p = from; p !== void 0; p = p.parent) if (p.node === node) return p;
}
function make_frame(node, key, parent, depth) {
	return {
		node,
		keys: null,
		index: -1,
		depth,
		key,
		parent,
		view: void 0,
		tx: void 0,
		circular: void 0,
		states: 0,
		matched: true,
		skipped: false,
		isLeaf: true
	};
}
/**
* Depth-first ENTER/EXIT cursor. Pull one event at a time via {@link step};
* after it returns true, read {@link phase} and {@link frame}. ENTER fires when
* a node is first reached (parents before children); EXIT fires once all of a
* node's descendants have been processed (children before parents).
*/
var Cursor = class {
	phase = 1;
	frame;
	stack;
	ancestors = /* @__PURE__ */ new Set();
	symbols;
	mapSet;
	maxDepth;
	matcher;
	emitExits;
	pool = [];
	recyclable = void 0;
	constructor(root, options = {}) {
		this.symbols = !!options.symbols;
		this.mapSet = !!options.mapSet;
		this.maxDepth = options.maxDepth;
		this.matcher = options.match !== void 0 ? compile_pattern(options.match) : void 0;
		this.emitExits = options.exits !== false;
		const f = make_frame(root, void 0, void 0, 0);
		if (this.matcher) f.states = this.matcher.initial;
		this.stack = [f];
		this.frame = f;
	}
	acquire(node, key, parent, depth) {
		const pooled = this.pool.pop();
		return pooled !== void 0 ? reset_frame(pooled, node, key, parent, depth) : make_frame(node, key, parent, depth);
	}
	/** Advance to the next ENTER/EXIT event. Returns false when traversal is done. */
	step() {
		const stack = this.stack;
		if (this.recyclable !== void 0) {
			this.pool.push(this.recyclable);
			this.recyclable = void 0;
		}
		for (;;) {
			const top = stack.length - 1;
			if (top < 0) return false;
			const f = stack[top];
			if (f.index === -1) {
				this.enter(f);
				f.index = 0;
				this.frame = f;
				this.phase = 1;
				return true;
			}
			const keys = f.keys;
			if (keys !== null && !f.skipped && f.circular === void 0 && f.index < keys.length) {
				const k = keys[f.index++];
				const child = this.acquire(child_at(f.node, k, this.mapSet), k, f, f.depth + 1);
				if (this.matcher !== void 0) child.states = this.matcher.step(f.states, k);
				assert_depth(child.depth, this.maxDepth);
				stack.push(child);
				continue;
			}
			stack.pop();
			const node = f.node;
			if (f.circular === void 0 && typeof node === "object" && node !== null) this.ancestors.delete(node);
			if (this.emitExits) {
				this.frame = f;
				this.phase = 2;
				this.recyclable = f;
				return true;
			}
			this.pool.push(f);
		}
	}
	/** Mark the current node so its descendants are not visited. */
	skip() {
		this.frame.skipped = true;
	}
	/**
	* Swap the current node for `value` and descend into the REPLACEMENT's
	* children instead of the original's (transform's `replace(v, {descend:true})`).
	* The replacement node itself is not re-entered. Must be called during the
	* current frame's ENTER, before the next {@link step}.
	*/
	replaceCurrent(value) {
		const f = this.frame;
		const old = f.node;
		if (f.circular === void 0 && typeof old === "object" && old !== null) this.ancestors.delete(old);
		f.node = value;
		f.circular = void 0;
		if (typeof value === "object" && value !== null) if (this.ancestors.has(value)) {
			f.circular = ancestor_of(value, f.parent);
			f.keys = null;
		} else {
			this.ancestors.add(value);
			f.keys = list_keys(value, this.symbols, this.mapSet);
		}
		else f.keys = null;
		f.index = 0;
		f.isLeaf = f.keys === null || f.keys.length === 0;
	}
	enter(f) {
		const node = f.node;
		if (typeof node === "object" && node !== null) if (this.ancestors.has(node)) f.circular = ancestor_of(node, f.parent);
		else {
			this.ancestors.add(node);
			f.keys = list_keys(node, this.symbols, this.mapSet);
		}
		f.isLeaf = f.keys === null || f.keys.length === 0;
		const m = this.matcher;
		if (m !== void 0) {
			f.matched = m.accepts(f.states);
			if (!m.viable(f.states)) f.skipped = true;
		}
	}
	get hasMatcher() {
		return this.matcher !== void 0;
	}
};
function* breadthFrames(root, options = {}) {
	const symbols = !!options.symbols;
	const mapSet = !!options.mapSet;
	const maxDepth = options.maxDepth;
	const matcher = options.match !== void 0 ? compile_pattern(options.match) : void 0;
	const rootFrame = make_frame(root, void 0, void 0, 0);
	if (matcher) rootFrame.states = matcher.initial;
	const queue = [rootFrame];
	let head = 0;
	while (head < queue.length) {
		const f = queue[head++];
		assert_depth(f.depth, maxDepth);
		const node = f.node;
		if (typeof node === "object" && node !== null) {
			for (let p = f.parent; p !== void 0; p = p.parent) if (p.node === node) {
				f.circular = p;
				break;
			}
			if (f.circular === void 0) f.keys = list_keys(node, symbols, mapSet);
		}
		f.isLeaf = f.keys === null || f.keys.length === 0;
		if (matcher) {
			f.matched = matcher.accepts(f.states);
			if (!matcher.viable(f.states)) f.skipped = true;
		}
		yield f;
		const keys = f.keys;
		if (keys !== null && !f.skipped && f.circular === void 0) for (let i = 0; i < keys.length; i++) {
			const k = keys[i];
			const child = make_frame(child_at(node, k, mapSet), k, f, f.depth + 1);
			if (matcher) child.states = matcher.step(f.states, k);
			queue.push(child);
		}
	}
}
//#endregion
//#region packages/neotraverse/src/safe/visit.ts
/**
* One visited node. Created fresh per node and safe to retain. The control
* method {@link skip} is meaningful only during the current iteration step.
*/
var Visit = class {
	/** The node's value. */
	value;
	/** The node's key in its parent (`undefined` at the root). Map keys appear as-is. */
	key;
	/** The parent node's Visit (`undefined` at the root). */
	parent;
	/** Depth from the root (`0` at the root). */
	depth;
	/** No traversable children under the current options. */
	isLeaf;
	/** The ancestor Visit this node points back to, if it is a back-edge. */
	circular;
	/** @internal owning session, for {@link skip}. */
	s;
	constructor(session, value, key, parent, depth, isLeaf, circular) {
		this.s = session;
		this.value = value;
		this.key = key;
		this.parent = parent;
		this.depth = depth;
		this.isLeaf = isLeaf;
		this.circular = circular;
	}
	/** Key path from the root. Lazily rebuilt from the parent chain on each read. */
	get path() {
		const out = new Array(this.depth);
		let v = this;
		for (let i = this.depth - 1; i >= 0; i--) {
			out[i] = v.key;
			v = v.parent;
		}
		return out;
	}
	/** RFC 6901 JSON Pointer to this node (`/users/0/name`). Lazy. */
	get pointer() {
		const path = this.path;
		let out = "";
		for (let i = 0; i < path.length; i++) {
			let s = String(path[i]);
			if (s.indexOf("~") !== -1 || s.indexOf("/") !== -1) s = s.replace(/~/g, "~0").replace(/\//g, "~1");
			out += "/" + s;
		}
		return out;
	}
	/**
	* Do not descend into this node's children (pre/breadth order only).
	* Throws in post-order, where children are already visited. A no-op when
	* called on a stale (already-passed) record.
	*/
	skip() {
		if (this.s.post) throw new TypeError("neotraverse: skip() is not available in order: 'post' (children are already visited)");
		if (this.s.current === this && this.s.cursor) this.s.cursor.skip();
	}
};
function makeVisit(session, f, value) {
	const parent = f.parent !== void 0 ? f.parent.view : void 0;
	const circular = f.circular !== void 0 ? f.circular.view : void 0;
	const v = new Visit(session, value, f.key, parent, f.depth, f.isLeaf, circular);
	f.view = v;
	return v;
}
const IteratorBase = globalThis.Iterator ?? class {};
var VisitIterator = class extends IteratorBase {
	cursor;
	session;
	post;
	hasMatcher;
	done = false;
	result = {
		value: void 0,
		done: false
	};
	constructor(root, options) {
		super();
		this.post = options.order === "post";
		this.cursor = new Cursor(root, {
			...options,
			exits: this.post
		});
		this.hasMatcher = options.match !== void 0;
		this.session = {
			cursor: this.cursor,
			post: this.post,
			current: void 0
		};
	}
	yield(v) {
		this.session.current = v;
		this.result.value = v;
		this.result.done = false;
		return this.result;
	}
	finish() {
		this.done = true;
		this.session.current = void 0;
		this.result.value = void 0;
		this.result.done = true;
		return this.result;
	}
	next() {
		if (this.done) return this.finish();
		const cursor = this.cursor;
		while (cursor.step()) {
			const f = cursor.frame;
			if (cursor.phase === 1) {
				const v = makeVisit(this.session, f, f.node);
				if (!this.post && (!this.hasMatcher || f.matched)) return this.yield(v);
			} else if (this.post && (!this.hasMatcher || f.matched)) return this.yield(f.view);
		}
		return this.finish();
	}
	return() {
		return this.finish();
	}
};
var BreadthIterator = class extends IteratorBase {
	gen;
	session;
	hasMatcher;
	result = {
		value: void 0,
		done: false
	};
	constructor(root, options) {
		super();
		this.gen = breadthFrames(root, options);
		this.hasMatcher = options.match !== void 0;
		this.session = {
			cursor: void 0,
			post: false,
			current: void 0
		};
	}
	next() {
		for (;;) {
			const r = this.gen.next();
			if (r.done) {
				this.result.value = void 0;
				this.result.done = true;
				return this.result;
			}
			const f = r.value;
			const v = makeVisit(this.session, f, f.node);
			if (!this.hasMatcher || f.matched) {
				this.result.value = v;
				this.result.done = false;
				return this.result;
			}
		}
	}
};
function visit(root, optionsOrPattern, maybeOptions) {
	if (typeof optionsOrPattern === "function") throw new TypeError("neotraverse: visit(root, callback) is not the v1 walk(); use transform(root, visitor) for edits or `for (const v of visit(root))` for reads");
	const options = typeof optionsOrPattern === "string" ? {
		...maybeOptions,
		match: optionsOrPattern
	} : optionsOrPattern ?? {};
	return options.order === "breadth" ? new BreadthIterator(root, options) : new VisitIterator(root, options);
}
//#endregion
//#region packages/neotraverse/src/safe/transform.ts
const COMMAND = Symbol("neotraverse.command");
const REMOVE = { [COMMAND]: "remove" };
const SKIP = { [COMMAND]: "skip" };
const STOP = { [COMMAND]: "stop" };
const edit = {
	replace: (value, opts) => ({
		[COMMAND]: "replace",
		value,
		descend: !!(opts && opts.descend)
	}),
	remove: () => REMOVE,
	skip: () => SKIP,
	stop: () => STOP
};
function as_command(ret) {
	if (ret == null) return void 0;
	if (typeof ret === "object" && COMMAND in ret) return ret;
	throw new TypeError("neotraverse: visitor returned a non-command value; did you mean edit.replace(...)?");
}
const REMOVED = Symbol("removed");
function apply_edits(base, edits, mutate, symbols) {
	const tag = type_tag(base);
	if (tag === "array") {
		const out = [];
		const len = base.length;
		for (let i = 0; i < len; i++) if (edits.has(i)) {
			const e = edits.get(i);
			if (e !== REMOVED) out.push(e);
		} else out.push(base[i]);
		if (mutate) {
			base.length = 0;
			for (let i = 0; i < out.length; i++) base.push(out[i]);
			for (const [k, v] of edits) {
				if (typeof k === "number") continue;
				if (v === REMOVED) delete base[k];
				else base[k] = v;
			}
			return base;
		}
		const xkeys = symbols ? Object.keys(base).concat(Object.getOwnPropertySymbols(base)) : Object.keys(base);
		for (let j = 0; j < xkeys.length; j++) {
			const k = xkeys[j];
			if (typeof k === "string" && "" + +k === k && +k < len) continue;
			if (edits.has(k)) {
				const e = edits.get(k);
				if (e !== REMOVED) out[k] = e;
			} else out[k] = base[k];
		}
		return out;
	}
	if (tag === "map") {
		const out = mutate ? base : new Map(base);
		for (const [k, v] of edits) if (v === REMOVED) out.delete(k);
		else out.set(k, v);
		return out;
	}
	if (tag === "set") {
		const arr = [...base];
		const out = [];
		for (let i = 0; i < arr.length; i++) if (edits.has(i)) {
			const e = edits.get(i);
			if (e !== REMOVED) out.push(e);
		} else out.push(arr[i]);
		if (mutate) {
			base.clear();
			for (let i = 0; i < out.length; i++) base.add(out[i]);
			return base;
		}
		return new Set(out);
	}
	const out = mutate ? base : shallow_shell(base, symbols);
	for (const [k, v] of edits) if (v === REMOVED) delete out[k];
	else safe_set(out, k, v);
	return out;
}
function compute_fold(tx, mutate, symbols) {
	if (tx.final) return tx.result;
	if (tx.edits === null) return tx.base;
	return apply_edits(tx.base, tx.edits, mutate, symbols);
}
function dispatch$1(session, f, baseValue, fns, rules, matchScoped) {
	let current = baseValue;
	let replaced = false;
	let descend = false;
	const runOne = (fn) => {
		const cmd = as_command(fn(makeVisit(session, f, current), edit));
		if (cmd === void 0) return 0;
		if (cmd[COMMAND] === "replace") {
			current = cmd.value;
			replaced = true;
			descend = cmd.descend;
			return 0;
		}
		return cmd;
	};
	if (rules !== null) {
		const states = f.tx.states;
		for (let i = 0; i < rules.length; i++) {
			if (!rules[i].matcher.accepts(states[i])) continue;
			const out = runOne(rules[i].fn);
			if (out !== 0) return out;
		}
	} else if (!matchScoped || f.matched) for (let i = 0; i < fns.length; i++) {
		const out = runOne(fns[i]);
		if (out !== 0) return out;
	}
	if (replaced) return descend ? edit.replace(current, { descend: true }) : edit.replace(current);
}
function run(root, fns, rules, options) {
	const symbols = !!options.symbols;
	const mapSet = !!options.mapSet;
	const mutate = !!options.mutate;
	const post = options.order === "post";
	const matchScoped = rules === null && options.match !== void 0;
	const cursor = new Cursor(root, {
		symbols,
		mapSet,
		maxDepth: options.maxDepth,
		match: rules === null ? options.match : void 0
	});
	const session = {
		cursor,
		post,
		current: void 0
	};
	let stopped = false;
	let rootResult = root;
	while (cursor.step()) {
		const f = cursor.frame;
		if (cursor.phase === 1) {
			const tx = {
				edits: null,
				dirty: false,
				removed: false,
				final: false,
				base: f.node,
				result: f.node,
				states: null
			};
			f.tx = tx;
			if (rules !== null) {
				const states = new Array(rules.length);
				const parentStates = f.parent ? f.parent.tx.states : null;
				let anyViable = false;
				for (let i = 0; i < rules.length; i++) {
					const m = rules[i].matcher;
					states[i] = f.parent ? m.step(parentStates[i], f.key) : m.initial;
					if (m.viable(states[i])) anyViable = true;
				}
				tx.states = states;
				if (!anyViable) cursor.skip();
			}
			if (stopped) {
				cursor.skip();
				continue;
			}
			if (post) {
				makeVisit(session, f, f.node);
				continue;
			}
			const cmd = dispatch$1(session, f, f.node, fns, rules, matchScoped);
			if (cmd !== void 0) {
				const t = cmd[COMMAND];
				if (t === "replace") {
					const rc = cmd;
					tx.dirty = true;
					if (rc.descend) {
						tx.base = rc.value;
						cursor.replaceCurrent(rc.value);
					} else {
						tx.final = true;
						tx.result = rc.value;
						cursor.skip();
					}
				} else if (t === "remove") {
					tx.removed = true;
					tx.dirty = true;
					cursor.skip();
				} else if (t === "skip") cursor.skip();
				else {
					cursor.skip();
					stopped = true;
				}
			}
		} else {
			const tx = f.tx;
			let folded = compute_fold(tx, mutate, symbols);
			if (post && !stopped && !tx.removed && !tx.final) {
				const cmd = dispatch$1(session, f, folded, fns, rules, matchScoped);
				if (cmd !== void 0) {
					const t = cmd[COMMAND];
					if (t === "replace") {
						folded = cmd.value;
						tx.dirty = true;
					} else if (t === "remove") {
						tx.removed = true;
						tx.dirty = true;
					} else if (t === "stop") stopped = true;
				}
			}
			tx.result = tx.removed ? void 0 : folded;
			if (f.parent) {
				if (tx.dirty || tx.removed) {
					const ptx = f.parent.tx;
					(ptx.edits ??= /* @__PURE__ */ new Map()).set(f.key, tx.removed ? REMOVED : tx.result);
					ptx.dirty = true;
				}
			} else rootResult = tx.removed ? void 0 : tx.result;
		}
	}
	return rootResult;
}
function normalize(visitor) {
	if (typeof visitor === "function") return {
		fns: [visitor],
		rules: null
	};
	if (is_array(visitor)) return {
		fns: visitor,
		rules: null
	};
	const rules = [];
	for (const key of Object.keys(visitor)) rules.push({
		matcher: compile_pattern(key),
		fn: visitor[key]
	});
	return {
		fns: null,
		rules
	};
}
function transform(root, visitor, options = {}) {
	const { fns, rules } = normalize(visitor);
	if (rules !== null && options.match !== void 0) throw new TypeError("neotraverse: the Rules form carries its own patterns; do not also pass options.match");
	if (options.order === "breadth") throw new TypeError("neotraverse: transform supports order 'pre' | 'post' only (no breadth rewrites)");
	return run(root, fns, rules, options);
}
//#endregion
//#region packages/neotraverse/src/safe/transform-async.ts
async function dispatch(ctx, value, key, parentVisit, depth, isLeaf, circular, states, matched) {
	let current = value;
	let replaced = false;
	let descend = false;
	let lastVisit = new Visit(ctx.session, current, key, parentVisit, depth, isLeaf, circular);
	const runOne = async (fn) => {
		lastVisit = new Visit(ctx.session, current, key, parentVisit, depth, isLeaf, circular);
		const cmd = as_command(await fn(lastVisit, edit));
		if (cmd === void 0) return 0;
		if (cmd[COMMAND] === "replace") {
			current = cmd.value;
			replaced = true;
			descend = cmd.descend;
			return 0;
		}
		return cmd;
	};
	if (ctx.rules !== null) for (let i = 0; i < ctx.rules.length; i++) {
		if (!ctx.rules[i].matcher.accepts(states[i])) continue;
		const out = await runOne(ctx.rules[i].fn);
		if (out !== 0) return {
			cmd: out,
			lastVisit
		};
	}
	else if (ctx.matchMatcher === void 0 || matched) for (let i = 0; i < ctx.fns.length; i++) {
		const out = await runOne(ctx.fns[i]);
		if (out !== 0) return {
			cmd: out,
			lastVisit
		};
	}
	if (replaced) return {
		cmd: descend ? edit.replace(current, { descend: true }) : edit.replace(current),
		lastVisit
	};
	return {
		cmd: void 0,
		lastVisit
	};
}
async function fold(ctx, node, key, parentVisit, depth, ancestors, states, matchStates) {
	ctx.signal?.throwIfAborted();
	assert_depth(depth, ctx.maxDepth);
	const isObj = typeof node === "object" && node !== null;
	let circularHit = false;
	let circularVisit = void 0;
	if (isObj && ancestors.has(node)) {
		circularHit = true;
		for (let p = parentVisit; p !== void 0; p = p.parent) if (p.value === node) {
			circularVisit = p;
			break;
		}
	}
	let keys = isObj && !circularHit ? list_keys(node, ctx.symbols, ctx.mapSet) : null;
	const isLeaf = keys === null || keys.length === 0;
	const matched = ctx.matchMatcher === void 0 ? true : ctx.matchMatcher.accepts(matchStates);
	const viable = ctx.matchMatcher === void 0 ? true : ctx.matchMatcher.viable(matchStates);
	let anyRuleViable = true;
	if (ctx.rules !== null && states !== null) {
		anyRuleViable = false;
		for (let i = 0; i < ctx.rules.length; i++) if (ctx.rules[i].matcher.viable(states[i])) anyRuleViable = true;
	}
	let base = node;
	let result = node;
	let changed = false;
	let removed = false;
	let ownVisit = new Visit(ctx.session, node, key, parentVisit, depth, isLeaf, circularVisit);
	if (!ctx.post && !ctx.stopped && (ctx.matchMatcher === void 0 || true)) {
		const { cmd, lastVisit } = await dispatch(ctx, node, key, parentVisit, depth, isLeaf, circularVisit, states, matched);
		ownVisit = lastVisit;
		if (cmd !== void 0) {
			const t = cmd[COMMAND];
			if (t === "replace") {
				changed = true;
				if (cmd.descend) {
					base = cmd.value;
					keys = typeof base === "object" && base !== null ? list_keys(base, ctx.symbols, ctx.mapSet) : null;
				} else return {
					value: cmd.value,
					changed: true,
					removed: false
				};
			} else if (t === "remove") return {
				value: void 0,
				changed: true,
				removed: true
			};
			else if (t === "skip") return {
				value: node,
				changed: false,
				removed: false
			};
			else {
				ctx.stopped = true;
				return {
					value: node,
					changed: false,
					removed: false
				};
			}
		}
	}
	if (ctx.matchMatcher !== void 0 && !viable || ctx.rules !== null && !anyRuleViable) keys = null;
	if (keys !== null && !ctx.stopped && !circularHit) {
		const baseIsObj = typeof base === "object" && base !== null;
		if (baseIsObj) ancestors.set(base, true);
		const edits = /* @__PURE__ */ new Map();
		const tag = ctx.mapSet && base instanceof Map ? "map" : ctx.mapSet && base instanceof Set ? "set" : "";
		const childList = [];
		if (tag === "map") for (const [k, val] of base) childList.push({
			k,
			child: val
		});
		else if (tag === "set") {
			let i = 0;
			for (const val of base) childList.push({
				k: i++,
				child: val
			});
		} else for (let i = 0; i < keys.length; i++) childList.push({
			k: keys[i],
			child: base[keys[i]]
		});
		const limit = ctx.concurrency;
		for (let start = 0; start < childList.length; start += limit) {
			if (ctx.stopped) break;
			const batch = childList.slice(start, start + limit);
			const results = await Promise.all(batch.map((c) => {
				const childStates = ctx.rules !== null && states !== null ? states.map((s, i) => ctx.rules[i].matcher.step(s, c.k)) : null;
				const childMatch = ctx.matchMatcher !== void 0 ? ctx.matchMatcher.step(matchStates, c.k) : 0;
				return fold(ctx, c.child, c.k, ownVisit, depth + 1, ancestors, childStates, childMatch);
			}));
			for (let i = 0; i < results.length; i++) {
				const r = results[i];
				if (r.removed) {
					edits.set(batch[i].k, REMOVED);
					changed = true;
				} else if (r.changed) {
					edits.set(batch[i].k, r.value);
					changed = true;
				}
			}
		}
		if (baseIsObj) ancestors.delete(base);
		if (edits.size > 0) result = apply_edits(base, edits, ctx.mutate, ctx.symbols);
		else result = base;
	} else result = base;
	if (ctx.post && !ctx.stopped) {
		const { cmd } = await dispatch(ctx, result, key, parentVisit, depth, isLeaf, circularVisit, states, matched);
		if (cmd !== void 0) {
			const t = cmd[COMMAND];
			if (t === "replace") {
				result = cmd.value;
				changed = true;
			} else if (t === "remove") return {
				value: void 0,
				changed: true,
				removed: true
			};
			else if (t === "stop") ctx.stopped = true;
		}
	}
	return {
		value: result,
		changed,
		removed
	};
}
async function transformAsync(root, visitor, options = {}) {
	const { fns, rules } = normalize(visitor);
	if (rules !== null && options.match !== void 0) throw new TypeError("neotraverse: the Rules form carries its own patterns; do not also pass options.match");
	const matchMatcher = rules === null && options.match !== void 0 ? compile_pattern(options.match) : void 0;
	const ctx = {
		symbols: !!options.symbols,
		mapSet: !!options.mapSet,
		maxDepth: options.maxDepth,
		mutate: !!options.mutate,
		post: options.order === "post",
		signal: options.signal,
		concurrency: typeof options.concurrency === "number" && options.concurrency >= 1 ? Math.floor(options.concurrency) : 1,
		fns,
		rules,
		matchMatcher,
		stopped: false,
		session: {
			cursor: void 0,
			post: options.order === "post",
			current: void 0
		}
	};
	const states = rules !== null ? rules.map((r) => r.matcher.initial) : null;
	const matchStates = matchMatcher !== void 0 ? matchMatcher.initial : 0;
	const r = await fold(ctx, root, void 0, void 0, 0, /* @__PURE__ */ new Map(), states, matchStates);
	return r.removed ? void 0 : r.value;
}
//#endregion
//#region packages/neotraverse/src/safe/path.ts
function parse_dot(path) {
	const keys = [];
	let cur = "";
	for (let i = 0; i < path.length; i++) {
		const c = path[i];
		if (c === "\\" && i + 1 < path.length) {
			cur += path[++i];
			continue;
		}
		if (c === ".") {
			if (cur.length) keys.push(coerce_key(cur));
			cur = "";
			continue;
		}
		cur += c;
	}
	if (cur.length) keys.push(coerce_key(cur));
	return keys;
}
function parse_pointer(pointer) {
	if (pointer === "") return [];
	return pointer.slice(1).split("/").map((seg) => coerce_key(seg.replace(/~1/g, "/").replace(/~0/g, "~")));
}
/** Normalize any path spelling to an exact key array. Array form is taken as-is. */
function to_keys(path) {
	if (typeof path !== "string") return path;
	if (path === "") return [];
	return path[0] === "/" ? parse_pointer(path) : parse_dot(path);
}
function step_read(node, key) {
	if (node === null || node === void 0) return {
		found: false,
		value: void 0
	};
	if (is_unsafe_key(key)) return {
		found: false,
		value: void 0
	};
	if (node instanceof Map) return node.has(key) ? {
		found: true,
		value: node.get(key)
	} : {
		found: false,
		value: void 0
	};
	if (node instanceof Set) {
		if (typeof key === "number" && key >= 0 && key < node.size) {
			let i = 0;
			for (const v of node) {
				if (i === key) return {
					found: true,
					value: v
				};
				i++;
			}
		}
		return {
			found: false,
			value: void 0
		};
	}
	if (typeof node === "object" || typeof node === "string") {
		if (has_own.call(node, key)) return {
			found: true,
			value: node[key]
		};
	}
	return {
		found: false,
		value: void 0
	};
}
function get(obj, path, fallback) {
	const keys = to_keys(path);
	let node = obj;
	for (let i = 0; i < keys.length; i++) {
		const r = step_read(node, keys[i]);
		if (!r.found) return fallback;
		node = r.value;
	}
	return node;
}
function has(obj, path) {
	const keys = to_keys(path);
	let node = obj;
	for (let i = 0; i < keys.length; i++) {
		const r = step_read(node, keys[i]);
		if (!r.found) return false;
		node = r.value;
	}
	return true;
}
function write_child(node, key, value) {
	if (node instanceof Map) node.set(key, value);
	else safe_set(node, key, value);
}
function read_child(node, key) {
	if (node instanceof Map) return node.get(key);
	if (node === null || typeof node !== "object") return void 0;
	return node[key];
}
function copy_container(node, keyForKind) {
	if (node === null || typeof node !== "object") return typeof keyForKind === "number" ? [] : {};
	if (node instanceof Map) return new Map(node);
	return shallow_shell(node, true);
}
function set_cow(node, keys, i, value) {
	if (i === keys.length) return value;
	const key = keys[i];
	const isLast = i === keys.length - 1;
	let child = read_child(node, key);
	if (!isLast && (child === null || typeof child !== "object")) child = typeof keys[i + 1] === "number" ? [] : {};
	const newChild = set_cow(child, keys, i + 1, value);
	if (newChild === child && read_child(node, key) === child) return node;
	const copy = copy_container(node, key);
	write_child(copy, key, newChild);
	return copy;
}
function set_in_place(obj, keys, value) {
	let node = obj;
	for (let i = 0; i < keys.length - 1; i++) {
		const key = keys[i];
		let child = read_child(node, key);
		if (child === null || typeof child !== "object") {
			child = typeof keys[i + 1] === "number" ? [] : {};
			write_child(node, key, child);
		}
		node = child;
	}
	write_child(node, keys[keys.length - 1], value);
}
function set(obj, path, value, options) {
	const keys = to_keys(path);
	if (keys.length === 0) return value;
	for (let i = 0; i < keys.length; i++) if (is_unsafe_key(keys[i])) throw new TypeError(`neotraverse: unsafe path segment "${String(keys[i])}"`);
	if (options?.mutate) {
		set_in_place(obj, keys, value);
		return obj;
	}
	return set_cow(obj, keys, 0, value);
}
//#endregion
//#region packages/neotraverse/src/safe/clone.ts
const get_symbols$2 = Object.getOwnPropertySymbols;
const is_enumerable$2 = Object.prototype.propertyIsEnumerable;
const object_keys$1 = Object.keys;
const to_string = (x) => Object.prototype.toString.call(x);
function own_keys$2(obj, symbols) {
	const keys = object_keys$1(obj);
	if (symbols) {
		const syms = get_symbols$2(obj);
		for (let i = 0; i < syms.length; i++) if (is_enumerable$2.call(obj, syms[i])) keys.push(syms[i]);
	}
	return keys;
}
function is_boxed_primitive(obj) {
	const tag = to_string(obj);
	if (tag !== "[object Boolean]" && tag !== "[object Number]" && tag !== "[object String]") return false;
	try {
		return typeof obj.valueOf() !== "object";
	} catch {
		return false;
	}
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
	if (tag === "[object Map]") return new Map(src);
	if (tag === "[object Set]") return new Set(src);
	shell_keyed = true;
	const proto = get_proto(src);
	return proto === object_proto ? {} : Object.create(proto);
}
function clone_node(src, seen, symbols, maxDepth, depth) {
	if (typeof src !== "object" || src === null) return src;
	assert_depth(depth, maxDepth);
	const existing = seen.get(src);
	if (existing !== void 0) return existing;
	if (src instanceof Map) {
		const dst = /* @__PURE__ */ new Map();
		seen.set(src, dst);
		for (const [k, v] of src) dst.set(clone_node(k, seen, symbols, maxDepth, depth + 1), clone_node(v, seen, symbols, maxDepth, depth + 1));
		seen.delete(src);
		return dst;
	}
	if (src instanceof Set) {
		const dst = /* @__PURE__ */ new Set();
		seen.set(src, dst);
		for (const v of src) dst.add(clone_node(v, seen, symbols, maxDepth, depth + 1));
		seen.delete(src);
		return dst;
	}
	if (src instanceof WeakMap || src instanceof WeakSet) {
		seen.set(src, src);
		return src;
	}
	const dst = make_shell(src);
	if (!shell_keyed) {
		seen.set(src, dst);
		return dst;
	}
	seen.set(src, dst);
	const keys = own_keys$2(src, symbols);
	for (let i = 0; i < keys.length; i++) {
		const key = keys[i];
		safe_set(dst, key, clone_node(src[key], seen, symbols, maxDepth, depth + 1));
	}
	if (dst instanceof Error && "cause" in src) dst.cause = clone_node(src.cause, seen, symbols, maxDepth, depth + 1);
	seen.delete(src);
	return dst;
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
function clone(value, options) {
	if (typeof value !== "object" || value === null) return value;
	return clone_node(value, /* @__PURE__ */ new Map(), !!options?.symbols, options?.maxDepth, 0);
}
//#endregion
//#region packages/neotraverse/src/safe/equal.ts
const get_symbols$1 = Object.getOwnPropertySymbols;
const is_enumerable$1 = Object.prototype.propertyIsEnumerable;
function is_boxed(obj) {
	const tag = Object.prototype.toString.call(obj);
	if (tag !== "[object Boolean]" && tag !== "[object Number]" && tag !== "[object String]") return false;
	try {
		return typeof obj.valueOf() !== "object";
	} catch {
		return false;
	}
}
function own_keys$1(obj, symbols) {
	const keys = Object.keys(obj);
	if (symbols) {
		const syms = get_symbols$1(obj);
		for (let i = 0; i < syms.length; i++) if (is_enumerable$1.call(obj, syms[i])) keys.push(syms[i]);
	}
	return keys;
}
function equal_pair(a, b, compare, symbols, seen, maxDepth, depth) {
	if (a === b) return true;
	assert_depth(depth, maxDepth);
	if (compare) {
		const custom = compare(a, b);
		if (custom !== void 0) return custom;
	}
	if (typeof a !== typeof b) return false;
	if (a === null || b === null) return a === b;
	if (is_boxed(a) || is_boxed(b)) {
		if (!is_boxed(a) || !is_boxed(b)) return false;
		return same_value_zero(Object(a).valueOf(), Object(b).valueOf());
	}
	const ta = type_tag(a);
	if (ta !== type_tag(b)) return false;
	if (ta === "primitive" || ta === "null") return same_value_zero(a, b);
	if (typeof a === "object") {
		let bs = seen.get(a);
		if (bs?.has(b)) return true;
		if (!bs) {
			bs = /* @__PURE__ */ new Set();
			seen.set(a, bs);
		}
		bs.add(b);
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
			for (const [k, v] of a) if (!b.has(k) || !equal_pair(v, b.get(k), compare, symbols, seen, maxDepth, depth + 1)) return false;
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
					if (!used.has(i) && equal_pair(v, w, compare, symbols, seen, maxDepth, depth + 1)) {
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
		case "weakset":
		case "function": return a === b;
		case "array":
			if (a.length !== b.length) return false;
			for (let i = 0; i < a.length; i++) if (!equal_pair(a[i], b[i], compare, symbols, seen, maxDepth, depth + 1)) return false;
			return true;
		default: {
			const ka = own_keys$1(a, symbols);
			const kb = own_keys$1(b, symbols);
			if (ka.length !== kb.length) return false;
			for (let i = 0; i < ka.length; i++) {
				const k = ka[i];
				if (!has_own.call(b, k)) return false;
				if (!equal_pair(a[k], b[k], compare, symbols, seen, maxDepth, depth + 1)) return false;
			}
			return true;
		}
	}
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
function equal(a, b, options) {
	return equal_pair(a, b, options?.compare, !!options?.symbols, /* @__PURE__ */ new Map(), options?.maxDepth, 0);
}
//#endregion
//#region packages/neotraverse/src/safe/merge.ts
const object_keys = Object.keys;
const get_symbols = Object.getOwnPropertySymbols;
const is_enumerable = Object.prototype.propertyIsEnumerable;
function own_keys(obj, symbols) {
	const keys = object_keys(obj);
	if (symbols) {
		const syms = get_symbols(obj);
		for (let i = 0; i < syms.length; i++) if (is_enumerable.call(obj, syms[i])) keys.push(syms[i]);
	}
	return keys;
}
function mergeable(t) {
	return t === "object" || t === "array" || t === "map";
}
function resolve_strategy(ctx, states) {
	for (let i = 0; i < ctx.atEntries.length; i++) if (ctx.atEntries[i].matcher.accepts(states[i])) return ctx.atEntries[i].strategy;
	return ctx.defaultArrays;
}
function merge_arrays(base, overlay, strat, path, ctx, states) {
	if (typeof strat === "function") return strat(base, overlay, path);
	if (strat === "replace") return overlay;
	if (strat === "concat") return base.concat(overlay);
	if (strat === "union") {
		const out = base.slice();
		for (const el of overlay) {
			let dup = false;
			for (let i = 0; i < out.length; i++) if (equal(out[i], el)) {
				dup = true;
				break;
			}
			if (!dup) out.push(el);
		}
		return out;
	}
	const by = strat.by;
	const keyFn = typeof by === "function" ? by : (x) => x == null ? void 0 : x[by];
	const out = base.slice();
	const indexByKey = /* @__PURE__ */ new Map();
	for (let i = 0; i < out.length; i++) indexByKey.set(keyFn(out[i]), i);
	for (const el of overlay) {
		const k = keyFn(el);
		const at = indexByKey.get(k);
		if (at !== void 0) out[at] = merge_pair(out[at], el, path.concat(out[at]?.length ?? at), ctx, states);
		else {
			indexByKey.set(k, out.length);
			out.push(el);
		}
	}
	return out;
}
function child_states(ctx, states, key) {
	if (ctx.atEntries.length === 0) return states;
	const next = new Array(ctx.atEntries.length);
	for (let i = 0; i < ctx.atEntries.length; i++) next[i] = ctx.atEntries[i].matcher.step(states[i], key);
	return next;
}
function merge_pair(base, overlay, path, ctx, states) {
	if (overlay === void 0) return base;
	const ot = type_tag(overlay);
	const bt = type_tag(base);
	if (!mergeable(ot) || !mergeable(bt) || ot !== bt) return overlay;
	if (ctx.seen.has(overlay)) return overlay;
	ctx.seen.add(overlay);
	let result;
	if (ot === "array") {
		result = merge_arrays(base, overlay, resolve_strategy(ctx, states), path, ctx, child_states(ctx, states, 0));
		if (ctx.mutate) {
			base.length = 0;
			for (let i = 0; i < result.length; i++) base.push(result[i]);
			result = base;
		}
	} else if (ot === "map") {
		const out = ctx.mutate ? base : new Map(base);
		let changed = ctx.mutate;
		for (const [k, ov] of overlay) {
			const cs = child_states(ctx, states, k);
			if (out.has(k)) {
				const merged = merge_pair(out.get(k), ov, path.concat(k), ctx, cs);
				if (merged !== out.get(k)) changed = true;
				out.set(k, merged);
			} else {
				out.set(k, ov);
				changed = true;
			}
		}
		result = changed ? out : base;
	} else {
		const okeys = own_keys(overlay, ctx.symbols);
		const out = ctx.mutate ? base : shallow_shell(base, ctx.symbols);
		let changed = ctx.mutate;
		for (let i = 0; i < okeys.length; i++) {
			const k = okeys[i];
			const ov = overlay[k];
			const cs = child_states(ctx, states, k);
			if (has_own.call(base, k)) {
				const merged = merge_pair(base[k], ov, path.concat(k), ctx, cs);
				if (merged !== base[k]) changed = true;
				safe_set(out, k, merged);
			} else {
				safe_set(out, k, ov);
				changed = true;
			}
		}
		result = changed ? out : base;
	}
	ctx.seen.delete(overlay);
	return result;
}
function merge(base, overlay, options = {}) {
	const atEntries = options.at ? Object.keys(options.at).map((pat) => ({
		matcher: compile_pattern(pat),
		strategy: options.at[pat]
	})) : [];
	return merge_pair(base, overlay, [], {
		defaultArrays: options.arrays ?? "replace",
		atEntries,
		symbols: !!options.symbols,
		mutate: !!options.mutate,
		seen: /* @__PURE__ */ new WeakSet()
	}, atEntries.map((e) => e.matcher.initial));
}
//#endregion
//#region packages/neotraverse/src/safe/diff.ts
function pointer(path) {
	let out = "";
	for (let i = 0; i < path.length; i++) {
		let s = String(path[i]);
		if (s.indexOf("~") !== -1 || s.indexOf("/") !== -1) s = s.replace(/~/g, "~0").replace(/\//g, "~1");
		out += "/" + s;
	}
	return out;
}
const ATOMIC = new Set([
	"map",
	"set",
	"weakmap",
	"weakset",
	"date",
	"regexp",
	"error",
	"typed-array",
	"arraybuffer",
	"dataview",
	"function"
]);
function assert_acyclic(value, seen) {
	if (typeof value !== "object" || value === null) return;
	if (seen.has(value)) throw new TypeError("neotraverse: diff does not support cyclic inputs");
	seen.add(value);
	if (Array.isArray(value)) for (let i = 0; i < value.length; i++) assert_acyclic(value[i], seen);
	else if (value instanceof Map) for (const v of value.values()) assert_acyclic(v, seen);
	else if (value instanceof Set) for (const v of value) assert_acyclic(v, seen);
	else for (const k of Object.keys(value)) assert_acyclic(value[k], seen);
	seen.delete(value);
}
function diff_pair(a, b, path, ops, stack, maxDepth, depth) {
	if (a === b) return;
	assert_depth(depth, maxDepth);
	const ta = type_tag(a);
	if (ta !== type_tag(b) || ta === "primitive" || ta === "null") {
		assert_acyclic(a, /* @__PURE__ */ new Set());
		assert_acyclic(b, /* @__PURE__ */ new Set());
		ops.push({
			op: "replace",
			path: pointer(path),
			value: clone(b)
		});
		return;
	}
	const tracked = typeof a === "object" && a !== null;
	if (tracked) {
		if (stack.has(a)) throw new TypeError("neotraverse: diff does not support cyclic inputs");
		stack.add(a);
	}
	if (ta === "array") {
		const min = Math.min(a.length, b.length);
		for (let i = 0; i < min; i++) diff_pair(a[i], b[i], path.concat(i), ops, stack, maxDepth, depth + 1);
		for (let i = a.length; i < b.length; i++) {
			assert_acyclic(b[i], /* @__PURE__ */ new Set());
			ops.push({
				op: "add",
				path: pointer(path.concat(i)),
				value: clone(b[i])
			});
		}
		for (let i = a.length - 1; i >= b.length; i--) {
			assert_acyclic(a[i], /* @__PURE__ */ new Set());
			ops.push({
				op: "remove",
				path: pointer(path.concat(i))
			});
		}
	} else if (ATOMIC.has(ta)) {
		if (!equal(a, b, { maxDepth })) ops.push({
			op: "replace",
			path: pointer(path),
			value: clone(b)
		});
	} else {
		const keys = new Set([...Object.keys(a), ...Object.keys(b)]);
		for (const k of keys) {
			const p = path.concat(k);
			const ha = has_own.call(a, k);
			const hb = has_own.call(b, k);
			if (ha && !hb) {
				assert_acyclic(a[k], /* @__PURE__ */ new Set());
				ops.push({
					op: "remove",
					path: pointer(p)
				});
			} else if (!ha && hb) {
				assert_acyclic(b[k], /* @__PURE__ */ new Set());
				ops.push({
					op: "add",
					path: pointer(p),
					value: clone(b[k])
				});
			} else diff_pair(a[k], b[k], p, ops, stack, maxDepth, depth + 1);
		}
	}
	if (tracked) stack.delete(a);
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
function diff(a, b, options) {
	const ops = [];
	diff_pair(a, b, [], ops, /* @__PURE__ */ new WeakSet(), options?.maxDepth, 0);
	return ops;
}
function remove_at(root, keys, mutate) {
	if (keys.length === 0) return root;
	const parentKeys = keys.slice(0, -1);
	const last = keys[keys.length - 1];
	const parent = parentKeys.length ? get_in(root, parentKeys) : root;
	if (parent == null) return root;
	if (mutate) {
		if (Array.isArray(parent) && typeof last === "number") parent.splice(last, 1);
		else if (parent instanceof Map) parent.delete(last);
		else delete parent[last];
		return root;
	}
	let newParent;
	if (Array.isArray(parent) && typeof last === "number") {
		newParent = parent.slice();
		newParent.splice(last, 1);
	} else if (parent instanceof Map) {
		newParent = new Map(parent);
		newParent.delete(last);
	} else {
		newParent = shallow_shell(parent, true);
		delete newParent[last];
	}
	return parentKeys.length ? set(root, parentKeys, newParent) : newParent;
}
function get_in(node, keys) {
	for (let i = 0; i < keys.length; i++) {
		if (node == null) return void 0;
		node = node instanceof Map ? node.get(keys[i]) : node[keys[i]];
	}
	return node;
}
function patch(value, ops, options) {
	const mutate = !!options?.mutate;
	let root = value;
	for (let i = 0; i < ops.length; i++) {
		const op = ops[i];
		if (op.op !== "add" && op.op !== "remove" && op.op !== "replace") throw new TypeError(`neotraverse: unsupported patch op "${op.op}"`);
		const keys = to_keys(op.path.startsWith("/") || op.path === "" ? op.path : `/${op.path}`);
		if (op.op === "remove") root = remove_at(root, keys, mutate);
		else if (op.op === "add" && keys.length > 0 && typeof keys[keys.length - 1] === "number") root = insert_at(root, keys, op.value, mutate);
		else root = set(root, keys, op.value, { mutate });
	}
	return root;
}
function insert_at(root, keys, value, mutate) {
	const parentKeys = keys.slice(0, -1);
	const idx = keys[keys.length - 1];
	const parent = parentKeys.length ? get_in(root, parentKeys) : root;
	if (Array.isArray(parent)) {
		if (mutate) {
			parent.splice(idx, 0, value);
			return root;
		}
		const copy = parent.slice();
		copy.splice(idx, 0, value);
		return parentKeys.length ? set(root, parentKeys, copy) : copy;
	}
	return set(root, keys, value, { mutate });
}
//#endregion
//#region packages/neotraverse/src/safe/refs.ts
function is_ref(node) {
	return node !== null && typeof node === "object" && !Array.isArray(node) && typeof node.$ref === "string" && Object.keys(node).length === 1 && Object.getOwnPropertySymbols(node).length === 0;
}
const NOT_RESOLVED = Symbol("unresolved");
function resolveRefs(root, options) {
	const cache = /* @__PURE__ */ new Map();
	const resolveTarget = (ref0) => {
		if (cache.has(ref0)) return cache.get(ref0);
		const seen = /* @__PURE__ */ new Set();
		const chain = [];
		let ref = ref0;
		let result = NOT_RESOLVED;
		for (;;) {
			if (cache.has(ref)) {
				result = cache.get(ref);
				break;
			}
			if (!ref.startsWith("#")) break;
			const pointer = ref.slice(1);
			if (pointer !== "" && !pointer.startsWith("/")) break;
			if (seen.has(pointer)) break;
			seen.add(pointer);
			chain.push(ref);
			const target = pointer === "" ? root : get(root, pointer);
			if (target === void 0) break;
			if (is_ref(target)) {
				ref = target.$ref;
				continue;
			}
			result = target;
			break;
		}
		for (let i = 0; i < chain.length; i++) cache.set(chain[i], result);
		cache.set(ref0, result);
		return result;
	};
	return transform(root, (v, edit) => {
		if (is_ref(v.value)) {
			const target = resolveTarget(v.value.$ref);
			if (target !== NOT_RESOLVED) return edit.replace(target, { descend: true });
		}
	}, { maxDepth: options?.maxDepth });
}
//#endregion
export { Visit, clone, diff, equal, get, has, merge, patch, resolveRefs, set, transform, transformAsync, visit };
