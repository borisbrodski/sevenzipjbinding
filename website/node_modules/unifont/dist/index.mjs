import { t as __exportAll } from "./rolldown-runtime-D7D4PA-g.mjs";
import { hash } from "ohash";
import { findAll, generate, parse } from "css-tree";
import process from "node:process";
//#region src/css/parse.ts
const extractableKeyMap = {
	"src": "src",
	"font-display": "display",
	"font-weight": "weight",
	"font-stretch": "stretch",
	"font-style": "style",
	"font-feature-settings": "featureSettings",
	"font-variation-settings": "variationSettings",
	"unicode-range": "unicodeRange"
};
const formatPriorityList = Object.values({
	woff2: "woff2",
	woff: "woff",
	otf: "opentype",
	ttf: "truetype",
	eot: "embedded-opentype",
	svg: "svg"
});
function extractFontFaceData(css, family) {
	const fontFaces = [];
	for (const node of findAll(parse(css), (node) => node.type === "Atrule" && node.name === "font-face")) {
		/* v8 ignore next 3 */
		if (node.type !== "Atrule" || node.name !== "font-face") continue;
		if (family) {
			if (!node.block?.children.some((child) => {
				if (child.type !== "Declaration" || child.property !== "font-family") return false;
				const value = extractCSSValue(child);
				const slug = family.toLowerCase();
				if (typeof value === "string" && value.toLowerCase() === slug) return true;
				if (Array.isArray(value) && value.length > 0 && value.some((v) => v.toLowerCase() === slug)) return true;
				return false;
			})) continue;
		}
		const data = {};
		for (const child of node.block?.children || []) if (child.type === "Declaration" && child.property in extractableKeyMap) {
			const value = extractCSSValue(child);
			data[extractableKeyMap[child.property]] = ["src", "unicode-range"].includes(child.property) && !Array.isArray(value) ? [value] : value;
		}
		if (!data.src) continue;
		fontFaces.push(data);
	}
	return mergeFontSources(fontFaces);
}
const RE$1 = /^(?<quote>['"])(.*)\k<quote>$/;
function processRawValue(value) {
	return value.split(",").map((v) => v.trim().replace(RE$1, "$2"));
}
function extractCSSValue(node) {
	if (node.value.type === "Raw") return processRawValue(node.value.value);
	const values = [];
	let buffer = "";
	for (const child of node.value.children) {
		if (child.type === "Function") {
			if (child.name === "local" && child.children.first?.type === "String") values.push({ name: child.children.first.value });
			if (child.name === "format") {
				if (child.children.first?.type === "String") values.at(-1).format = child.children.first.value;
				else if (child.children.first?.type === "Identifier") values.at(-1).format = child.children.first.name;
			}
			if (child.name === "tech") {
				if (child.children.first?.type === "String") values.at(-1).tech = child.children.first.value;
				else if (child.children.first?.type === "Identifier") values.at(-1).tech = child.children.first.name;
			}
		}
		if (child.type === "Url") values.push({ url: child.value });
		if (child.type === "Identifier") buffer = buffer ? `${buffer} ${child.name}` : child.name;
		if (child.type === "String") values.push(child.value);
		if (child.type === "Dimension") {
			const dimensionValue = child.value + child.unit;
			buffer = buffer ? `${buffer} ${dimensionValue}` : dimensionValue;
		}
		if (child.type === "Operator" && child.value === "," && buffer) {
			values.push(buffer);
			buffer = "";
		}
		if (child.type === "UnicodeRange") values.push(child.value);
		if (child.type === "Number") values.push(Number(child.value));
		if (child.type === "Percentage") {
			const percentageValue = `${child.value}%`;
			buffer = buffer ? `${buffer} ${percentageValue}` : percentageValue;
		}
	}
	if (buffer) values.push(buffer);
	if (values.length === 1) return values[0];
	return values;
}
function mergeFontSources(data) {
	const mergedData = [];
	for (const face of data) {
		const keys = Object.keys(face).filter((k) => k !== "src");
		const existing = mergedData.find((f) => Object.keys(f).length === keys.length + 1 && keys.every((key) => f[key]?.toString() === face[key]?.toString()));
		if (existing) {
			for (const s of face.src) if (existing.src.every((src) => "url" in src ? !("url" in s) || s.url !== src.url : !("name" in s) || s.name !== src.name)) existing.src.push(s);
		} else mergedData.push(face);
	}
	for (const face of mergedData) face.src.sort((a, b) => {
		return ("format" in a ? formatPriorityList.indexOf(a.format || "woff2") : -2) - ("format" in b ? formatPriorityList.indexOf(b.format || "woff2") : -2);
	});
	return mergedData;
}
//#endregion
//#region src/fetch.ts
const RETRY_DELAY = 1e3;
var NonRetryableError = class extends Error {};
async function fetchWithRetries(url, init, retries = 3) {
	try {
		const response = await fetch(url, init);
		if (!response.ok) {
			const message = `Fetch error (status ${response.status}): ${response.statusText}`;
			if (response.status >= 400 && response.status < 500 && response.status !== 429) throw new NonRetryableError(message);
			throw new Error(message);
		}
		return response;
	} catch (err) {
		if (retries <= 0 || err instanceof NonRetryableError) throw err;
		console.warn(`Could not fetch from \`${url}\`. Will retry in \`${RETRY_DELAY}ms\`. \`${retries}\` retries left.`);
		return new Promise((resolve) => setTimeout(resolve, RETRY_DELAY)).then(() => fetchWithRetries(url, init, retries - 1));
	}
}
//#endregion
//#region src/utils.ts
function defineFontProvider(name, provider) {
	return ((options) => Object.assign(provider.bind(null, options || {}), {
		_name: name,
		_options: options
	}));
}
function prepareWeights({ inputWeights, weights, hasVariableWeights }) {
	const collectedWeights = [];
	for (const weight of inputWeights) {
		if (weight.includes(" ")) {
			if (hasVariableWeights) {
				collectedWeights.push(weight);
				continue;
			}
			const [min, max] = weight.split(" ");
			collectedWeights.push(...weights.filter((_w) => {
				const w = Number(_w);
				return w >= Number(min) && w <= Number(max);
			}).map((w) => String(w)));
			continue;
		}
		if (weights.includes(weight)) collectedWeights.push(weight);
	}
	return Array.from(new Set(collectedWeights), (weight) => ({
		weight,
		variable: weight.includes(" ")
	}));
}
function splitCssIntoSubsets(input) {
	const data = [];
	const comments = [];
	const nodes = findAll(parse(input, {
		positions: true,
		onComment(value, loc) {
			comments.push({
				value: value.trim(),
				endLine: loc.end.line
			});
		}
	}), (node) => node.type === "Atrule" && node.name === "font-face");
	if (comments.length === 0) return [{
		subset: null,
		css: input
	}];
	for (const node of nodes) {
		const comment = comments.filter((comment) => comment.endLine < node.loc.start.line).at(-1);
		data.push({
			subset: comment?.value ?? null,
			css: generate(node)
		});
	}
	return data;
}
const formatMap = {
	woff2: "woff2",
	woff: "woff",
	otf: "opentype",
	ttf: "truetype",
	eot: "embedded-opentype"
};
/** Maps variation format strings (e.g. 'woff2-variations') to their base CSS format name (e.g. 'woff2') */
const variationFormatMap = {
	"woff2-variations": "woff2",
	"woff-variations": "woff",
	"opentype-variations": "opentype",
	"truetype-variations": "truetype"
};
function computeIdFromSource(source) {
	return "name" in source ? source.name : source.url;
}
function cleanFontFaces(fonts, _formats) {
	const formats = _formats.map((format) => formatMap[format]);
	const result = [];
	const hashToIndex = /* @__PURE__ */ new Map();
	for (const { src: _src, meta, ...font } of fonts) {
		const key = hash(font);
		const index = hashToIndex.get(key);
		const src = _src.map((source) => "name" in source ? source : {
			...source,
			...source.format ? { format: formatMap[source.format] ?? source.format } : {}
		}).filter((source) => {
			if ("name" in source) return true;
			if (!source.format) return true;
			if (formats.includes(source.format)) return true;
			const baseFormat = variationFormatMap[source.format];
			return !!baseFormat && formats.includes(baseFormat);
		});
		if (src.length === 0) continue;
		if (index === void 0) {
			hashToIndex.set(key, result.push({
				...font,
				...meta ? { meta } : {},
				src
			}) - 1);
			continue;
		}
		const existing = result[index];
		const ids = new Set(existing.src.map((source) => computeIdFromSource(source)));
		existing.src.push(...src.filter((source) => {
			const id = computeIdFromSource(source);
			return !ids.has(id) && ids.add(id);
		}));
	}
	return result;
}
//#endregion
//#region src/providers/adobe.ts
async function getAdobeFontMeta(id) {
	const { kit } = await fetchWithRetries(`https://typekit.com/api/v1/json/kits/${id}/published`).then((res) => res.json());
	return kit;
}
const KIT_REFRESH_TIMEOUT = 300 * 1e3;
var adobe_default = defineFontProvider("adobe", async (options, ctx) => {
	if (!options.id) return;
	const familyMap = /* @__PURE__ */ new Map();
	const notFoundFamilies = /* @__PURE__ */ new Set();
	const fonts = { kits: [] };
	let lastRefreshKitTime;
	let fetchKitsPromise;
	const kits = typeof options.id === "string" ? [options.id] : options.id;
	await fetchKits();
	async function fetchKits(bypassCache = false) {
		const newKits = [];
		const newFamilyMap = /* @__PURE__ */ new Map();
		await Promise.all(kits.map(async (id) => {
			let meta;
			const key = `adobe:meta-${id}.json`;
			if (bypassCache) {
				meta = await getAdobeFontMeta(id);
				await ctx.storage.setItem(key, meta);
			} else meta = await ctx.storage.getItem(key, () => getAdobeFontMeta(id));
			if (!meta) throw new TypeError("No font metadata found in adobe response.");
			newKits.push(meta);
			for (const family of meta.families) newFamilyMap.set(family.name, family.id);
		}));
		familyMap.clear();
		notFoundFamilies.clear();
		fonts.kits = newKits;
		for (const [k, v] of newFamilyMap) familyMap.set(k, v);
	}
	async function getFontDetails(family, options) {
		options.weights = options.weights.map(String);
		for (const kit of fonts.kits) {
			const font = kit.families.find((f) => f.name === family);
			if (!font) continue;
			const weights = prepareWeights({
				inputWeights: options.weights,
				hasVariableWeights: false,
				weights: font.variations.map((v) => `${v.slice(-1)}00`)
			}).map((w) => w.weight);
			const styles = [];
			for (const style of font.variations) {
				if (style.includes("i") && !options.styles.includes("italic")) continue;
				if (!weights.includes(String(`${style.slice(-1)}00`))) continue;
				styles.push(style);
			}
			if (styles.length === 0) continue;
			return extractFontFaceData(await fetchWithRetries(`https://use.typekit.net/${kit.id}.css`).then((res) => res.text()), font.css_names[0] ?? family.toLowerCase().split(" ").join("-")).filter((font) => {
				const [lowerWeight, upperWeight] = Array.isArray(font.weight) ? font.weight : [0, 0];
				return (!options.styles || !font.style || options.styles.includes(font.style)) && (!weights || !font.weight || Array.isArray(font.weight) ? weights.some((weight) => Number(weight) <= upperWeight || Number(weight) >= lowerWeight) : weights.includes(String(font.weight)));
			});
		}
		return [];
	}
	return {
		listFonts() {
			return [...familyMap.keys()];
		},
		async resolveFont(family, options) {
			if (notFoundFamilies.has(family)) return;
			if (!familyMap.has(family)) if (fetchKitsPromise) await fetchKitsPromise;
			else {
				const lastRefetch = lastRefreshKitTime || 0;
				const now = Date.now();
				if (now - lastRefetch > KIT_REFRESH_TIMEOUT) {
					lastRefreshKitTime = now;
					fetchKitsPromise = fetchKits(true).finally(() => {
						fetchKitsPromise = void 0;
					});
					await fetchKitsPromise;
				}
			}
			if (!familyMap.has(family)) {
				notFoundFamilies.add(family);
				return;
			}
			return { fonts: await ctx.storage.getItem(`adobe:${family}-${hash(options)}-data.json`, () => getFontDetails(family, options)) };
		}
	};
});
//#endregion
//#region src/providers/bunny.ts
const BASE_URL$2 = "https://fonts.bunny.net";
const VALID_FALLBACKS$2 = [
	"sans-serif",
	"serif",
	"monospace"
];
function getFallbacks$3(category) {
	if (VALID_FALLBACKS$2.includes(category)) return [category];
}
var bunny_default = defineFontProvider("bunny", async (_options, ctx) => {
	const familyMap = /* @__PURE__ */ new Map();
	const fonts = await ctx.storage.getItem("bunny:meta.json", () => fetchWithRetries(`${BASE_URL$2}/list`).then((res) => res.json()));
	for (const [id, family] of Object.entries(fonts)) familyMap.set(family.familyName, id);
	async function getFontDetails(id, font, options) {
		const weights = prepareWeights({
			inputWeights: options.weights,
			hasVariableWeights: false,
			weights: font.weights.map(String)
		});
		const styleMap = {
			italic: "i",
			oblique: "i",
			normal: ""
		};
		const styles = new Set(options.styles.map((i) => styleMap[i]));
		if (weights.length === 0 || styles.size === 0) return [];
		const resolvedVariants = weights.flatMap((w) => Array.from(styles, (s) => `${w.weight}${s}`));
		const css = await fetchWithRetries(`${BASE_URL$2}/css?family=${id}:${resolvedVariants.join(",")}`).then((res) => res.text());
		const resolvedFontFaceData = [];
		const groups = splitCssIntoSubsets(css).filter((group) => group.subset ? options.subsets.includes(group.subset) : true);
		for (const group of groups) {
			const data = extractFontFaceData(group.css);
			data.map((f) => {
				f.meta ??= {};
				if (group.subset) f.meta.subset = group.subset;
				return f;
			});
			resolvedFontFaceData.push(...data);
		}
		return cleanFontFaces(resolvedFontFaceData, options.formats);
	}
	return {
		listFonts() {
			return [...familyMap.keys()];
		},
		async resolveFont(fontFamily, defaults) {
			const id = familyMap.get(fontFamily);
			if (!id) return;
			const font = fonts[id];
			return {
				fonts: await ctx.storage.getItem(`bunny:${fontFamily}-${hash(defaults)}-data.json`, () => getFontDetails(id, font, defaults)),
				fallbacks: getFallbacks$3(font.category)
			};
		}
	};
});
//#endregion
//#region src/providers/fontshare.ts
const BASE_URL$1 = "https://api.fontshare.com/v2";
function getFallbacks$2(category) {
	if (category.includes("Serif")) return ["serif"];
	if (category.includes("Sans")) return ["sans-serif"];
}
var fontshare_default = defineFontProvider("fontshare", async (_options, ctx) => {
	const fontshareFamilies = /* @__PURE__ */ new Set();
	const fonts = await ctx.storage.getItem("fontshare:meta.json", async () => {
		const fonts = [];
		let offset = 0;
		let chunk;
		do {
			chunk = await fetchWithRetries(`${BASE_URL$1}/fonts?offset=${offset}&limit=100`).then((res) => res.json());
			fonts.push(...chunk.fonts);
			offset++;
		} while (chunk.has_more);
		return fonts;
	});
	for (const font of fonts) fontshareFamilies.add(font.name);
	async function getFontDetails(font, options) {
		const numbers = [];
		let axis;
		const hasVariable = font.styles.some((e) => e.is_variable);
		if (hasVariable) axis = font.axes.find((e) => e.property === "wght");
		const weights = prepareWeights({
			inputWeights: options.weights,
			hasVariableWeights: hasVariable && !!axis,
			weights: font.styles.map((s) => String(s.weight.weight))
		}).map((w) => w.weight);
		for (const style of font.styles) {
			if (style.is_italic && !options.styles.includes("italic")) continue;
			if (!style.is_italic && !options.styles.includes("normal")) continue;
			if (style.is_variable && axis && !weights.includes(`${axis.range_left} ${axis.range_right}`)) continue;
			if (!style.is_variable && !weights.includes(String(style.weight.weight))) continue;
			numbers.push(style.weight.number);
		}
		if (numbers.length === 0) return [];
		return cleanFontFaces(extractFontFaceData(await fetchWithRetries(`${BASE_URL$1}/css?f[]=${font.slug}@${numbers.join(",")}`).then((res) => res.text())), options.formats);
	}
	return {
		listFonts() {
			return [...fontshareFamilies];
		},
		async resolveFont(fontFamily, defaults) {
			if (!fontshareFamilies.has(fontFamily)) return;
			const font = fonts.find((f) => f.name === fontFamily);
			return {
				fonts: await ctx.storage.getItem(`fontshare:${fontFamily}-${hash(defaults)}-data.json`, () => getFontDetails(font, defaults)),
				fallbacks: getFallbacks$2(font.category)
			};
		}
	};
});
//#endregion
//#region src/providers/fontsource.ts
const BASE_URL = "https://api.fontsource.org/v1";
const FONTSOURCE_REGISTERED_AXES = /* @__PURE__ */ new Set([
	"wght",
	"ital",
	"slnt",
	"wdth",
	"opsz"
]);
function pickFontsourceAxisSlug(axes) {
	let hasRegisteredExtra = false;
	for (const axis of axes) {
		if (axis === "wght" || axis === "ital") continue;
		if (!FONTSOURCE_REGISTERED_AXES.has(axis)) return "full";
		hasRegisteredExtra = true;
	}
	return hasRegisteredExtra ? "standard" : "wght";
}
const VALID_FALLBACKS$1 = [
	"sans-serif",
	"serif",
	"monospace"
];
function getFallbacks$1(category) {
	if (VALID_FALLBACKS$1.includes(category)) return [category];
}
var fontsource_default = defineFontProvider("fontsource", async (_options, ctx) => {
	const fonts = await ctx.storage.getItem("fontsource:meta.json", () => fetchWithRetries(`${BASE_URL}/fonts`).then((res) => res.json()));
	const familyMap = /* @__PURE__ */ new Map();
	for (const meta of fonts) familyMap.set(meta.family, meta);
	async function getFontDetails(font, options) {
		const weights = prepareWeights({
			inputWeights: options.weights,
			hasVariableWeights: font.variable,
			weights: font.weights.map(String)
		});
		const styles = options.styles.filter((style) => font.styles.includes(style));
		const subsets = options.subsets ? options.subsets.filter((subset) => font.subsets.includes(subset)) : [font.defSubset];
		if (weights.length === 0 || styles.length === 0) return [];
		const fontDetail = await fetchWithRetries(`${BASE_URL}/fonts/${font.id}`).then((res) => res.json());
		const fontFaceData = [];
		for (const subset of subsets) for (const style of styles) for (const { weight, variable } of weights) {
			if (variable) {
				try {
					const variableAxes = await ctx.storage.getItem(`fontsource:${font.family}-axes.json`, () => fetchWithRetries(`${BASE_URL}/variable/${font.id}`).then((res) => res.json()));
					if (variableAxes && variableAxes.axes.wght) {
						const axisSlug = pickFontsourceAxisSlug(Object.keys(variableAxes.axes));
						fontFaceData.push({
							style,
							weight: [Number(variableAxes.axes.wght.min), Number(variableAxes.axes.wght.max)],
							src: [{
								url: `https://cdn.jsdelivr.net/fontsource/fonts/${font.id}:vf@latest/${subset}-${axisSlug}-${style}.woff2`,
								format: "woff2"
							}],
							unicodeRange: fontDetail.unicodeRange[subset]?.split(","),
							meta: { subset }
						});
					}
				} catch {
					console.error(`Could not download variable axes metadata for \`${font.family}\` from \`fontsource\`. \`unifont\` will not be able to inject variable axes for ${font.family}.`);
				}
				continue;
			}
			const variantUrl = fontDetail.variants[weight][style][subset].url;
			fontFaceData.push({
				style,
				weight,
				src: Object.entries(variantUrl).map(([format, url]) => ({
					url,
					format
				})),
				unicodeRange: fontDetail.unicodeRange[subset]?.split(","),
				meta: { subset }
			});
		}
		return cleanFontFaces(fontFaceData, options.formats);
	}
	return {
		listFonts() {
			return [...familyMap.keys()];
		},
		async resolveFont(fontFamily, options) {
			const font = familyMap.get(fontFamily);
			if (!font) return;
			return {
				fonts: await ctx.storage.getItem(`fontsource:${fontFamily}-${hash(options)}-data.json`, () => getFontDetails(font, options)),
				fallbacks: getFallbacks$1(font.category)
			};
		}
	};
});
//#endregion
//#region src/providers/google.ts
const userAgents = {
	eot: "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)",
	ttf: "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; de-at) AppleWebKit/533.21.1 (KHTML, like Gecko) Version/5.0.5 Safari/533.21.1",
	woff: "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0",
	woff2: "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
};
const VALID_FALLBACKS = {
	"Sans Serif": "sans-serif",
	"Serif": "serif",
	"Monospace": "monospace"
};
function getFallbacks(category) {
	const fallback = VALID_FALLBACKS[category];
	if (fallback) return [fallback];
}
var google_default = defineFontProvider("google", async (providerOptions, ctx) => {
	const { familyMetadataList: googleFonts } = await ctx.storage.getItem("google:meta.json", () => fetchWithRetries("https://fonts.google.com/metadata/fonts").then((res) => res.json()));
	const styleMap = {
		italic: "1",
		oblique: "1",
		normal: "0"
	};
	async function getFontDetails(font, options) {
		const styles = [...new Set(options.styles.map((i) => styleMap[i]))].sort();
		const glyphs = (options.options?.experimental?.glyphs ?? providerOptions.experimental?.glyphs?.[font.family])?.join("");
		const weights = prepareWeights({
			inputWeights: options.weights,
			hasVariableWeights: font.axes.some((a) => a.tag === "wght"),
			weights: Object.keys(font.fonts)
		}).map((v) => v.variable ? {
			weight: v.weight.replace(" ", ".."),
			variable: v.variable
		} : v);
		if (weights.length === 0 || styles.length === 0) return [];
		const resolvedAxes = [];
		let resolvedVariants = [];
		const variableAxis = options.options?.experimental?.variableAxis ?? providerOptions.experimental?.variableAxis?.[font.family];
		const candidateAxes = [
			"wght",
			"ital",
			...Object.keys(variableAxis ?? {})
		].sort(googleFlavoredSorting);
		for (const axis of candidateAxes) {
			const axisValue = {
				wght: weights.map((v) => v.weight),
				ital: styles
			}[axis] ?? variableAxis[axis].map((v) => Array.isArray(v) ? `${v[0]}..${v[1]}` : v);
			if (resolvedVariants.length === 0) resolvedVariants = axisValue;
			else resolvedVariants = resolvedVariants.flatMap((v) => Array.from(axisValue, (o) => [v, o].join(","))).sort();
			resolvedAxes.push(axis);
		}
		let priority = 0;
		const resolvedFontFaceData = [];
		for (const format of options.formats) {
			const userAgent = userAgents[format];
			if (!userAgent) continue;
			let url = `https://fonts.googleapis.com/css2?family=${font.family}:${resolvedAxes.join(",")}@${resolvedVariants.join(";")}`;
			if (glyphs) url += `&text=${encodeURIComponent(glyphs)}`;
			const groups = splitCssIntoSubsets(await fetchWithRetries(url, { headers: { "user-agent": userAgent } }).then((res) => res.text())).filter((group) => group.subset ? options.subsets.includes(group.subset) : true);
			for (const group of groups) {
				const data = extractFontFaceData(group.css);
				data.map((f) => {
					f.meta ??= {};
					f.meta.priority = priority;
					if (group.subset) f.meta.subset = group.subset;
					return f;
				});
				resolvedFontFaceData.push(...data);
			}
			priority++;
		}
		return cleanFontFaces(resolvedFontFaceData, options.formats);
	}
	return {
		listFonts() {
			return googleFonts.map((font) => font.family);
		},
		async resolveFont(fontFamily, options) {
			const font = googleFonts.find((font) => font.family === fontFamily);
			if (!font) return;
			return {
				fonts: await ctx.storage.getItem(`google:${fontFamily}-${hash(options)}-data.json`, () => getFontDetails(font, options)),
				fallbacks: getFallbacks(font.category)
			};
		}
	};
});
function googleFlavoredSorting(a, b) {
	const isALowercase = a.charAt(0) === a.charAt(0).toLowerCase();
	const isBLowercase = b.charAt(0) === b.charAt(0).toLowerCase();
	if (isALowercase !== isBLowercase) return Number(isBLowercase) - Number(isALowercase);
	else return a.localeCompare(b);
}
//#endregion
//#region src/providers/googleicons.ts
var googleicons_default = defineFontProvider("googleicons", async (providerOptions, ctx) => {
	const googleIcons = await ctx.storage.getItem("googleicons:meta.json", async () => {
		const data = await fetchWithRetries("https://fonts.google.com/metadata/icons?key=material_symbols&incomplete=true").then((res) => res.text());
		return JSON.parse(data.substring(data.indexOf("\n") + 1)).families;
	});
	async function getFontDetails(family, options) {
		const iconNames = (options.options?.experimental?.glyphs ?? providerOptions.experimental?.glyphs?.[family])?.toSorted().join(",");
		let css = "";
		for (const format of options.formats) {
			const userAgent = userAgents[format];
			if (!userAgent) continue;
			if (family.includes("Icons")) css += await fetchWithRetries(`https://fonts.googleapis.com/icon?family=${family}`, { headers: { "user-agent": userAgent } }).then((res) => res.text());
			else {
				let url = `https://fonts.googleapis.com/css2?family=${family}:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200`;
				if (iconNames) url += `&icon_names=${iconNames}`;
				css += await fetchWithRetries(url, { headers: { "user-agent": userAgent } }).then((res) => res.text());
			}
		}
		return cleanFontFaces(extractFontFaceData(css), options.formats);
	}
	return {
		listFonts() {
			return googleIcons;
		},
		async resolveFont(fontFamily, options) {
			if (!googleIcons.includes(fontFamily)) return;
			return { fonts: await ctx.storage.getItem(`googleicons:${fontFamily}-${hash(options)}-data.json`, () => getFontDetails(fontFamily, options)) };
		}
	};
});
//#endregion
//#region src/providers/npm.ts
const DEFAULT_CDN = "https://cdn.jsdelivr.net/npm";
const KNOWN_FONT_PACKAGES = [
	{
		match: /^@fontsource-variable\//,
		family: (pkg) => {
			return `${slugToFamily(pkg.replace("@fontsource-variable/", ""))} Variable`;
		}
	},
	{
		match: /^@fontsource\//,
		family: (pkg) => {
			return slugToFamily(pkg.replace("@fontsource/", ""));
		}
	},
	{
		match: /^cal-sans$/,
		family: () => "Cal Sans"
	}
];
/** Convert a slug like "geist-sans" to "Geist Sans" */
function slugToFamily(slug) {
	return slug.split("-").map((s) => s.charAt(0).toUpperCase() + s.slice(1)).join(" ");
}
const SPACE_RE = /\s+/g;
/** Convert a family name like "Geist Sans" to a fontsource slug "geist-sans" */
function familyToSlug(family) {
	return family.toLowerCase().replace(SPACE_RE, "-");
}
const VARIABLE_RE = / Variable$/;
/**
* Guess the npm package name and CSS file for a font family that wasn't
* found in the auto-detected packages. Uses fontsource conventions as fallback.
*/
function guessPackageForFamily(family) {
	if (family.endsWith(" Variable")) return {
		pkgName: `@fontsource-variable/${familyToSlug(family.replace(VARIABLE_RE, ""))}`,
		file: "index.css"
	};
	return {
		pkgName: `@fontsource/${familyToSlug(family)}`,
		file: "index.css"
	};
}
var npm_default = defineFontProvider("npm", (providerOptions, ctx) => {
	const cdn = providerOptions.cdn || DEFAULT_CDN;
	const remote = providerOptions.remote ?? true;
	const readFile = providerOptions.readFile;
	const root = providerOptions.root || ".";
	let detectedFonts;
	let detectedFontsHash;
	async function getDetectedFonts() {
		if (!readFile) return detectedFonts ??= /* @__PURE__ */ new Map();
		let pkgJsonContent;
		try {
			pkgJsonContent = await readFile(`${root}/package.json`);
		} catch {
			return detectedFonts ??= /* @__PURE__ */ new Map();
		}
		if (!pkgJsonContent) return detectedFonts ??= /* @__PURE__ */ new Map();
		const contentHash = hash(pkgJsonContent);
		if (detectedFonts && detectedFontsHash === contentHash) return detectedFonts;
		detectedFontsHash = contentHash;
		detectedFonts = /* @__PURE__ */ new Map();
		try {
			const pkgJson = JSON.parse(pkgJsonContent);
			const allDeps = {
				...pkgJson.dependencies,
				...pkgJson.devDependencies
			};
			for (const depName of Object.keys(allDeps)) for (const pattern of KNOWN_FONT_PACKAGES) if (pattern.match.test(depName)) {
				const family = pattern.family(depName);
				detectedFonts.set(family.toLowerCase(), {
					family,
					pkgName: depName,
					file: pattern.file || "index.css"
				});
				break;
			}
		} catch {}
		return detectedFonts;
	}
	function resolveUrlsToAbsolute(fontFaces, baseUrl) {
		for (const face of fontFaces) if (Array.isArray(face.src)) face.src = face.src.map((src) => {
			if ("url" in src) {
				const url = src.url;
				if (url.startsWith("http") || url.startsWith("data:") || url.startsWith("//")) return src;
				return {
					...src,
					url: new URL(url, baseUrl).href
				};
			}
			return src;
		});
	}
	async function resolveFromLocal(pkgName, cssFile, family, formats) {
		if (!readFile) return null;
		const cssPath = `${root}/node_modules/${pkgName}/${cssFile}`;
		const css = await readFile(cssPath).catch(() => null);
		if (!css) return null;
		const fontFaces = extractFontFaceData(css, family);
		if (fontFaces.length === 0) return null;
		let version = "latest";
		try {
			const localPkgJson = await readFile(`${root}/node_modules/${pkgName}/package.json`);
			if (localPkgJson) {
				const parsed = JSON.parse(localPkgJson);
				if (parsed.version) version = parsed.version;
			}
		} catch {}
		resolveUrlsToAbsolute(fontFaces, `${cdn}/${pkgName}@${version}/`);
		return cleanFontFaces(fontFaces, formats);
	}
	async function resolveFromCdn(pkgName, pkgVersion, cssFile, family, formats) {
		let css;
		try {
			css = await fetchWithRetries(`${cdn}/${pkgName}@${pkgVersion}/${cssFile}`).then((res) => res.text());
		} catch {
			return null;
		}
		if (!css) return null;
		const fontFaces = extractFontFaceData(css, family);
		resolveUrlsToAbsolute(fontFaces, `${cdn}/${pkgName}@${pkgVersion}/`);
		return cleanFontFaces(fontFaces, formats);
	}
	return {
		async listFonts() {
			const fonts = await getDetectedFonts();
			if (fonts.size === 0) return;
			return Array.from(fonts.values(), (f) => f.family);
		},
		async resolveFont(family, options) {
			const familyOptions = options.options || {};
			let pkgName;
			let cssFile;
			let pkgVersion;
			if (familyOptions.package) {
				pkgName = familyOptions.package;
				cssFile = familyOptions.file || "index.css";
				pkgVersion = familyOptions.version || "latest";
			} else {
				const detected = (await getDetectedFonts()).get(family.toLowerCase());
				if (detected) {
					pkgName = detected.pkgName;
					cssFile = familyOptions.file || detected.file;
					pkgVersion = familyOptions.version || "latest";
				} else {
					const guessed = guessPackageForFamily(family);
					pkgName = guessed.pkgName;
					cssFile = familyOptions.file || guessed.file;
					pkgVersion = familyOptions.version || "latest";
				}
			}
			const key = `npm:${pkgName}/${cssFile}-${hash(options)}`;
			const fonts = await ctx.storage.getItem(key, async () => {
				const localResult = await resolveFromLocal(pkgName, cssFile, family, options.formats);
				if (localResult) return localResult;
				if (!remote) return null;
				return await resolveFromCdn(pkgName, pkgVersion, cssFile, family, options.formats);
			});
			if (!fonts) return;
			return { fonts };
		}
	};
});
//#endregion
//#region src/providers.ts
var providers_exports = /* @__PURE__ */ __exportAll({
	adobe: () => adobe_default,
	bunny: () => bunny_default,
	fontshare: () => fontshare_default,
	fontsource: () => fontsource_default,
	google: () => google_default,
	googleicons: () => googleicons_default,
	npm: () => npm_default
});
//#endregion
//#region package.json
var version = "0.7.5";
//#endregion
//#region src/cache.ts
function memoryStorage() {
	const cache = /* @__PURE__ */ new Map();
	return {
		getItem(key) {
			return cache.get(key);
		},
		setItem(key, value) {
			cache.set(key, value);
		}
	};
}
const ONE_WEEK = 1e3 * 60 * 60 * 24 * 7;
function createAsyncStorage(storage, options = {}) {
	const prefix = options?.cachedBy?.length ? `${createCacheKey(...options.cachedBy)}:` : "";
	const resolveKey = (key) => `${prefix}${key}`;
	return {
		async getItem(key, init) {
			const resolvedKey = resolveKey(key);
			const now = Date.now();
			const res = await storage.getItem(resolvedKey);
			if (res && res.expires > now && res.version === version) return res.data;
			if (!init) return null;
			const data = await init();
			await storage.setItem(resolvedKey, {
				expires: now + ONE_WEEK,
				version,
				data
			});
			return data;
		},
		async setItem(key, data) {
			await storage.setItem(resolveKey(key), {
				expires: Date.now() + ONE_WEEK,
				version,
				data
			});
		}
	};
}
function createCacheKey(...fragments) {
	return fragments.map((f) => {
		return sanitize(typeof f === "string" ? f : hash(f));
	}).join(":");
}
const RE = /[^\w.-]/g;
function sanitize(input) {
	if (!input) return "";
	return input.replace(RE, "_");
}
//#endregion
//#region src/proxy.ts
let installed = false;
async function installProxyDispatcher() {
	if (installed) return;
	if (!(process.env.HTTPS_PROXY || process.env.HTTP_PROXY || process.env.https_proxy || process.env.http_proxy)) return;
	installed = true;
	try {
		const { Agent, EnvHttpProxyAgent, getGlobalDispatcher, setGlobalDispatcher } = await import("undici");
		const current = getGlobalDispatcher();
		if (current instanceof Agent && !(current instanceof EnvHttpProxyAgent)) setGlobalDispatcher(new EnvHttpProxyAgent());
	} catch {}
}
//#endregion
//#region src/unifont.ts
const defaultResolveOptions = {
	weights: ["400"],
	styles: ["normal", "italic"],
	subsets: [
		"cyrillic-ext",
		"cyrillic",
		"greek-ext",
		"greek",
		"vietnamese",
		"latin-ext",
		"latin"
	],
	formats: ["woff2"]
};
async function createUnifont(providers, unifontOptions) {
	await installProxyDispatcher();
	const stack = {};
	const storage = unifontOptions?.storage ?? memoryStorage();
	for (const provider of providers) stack[provider._name] = void 0;
	await Promise.all(providers.map(async (provider) => {
		const context = { storage: createAsyncStorage(storage, { cachedBy: [provider._name, provider._options] }) };
		try {
			const initializedProvider = await provider(context);
			if (initializedProvider) stack[provider._name] = initializedProvider;
		} catch (cause) {
			const message = `Could not initialize provider \`${provider._name}\`. \`unifont\` will not be able to process fonts provided by this provider.`;
			if (unifontOptions?.throwOnError) throw new Error(message, { cause });
			console.error(message, cause);
		}
		if (!stack[provider._name]?.resolveFont) delete stack[provider._name];
	}));
	const allProviders = Object.keys(stack);
	async function resolveFont(fontFamily, options = {}, providers = allProviders) {
		const mergedOptions = {
			...defaultResolveOptions,
			...options
		};
		for (const id of providers) {
			const provider = stack[id];
			try {
				const result = await provider?.resolveFont(fontFamily, {
					...mergedOptions,
					options: mergedOptions.options?.[id]
				});
				if (result) return {
					provider: id,
					...result
				};
			} catch (cause) {
				const message = `Could not resolve font face for \`${fontFamily}\` from \`${id}\` provider.`;
				if (unifontOptions?.throwOnError) throw new Error(message, { cause });
				console.error(message, cause);
			}
		}
		return { fonts: [] };
	}
	async function listFonts(providers = allProviders) {
		let names;
		for (const id of providers) {
			const provider = stack[id];
			try {
				const result = await provider?.listFonts?.();
				if (result) {
					names ??= [];
					names.push(...result);
				}
			} catch (cause) {
				const message = `Could not list names from \`${id}\` provider.`;
				if (unifontOptions?.throwOnError) throw new Error(message, { cause });
				console.error(message, cause);
			}
		}
		return names;
	}
	return {
		resolveFont,
		listFonts
	};
}
//#endregion
export { createUnifont, defaultResolveOptions, defineFontProvider, providers_exports as providers };
