import { isEqual, serialize } from "../_chunks/is-equal.mjs";
const PROTO_KEY = "__proto__";
function diff(obj1, obj2) {
	const diffs = [];
	_diff(obj1, obj2, "", diffs);
	return diffs;
}
function _diff(v1, v2, key, out) {
	if (v1 === v2) return;
	const leaf1 = v1 === null || typeof v1 !== "object";
	const leaf2 = v2 === null || typeof v2 !== "object";
	if (!leaf1 && !leaf2) {
		const entries1 = _entries(v1);
		const entries2 = _entries(v2);
		for (const [k, child1] of entries1) {
			const childKey = key ? `${key}.${k}` : k;
			if (entries2.has(k)) _diff(child1, entries2.get(k), childKey, out);
			else out.push(new DiffEntry(childKey, "removed", void 0, _toHashedObject(child1, childKey)));
		}
		for (const [k, child2] of entries2) {
			if (entries1.has(k)) continue;
			const childKey = key ? `${key}.${k}` : k;
			out.push(new DiffEntry(childKey, "added", _toHashedObject(child2, childKey)));
		}
		return;
	}
	if (_hasKeys(v1, leaf1) || _hasKeys(v2, leaf2)) return;
	const node1 = _emptyOrLeafNode(v1, key, leaf1);
	const node2 = _emptyOrLeafNode(v2, key, leaf2);
	if (node1.hash !== node2.hash) out.push(new DiffEntry(key, "changed", node2, node1));
}
function _entries(value) {
	const entries = /* @__PURE__ */ new Map();
	for (const key in value) if (key !== PROTO_KEY) entries.set(key, value[key]);
	return entries;
}
function _hasKeys(value, isLeaf) {
	if (isLeaf) return false;
	for (const key in value) if (key !== PROTO_KEY) return true;
	return false;
}
function _emptyOrLeafNode(value, key, isLeaf) {
	return isLeaf ? new DiffHashedObject(key, value, _leafHash(value)) : new DiffHashedObject(key, value, "{}", Object.create(null));
}
function _toHashedObject(obj, key = "") {
	if (obj === null || typeof obj !== "object") return new DiffHashedObject(key, obj, _leafHash(obj));
	const props = Object.create(null);
	const hashes = [];
	for (const _key in obj) {
		if (_key === PROTO_KEY) continue;
		const child = _toHashedObject(obj[_key], key ? `${key}.${_key}` : _key);
		props[_key] = child;
		hashes.push(child.hash);
	}
	return new DiffHashedObject(key, obj, `{${hashes.join(":")}}`, props);
}
function _leafHash(value) {
	switch (typeof value) {
		case "string": return `'${value}'`;
		case "number":
		case "boolean": return "" + value;
		case "bigint": return `${value}n`;
		default: return serialize(value);
	}
}
var DiffEntry = class {
	key;
	type;
	newValue;
	oldValue;
	constructor(key, type, newValue, oldValue) {
		this.key = key;
		this.type = type;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}
	toString() {
		return this.toJSON();
	}
	toJSON() {
		switch (this.type) {
			case "added": return `Added   \`${this.key}\``;
			case "removed": return `Removed \`${this.key}\``;
			case "changed": return `Changed \`${this.key}\` from \`${this.oldValue?.toString() || "-"}\` to \`${this.newValue?.toString()}\``;
		}
	}
};
var DiffHashedObject = class {
	key;
	value;
	hash;
	props;
	constructor(key, value, hash, props) {
		this.key = key;
		this.value = value;
		this.hash = hash;
		this.props = props;
	}
	toString() {
		if (this.props) return `{${Object.keys(this.props).join(",")}}`;
		else return JSON.stringify(this.value);
	}
	toJSON() {
		const k = this.key || ".";
		if (this.props) return `${k}({${Object.keys(this.props).join(",")}})`;
		return `${k}(${this.value})`;
	}
};
export { diff, isEqual };
