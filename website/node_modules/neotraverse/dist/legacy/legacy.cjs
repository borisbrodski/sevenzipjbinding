
//#region packages/neotraverse/src/legacy/index.ts
const to_string = (obj) => Object.prototype.toString.call(obj);
const is_typed_array = (value) => ArrayBuffer.isView(value) && !(value instanceof DataView);
const is_array = Array.isArray;
const is_boxed_primitive = (obj) => {
	const tag = to_string(obj);
	return tag === "[object Boolean]" || tag === "[object Number]" || tag === "[object String]";
};
const gopd = Object.getOwnPropertyDescriptor;
const is_property_enumerable = Object.prototype.propertyIsEnumerable;
const get_own_property_symbols = Object.getOwnPropertySymbols;
const has_own_property = Object.prototype.hasOwnProperty;
const is_unsafe_key = (key) => key === "__proto__" || key === "constructor" || key === "prototype";
function safe_set(dst, key, value) {
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
	const res = Object.keys(obj);
	const symbols = get_own_property_symbols(obj);
	for (let i = 0; i < symbols.length; i++) if (is_property_enumerable.call(obj, symbols[i])) res.push(symbols[i]);
	return res;
}
function is_non_writable(object, key) {
	var _gopd;
	return !((_gopd = gopd(object, key)) === null || _gopd === void 0 ? void 0 : _gopd.writable);
}
function copy(src, options) {
	if (typeof src === "object" && src !== null) {
		let dst;
		if (is_array(src)) dst = [];
		else if (is_typed_array(src)) return src.slice();
		else if (is_boxed_primitive(src)) return Object(src);
		else {
			const tag = to_string(src);
			if (tag === "[object Date]" && typeof src.getTime === "function") dst = new Date(src.getTime());
			else if (tag === "[object RegExp]" && typeof src.source === "string") dst = new RegExp(src);
			else if (tag === "[object Error]") dst = { message: src.message };
			else dst = Object.create(Object.getPrototypeOf(src));
		}
		const iterator_function = options.includeSymbols ? own_enumerable_keys : Object.keys;
		for (const key of iterator_function(src)) safe_set(dst, key, src[key]);
		return dst;
	}
	return src;
}
const empty_null = {
	includeSymbols: false,
	immutable: false
};
function walk(root, cb, options = empty_null) {
	const path = [];
	const parents = [];
	let alive = true;
	const iterator_function = options.includeSymbols ? own_enumerable_keys : Object.keys;
	const immutable = !!options.immutable;
	const max_depth = options.maxDepth;
	return (function walker(node_) {
		assert_within_depth(path.length, max_depth);
		const node = immutable ? copy(node_, options) : node_;
		const modifiers = {};
		let keep_going = true;
		const state = {
			node,
			node_,
			path: path.slice(),
			parent: parents[parents.length - 1],
			parents,
			key: path[path.length - 1],
			isRoot: path.length === 0,
			level: path.length,
			circular: void 0,
			isLeaf: false,
			notLeaf: true,
			notRoot: true,
			isFirst: false,
			isLast: false,
			update: function(x, stopHere = false) {
				if (!state.isRoot) safe_set(state.parent.node, state.key, x);
				state.node = x;
				if (stopHere) keep_going = false;
			},
			delete: function(stopHere) {
				delete state.parent.node[state.key];
				if (stopHere) keep_going = false;
			},
			remove: function(stopHere) {
				if (is_array(state.parent.node)) state.parent.node.splice(state.key, 1);
				else delete state.parent.node[state.key];
				if (stopHere) keep_going = false;
			},
			keys: null,
			before: function(f) {
				modifiers.before = f;
			},
			after: function(f) {
				modifiers.after = f;
			},
			pre: function(f) {
				modifiers.pre = f;
			},
			post: function(f) {
				modifiers.post = f;
			},
			stop: function() {
				alive = false;
			},
			block: function() {
				keep_going = false;
			}
		};
		if (!alive) return state;
		function update_state() {
			if (typeof state.node === "object" && state.node !== null) {
				if (!state.keys || state.node_ !== state.node) state.keys = iterator_function(state.node);
				state.isLeaf = state.keys.length === 0;
				for (let i = 0; i < parents.length; i++) if (parents[i].node_ === node_) {
					state.circular = parents[i];
					break;
				}
			} else {
				state.isLeaf = true;
				state.keys = null;
			}
			state.notLeaf = !state.isLeaf;
			state.notRoot = !state.isRoot;
		}
		update_state();
		const ret = cb.call(state, state.node);
		if (ret !== void 0 && state.update) state.update(ret);
		if (modifiers.before) modifiers.before.call(state, state.node);
		if (!keep_going) return state;
		if (typeof state.node === "object" && state.node !== null && !state.circular) {
			var _state$keys;
			parents.push(state);
			update_state();
			const keys = (_state$keys = state.keys) !== null && _state$keys !== void 0 ? _state$keys : [];
			for (let index = 0; index < keys.length; index++) {
				const key = keys[index];
				path.push(key);
				if (modifiers.pre) modifiers.pre.call(state, state.node[key], key);
				const child = walker(state.node[key]);
				if (immutable && has_own_property.call(state.node, key) && !is_non_writable(state.node, key)) safe_set(state.node, key, child.node);
				child.isLast = index === keys.length - 1;
				child.isFirst = index === 0;
				if (modifiers.post) modifiers.post.call(state, child);
				path.pop();
			}
			parents.pop();
		}
		if (modifiers.after) modifiers.after.call(state, state.node);
		return state;
	})(root).node;
}
/**
* @deprecated Import `Traverse` from `neotraverse/modern` instead
*
* @see https://neotraverse.puruvj.dev/legacy.html#methods
*/
var Traverse = class {
	constructor(obj, options = empty_null) {
		this._value = obj;
		this._options = options;
	}
	/**
	* Get the element at the array `path`.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	get(paths) {
		let node = this._value;
		for (let i = 0; node && i < paths.length; i++) {
			const key = paths[i];
			if (!has_own_property.call(node, key) || !this._options.includeSymbols && typeof key === "symbol") return;
			node = node[key];
		}
		return node;
	}
	/**
	* Return whether the element at the array `path` exists.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	has(paths) {
		let node = this._value;
		for (let i = 0; node && i < paths.length; i++) {
			const key = paths[i];
			if (!has_own_property.call(node, key) || !this._options.includeSymbols && typeof key === "symbol") return false;
			node = node[key];
		}
		return true;
	}
	/**
	* Set the element at the array `path` to `value`.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	set(path, value) {
		let node = this._value;
		let i = 0;
		for (i = 0; i < path.length - 1; i++) {
			const key = path[i];
			if (is_unsafe_key(key)) return value;
			if (!has_own_property.call(node, key)) node[key] = {};
			node = node[key];
		}
		if (is_unsafe_key(path[i])) return value;
		node[path[i]] = value;
		return value;
	}
	/**
	* Execute `fn` for each node in the object and return a new object with the results of the walk. To update nodes in the result use `this.update(value)`.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	map(cb) {
		return walk(this._value, cb, {
			immutable: true,
			includeSymbols: !!this._options.includeSymbols,
			maxDepth: this._options.maxDepth
		});
	}
	/**
	* Execute `fn` for each node in the object but unlike `.map()`, when `this.update()` is called it updates the object in-place.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	forEach(cb) {
		this._value = walk(this._value, cb, this._options);
		return this._value;
	}
	/**
	* For each node in the object, perform a [left-fold](http://en.wikipedia.org/wiki/Fold_(higher-order_function)) with the return value of `fn(acc, node)`.
	*
	* If `init` isn't specified, `init` is set to the root object for the first step and the root element is skipped.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	reduce(cb, init) {
		const skip = arguments.length === 1;
		let acc = skip ? this._value : init;
		this.forEach(function(x) {
			if (!this.isRoot || !skip) acc = cb.call(this, acc, x);
		});
		return acc;
	}
	/**
	* Return an `Array` of every possible non-cyclic path in the object.
	* Paths are `Array`s of string keys.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	paths() {
		const acc = [];
		this.forEach(function() {
			acc.push(this.path);
		});
		return acc;
	}
	/**
	* Return an `Array` of every node in the object.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	nodes() {
		const acc = [];
		this.forEach(function() {
			acc.push(this.node);
		});
		return acc;
	}
	/**
	* Create a deep clone of the object.
	*
	* @see https://neotraverse.puruvj.dev/legacy.html#methods
	*/
	clone() {
		const parents = [];
		const nodes = [];
		const options = this._options;
		const max_depth = options.maxDepth;
		if (is_typed_array(this._value)) return this._value.slice();
		return (function clone(src) {
			assert_within_depth(parents.length, max_depth);
			for (let i = 0; i < parents.length; i++) if (parents[i] === src) return nodes[i];
			if (typeof src === "object" && src !== null) {
				const dst = copy(src, options);
				if (is_typed_array(src) || is_boxed_primitive(src)) return dst;
				parents.push(src);
				nodes.push(dst);
				const iteratorFunction = options.includeSymbols ? own_enumerable_keys : Object.keys;
				for (const key of iteratorFunction(src)) safe_set(dst, key, clone(src[key]));
				parents.pop();
				nodes.pop();
				return dst;
			}
			return src;
		})(this._value);
	}
};
const traverse = (obj, options) => {
	return new Traverse(obj, options);
};
/**
* Get the element at the array `path`.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#get
*/
traverse.get = (obj, paths, options) => {
	return new Traverse(obj, options).get(paths);
};
/**
* Set the element at the array `path` to `value`.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#get
*/
traverse.set = (obj, path, value, options) => {
	return new Traverse(obj, options).set(path, value);
};
/**
* Return whether the element at the array `path` exists.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#get
*/
traverse.has = (obj, paths, options) => {
	return new Traverse(obj, options).has(paths);
};
/**
* Execute `fn` for each node in the object and return a new object with the results of the walk. To update nodes in the result use `this.update(value)`.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#map
*/
traverse.map = (obj, cb, options) => {
	return new Traverse(obj, options).map(cb);
};
/**
* Execute `fn` for each node in the object but unlike `.map()`, when `this.update()` is called it updates the object in-place.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#forEach
*/
traverse.forEach = (obj, cb, options) => {
	return new Traverse(obj, options).forEach(cb);
};
/**
* For each node in the object, perform a [left-fold](http://en.wikipedia.org/wiki/Fold_(higher-order_function)) with the return value of `fn(acc, node)`.
*
* If `init` isn't specified, `init` is set to the root object for the first step and the root element is skipped.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#reduce
*/
traverse.reduce = (obj, cb, init, options) => {
	return new Traverse(obj, options).reduce(cb, init);
};
/**
* Return an `Array` of every possible non-cyclic path in the object.
* Paths are `Array`s of string keys.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#paths
*/
traverse.paths = (obj, options) => {
	return new Traverse(obj, options).paths();
};
/**
* Return an `Array` of every node in the object.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#paths
*/
traverse.nodes = (obj, options) => {
	return new Traverse(obj, options).nodes();
};
/**
* Create a deep clone of the object.
*
* @see https://neotraverse.puruvj.dev/guide/api/core.html#clone
*/
traverse.clone = (obj, options) => {
	return new Traverse(obj, options).clone();
};

//#endregion
//#region packages/neotraverse/src/legacy/legacy.cts
module.exports = traverse;

//#endregion
module.exports = traverse;