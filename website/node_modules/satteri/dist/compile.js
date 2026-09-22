import { visitHastHandle, visitHastHandleCollect, visitHastHook, visitHastHookCollect, resolveSubscriptions, } from "./hast/hast-visitor.js";
import { visitMdastHandle, visitMdastHook, resolveMdastSubscriptions, } from "./mdast/mdast-visitor.js";
import { normalizePlugins } from "./plugin.js";
import { applyCommandsAndCompileHandle, applyCommandsAndRenderHandle, applyCommandsToMdastHandle, applyMdastCommandsAndConvertAndCompile, applyMdastCommandsAndConvertAndRender, compileHandle, convertMdastToHastHandle, createHastHandle, createHastHandleFromHtml, createMdastHandle, createMdxHastHandle, createMdxMdastHandle, dropHandle, getHandleSource, createHastHandleWithFrontmatter, createMdxHastHandleWithFrontmatter, getMdastFrontmatter, markdownToHtmlFast, markdownToJsFast, mdxToJsFast, renderHandle, serializeHandle, } from "#binding";
import { ENABLE_DEFINITION_LIST, ENABLE_DIRECTIVE, ENABLE_FOOTNOTES, ENABLE_GFM, ENABLE_HEADING_ATTRIBUTES, ENABLE_MATH, ENABLE_MATH_MULTI_DOLLAR, ENABLE_PLUSES_DELIMITED_METADATA_BLOCKS, ENABLE_SMART_DASHES, ENABLE_SMART_ELLIPSES, ENABLE_SMART_PUNCTUATION, ENABLE_SMART_QUOTES, ENABLE_STRIKETHROUGH, ENABLE_SUBSCRIPT, ENABLE_SUPERSCRIPT, ENABLE_TABLES, ENABLE_TASKLISTS, ENABLE_WIKILINKS, ENABLE_YAML_STYLE_METADATA_BLOCKS, } from "./generated/parse-options.js";
import { MdastReader } from "./mdast/mdast-reader.js";
import { materializeMdastTree } from "./mdast/mdast-materializer.js";
import { markHandleMutated } from "./lazy-child-resolver.js";
import { HastReader } from "./hast/hast-reader.js";
import { materializeHastTree } from "./hast/hast-materializer.js";
const GFM_PARSE_OPTIONS = ENABLE_TABLES | ENABLE_STRIKETHROUGH | ENABLE_TASKLISTS | ENABLE_GFM;
const FRONTMATTER_PARSE_OPTIONS = ENABLE_YAML_STYLE_METADATA_BLOCKS | ENABLE_PLUSES_DELIMITED_METADATA_BLOCKS;
export const DEFAULT_PARSE_OPTIONS = GFM_PARSE_OPTIONS | ENABLE_FOOTNOTES | FRONTMATTER_PARSE_OPTIONS;
/** Packs into the bit set napi reads straight back as `satteri_pulldown_cmark::Options`. */
function featuresToParseOptions(features) {
    let options = 0;
    const { gfm } = features;
    if (typeof gfm === "object") {
        options |= GFM_PARSE_OPTIONS;
        if (gfm.footnotes !== false)
            options |= ENABLE_FOOTNOTES;
    }
    else if (gfm ?? true) {
        options |= GFM_PARSE_OPTIONS | ENABLE_FOOTNOTES;
    }
    if (features.frontmatter ?? true)
        options |= FRONTMATTER_PARSE_OPTIONS;
    // Opting out of single-dollar math sets the multi-dollar sub-flag so the parser skips lone `$`.
    const { math } = features;
    if (typeof math === "object") {
        options |= math.singleDollarTextMath === false ? ENABLE_MATH_MULTI_DOLLAR : ENABLE_MATH;
    }
    else if (math) {
        options |= ENABLE_MATH;
    }
    if (features.headingAttributes)
        options |= ENABLE_HEADING_ATTRIBUTES;
    if (features.directive)
        options |= ENABLE_DIRECTIVE;
    if (features.superscript)
        options |= ENABLE_SUPERSCRIPT;
    if (features.subscript)
        options |= ENABLE_SUBSCRIPT;
    if (features.wikilinks)
        options |= ENABLE_WIKILINKS;
    if (features.definitionList)
        options |= ENABLE_DEFINITION_LIST;
    const smartPunctuation = features.smartPunctuation;
    if (typeof smartPunctuation === "object") {
        if (smartPunctuation.quotes ?? true)
            options |= ENABLE_SMART_QUOTES;
        if (smartPunctuation.dashes ?? true)
            options |= ENABLE_SMART_DASHES;
        if (smartPunctuation.ellipses ?? true)
            options |= ENABLE_SMART_ELLIPSES;
    }
    else if (smartPunctuation) {
        options |= ENABLE_SMART_PUNCTUATION;
    }
    return options;
}
/**
 * Split the user-facing `Features` into the packed parser options plus the
 * conversion-side `JsConvertOptions` carrying the footnote i18n strings and the
 * raw-HTML reparse. The public API only exposes `features`; both halves are
 * routed to napi internally.
 */
export function featuresToNative(features) {
    if (!features)
        return { parseOptions: DEFAULT_PARSE_OPTIONS, convertOptions: undefined };
    let convertOptions;
    const { gfm } = features;
    if (typeof gfm === "object" && typeof gfm.footnotes === "object") {
        const { label, backContent, backLabel, clobberPrefix } = gfm.footnotes;
        convertOptions = {};
        if (label !== undefined)
            convertOptions.footnoteLabel = label;
        if (backContent !== undefined)
            convertOptions.footnoteBackContent = backContent;
        if (backLabel !== undefined)
            convertOptions.footnoteBackLabel = backLabel;
        if (clobberPrefix !== undefined)
            convertOptions.clobberPrefix = clobberPrefix;
    }
    if (features.rawHtml !== undefined) {
        // Routed to convert options so every pipeline (fast paths, plugin paths,
        // *ToHast) applies the reparse.
        convertOptions = convertOptions ?? {};
        convertOptions.rawHtml = features.rawHtml;
    }
    return { parseOptions: featuresToParseOptions(features), convertOptions };
}
/** Free a handle's arena. A plugin's visitor can hand child stubs to user code;
 *  pass `invalidateStubs` so the epoch bump makes any stub retained past this
 *  point hit the designed retention error instead of snapshotting the freed arena. */
function releaseHandle(handle, invalidateStubs) {
    if (invalidateStubs)
        markHandleMutated(handle);
    dropHandle(handle);
}
function warnDroppedTransforms(plugin, dropped, kind) {
    const name = plugin.name ?? "<anonymous>";
    const noun = dropped === 1 ? "transform" : "transforms";
    console.warn(`satteri: plugin "${name}" queued ${dropped} ${kind} ${noun} on node(s) that a plugin had ` +
        `already removed or replaced; ${dropped === 1 ? "it was" : "they were"} dropped.`);
}
function settle(value, fn) {
    if (value instanceof Promise)
        return value.then(fn);
    fn(value);
    return undefined;
}
/** Stays synchronous until a step actually returns a promise. */
function sequence(steps) {
    let pending;
    for (const step of steps) {
        if (step === undefined)
            continue;
        pending = pending === undefined ? step() : pending.then(step);
    }
    return pending;
}
function runMdastPluginsOnHandle(handle, plugins, fileURL, data, sourceFormat, collectLast = false) {
    const out = { handle };
    // A plugin's visitors run once over the tree. A transform that passes a child
    // through (returning it inside the replacement) keeps that child's identity,
    // so a patch the same pass queued on it still applies: nesting composes in
    // one pass. Visitor-built nodes are not re-walked; transform them up front,
    // or hand off to a later plugin that sees the materialized tree.
    const runPlugin = (plugin, isLastPlugin) => {
        const subs = resolveMdastSubscriptions(plugin);
        const apply = (r, isFinalPass) => {
            if (!r.hasMutations)
                return;
            if (collectLast && isLastPlugin && isFinalPass) {
                out.pendingCommands = r.commandBuffer;
                out.lastPlugin = plugin;
                return;
            }
            markHandleMutated(handle);
            const dropped = applyCommandsToMdastHandle(handle, r.commandBuffer);
            if (dropped)
                warnDroppedTransforms(plugin, dropped, "mdast");
        };
        const before = typeof plugin.before === "function" ? plugin.before : undefined;
        const after = typeof plugin.after === "function" ? plugin.after : undefined;
        const hasVisitors = subs.length > 0;
        // Threaded like `data`: one context per pass, since a resolver is epoch-bound.
        const diagnostics = [];
        const runHook = (hook, isFinalPass) => () => settle(visitMdastHook(handle, plugin, hook, () => getHandleSource(handle), fileURL, data, sourceFormat, diagnostics), (r) => apply(r, isFinalPass));
        const runVisitors = (isFinalPass) => () => settle(visitMdastHandle(handle, plugin, subs, () => getHandleSource(handle), fileURL, data, sourceFormat, diagnostics), (r) => apply(r, isFinalPass));
        return sequence([
            before ? runHook(before, !hasVisitors && after === undefined) : undefined,
            hasVisitors ? runVisitors(after === undefined) : undefined,
            after ? runHook(after, true) : undefined,
        ]);
    };
    let i = 0;
    const runNext = () => {
        for (;;) {
            const plugin = plugins[i];
            if (plugin === undefined)
                break;
            i++;
            const r = runPlugin(plugin, i === plugins.length);
            if (r instanceof Promise)
                return r.then(runNext);
        }
        return out;
    };
    return runNext();
}
const EMPTY_COMMAND_BUFFER = new Uint8Array(0);
const NO_HAST_COMMANDS = {
    commands: EMPTY_COMMAND_BUFFER,
    lastPlugin: null,
};
/** Run every HAST plugin except the last one normally (each applies its
 *  commands inline), then return the last plugin's commands without applying.
 *  Lets the caller fuse the final apply with the downstream NAPI step
 *  (`render` or `compile`), saving one NAPI roundtrip per compile. */
function runHastPluginsCollectLast(handle, plugins, source, fileURL, data, sourceFormat) {
    let i = 0;
    const runNext = () => {
        for (;;) {
            const plugin = plugins[i];
            if (plugin === undefined)
                break;
            const isLastPlugin = i === plugins.length - 1;
            i++;
            const subs = resolveSubscriptions(plugin);
            const { before, after } = plugin;
            const passes = [];
            if (typeof before === "function")
                passes.push(before);
            if (subs.length > 0)
                passes.push("visitors");
            if (typeof after === "function")
                passes.push(after);
            const finalPass = passes.pop();
            if (finalPass === undefined)
                continue;
            const warnIfDropped = (dropped) => {
                if (dropped)
                    warnDroppedTransforms(plugin, dropped, "hast");
            };
            // Threaded like `data`: one context per pass, since a resolver is epoch-bound.
            const diagnostics = [];
            const runApplied = (pass) => () => settle(pass === "visitors"
                ? visitHastHandle(handle, plugin, subs, source, fileURL, data, sourceFormat, diagnostics)
                : visitHastHook(handle, plugin, pass, source, fileURL, data, sourceFormat, diagnostics), warnIfDropped);
            const collectFinal = () => {
                const collected = finalPass === "visitors"
                    ? visitHastHandleCollect(handle, plugin, subs, source, fileURL, data, sourceFormat, diagnostics)
                    : visitHastHookCollect(handle, plugin, finalPass, source, fileURL, data, sourceFormat, diagnostics);
                return collected instanceof Promise
                    ? collected.then((commands) => ({ commands, lastPlugin: plugin }))
                    : { commands: collected, lastPlugin: plugin };
            };
            const head = sequence(passes.map(runApplied));
            if (isLastPlugin) {
                return head instanceof Promise ? head.then(collectFinal) : collectFinal();
            }
            const tail = head instanceof Promise ? head.then(runApplied(finalPass)) : runApplied(finalPass)();
            if (tail instanceof Promise)
                return tail.then(runNext);
        }
        return NO_HAST_COMMANDS;
    };
    return runNext();
}
// Public API
function mdxOptionsToNative(opts) {
    const hasAny = opts.optimizeStatic ||
        opts.jsxImportSource !== undefined ||
        opts.jsx !== undefined ||
        opts.jsxRuntime !== undefined ||
        opts.development !== undefined ||
        opts.providerImportSource !== undefined ||
        opts.pragma !== undefined ||
        opts.pragmaFrag !== undefined ||
        opts.pragmaImportSource !== undefined ||
        opts.outputFormat !== undefined ||
        opts.elementAttributeNameCase !== undefined ||
        opts.stylePropertyNameCase !== undefined;
    if (!hasAny)
        return undefined;
    const result = {};
    if (opts.optimizeStatic)
        result.optimizeStatic = opts.optimizeStatic;
    if (opts.jsxImportSource !== undefined)
        result.jsxImportSource = opts.jsxImportSource;
    if (opts.jsx !== undefined)
        result.jsx = opts.jsx;
    if (opts.jsxRuntime !== undefined)
        result.jsxRuntime = opts.jsxRuntime;
    if (opts.development !== undefined)
        result.development = opts.development;
    if (opts.providerImportSource !== undefined)
        result.providerImportSource = opts.providerImportSource;
    if (opts.pragma !== undefined)
        result.pragma = opts.pragma;
    if (opts.pragmaFrag !== undefined)
        result.pragmaFrag = opts.pragmaFrag;
    if (opts.pragmaImportSource !== undefined)
        result.pragmaImportSource = opts.pragmaImportSource;
    if (opts.outputFormat !== undefined)
        result.outputFormat = opts.outputFormat;
    if (opts.elementAttributeNameCase !== undefined)
        result.elementAttributeNameCase = opts.elementAttributeNameCase;
    if (opts.stylePropertyNameCase !== undefined)
        result.stylePropertyNameCase = opts.stylePropertyNameCase;
    return result;
}
export function markdownToHtml(source, options = {}) {
    const { features, fileURL, data = {} } = options;
    const mdastPlugins = normalizePlugins(options.mdastPlugins ?? [], "mdastPlugins", source, fileURL, "markdown", data);
    const hastPlugins = normalizePlugins(options.hastPlugins ?? [], "hastPlugins", source, fileURL, "markdown", data);
    const hastMayHaveStubs = hastPlugins.length > 0;
    const { parseOptions, convertOptions: nativeConvertOptions } = featuresToNative(features);
    // Fast path: no plugins → parse, convert, render, and frontmatter all happen
    // inside a single NAPI call. Skips 5 handle-NAPI roundtrips that the
    // plugin-capable path needs to keep the arena live across passes.
    if (mdastPlugins.length === 0 && hastPlugins.length === 0) {
        const { html, frontmatter } = markdownToHtmlFast(source, parseOptions, nativeConvertOptions);
        return { html, frontmatter: frontmatter ?? null, data };
    }
    // Track source positions only when some plugin opts in with `position: true`;
    // otherwise the parse skips the LineIndex build and per-node line/column lookups.
    const trackPositions = mdastPlugins.some((p) => p.options?.position) || hastPlugins.some((p) => p.options?.position);
    // Fused tail for MDAST-plugins-only (no HAST plugins): after the MDAST
    // plugin pass returns its pending commands, apply + convert + render all
    // happen inside a single NAPI roundtrip. Saves the convert-to-hast handle
    // create + render + drop crossings the generic path makes separately.
    if (hastPlugins.length === 0) {
        const mdastHandle = createMdastHandle(source, parseOptions, trackPositions);
        try {
            const mdastResult = runMdastPluginsOnHandle(mdastHandle, mdastPlugins, fileURL, data, "markdown", true);
            const finishMdast = (r) => {
                try {
                    // Fused tail: apply pending commands (empty buffer is a Rust no-op),
                    // extract frontmatter post-mutation, convert, render, all in one
                    // NAPI roundtrip.
                    const commands = r.pendingCommands ?? EMPTY_COMMAND_BUFFER;
                    const { html, frontmatter, droppedTransforms } = applyMdastCommandsAndConvertAndRender(r.handle, commands, nativeConvertOptions);
                    if (droppedTransforms && r.lastPlugin)
                        warnDroppedTransforms(r.lastPlugin, droppedTransforms, "mdast");
                    return {
                        html,
                        frontmatter: frontmatter ?? null,
                        data,
                    };
                }
                finally {
                    releaseHandle(r.handle, true);
                }
            };
            if (mdastResult instanceof Promise) {
                return mdastResult.then(finishMdast, (err) => {
                    releaseHandle(mdastHandle, true);
                    throw err;
                });
            }
            return finishMdast(mdastResult);
        }
        catch (err) {
            releaseHandle(mdastHandle, true);
            throw err;
        }
    }
    const result = createHastHandleFromMdast(source, mdastPlugins, false, fileURL, parseOptions, nativeConvertOptions, data, trackPositions);
    const runHastThenRender = (r) => {
        // Run all but the last HAST plugin normally (each one applies its
        // commands inline); collect the last plugin's commands and fuse apply +
        // render + handle-drop into one NAPI call.
        let collected;
        try {
            collected = runHastPluginsCollectLast(r.hastHandle, hastPlugins, source, fileURL, data, "markdown");
        }
        catch (err) {
            releaseHandle(r.hastHandle, hastMayHaveStubs);
            throw err;
        }
        if (collected instanceof Promise) {
            return collected.then((c) => finishHastRender(r.hastHandle, c, r.frontmatter), (err) => {
                releaseHandle(r.hastHandle, hastMayHaveStubs);
                throw err;
            });
        }
        return finishHastRender(r.hastHandle, collected, r.frontmatter);
    };
    const finishHastRender = (h, collected, frontmatter) => {
        try {
            let html;
            if (collected.commands.length > 0) {
                const result = applyCommandsAndRenderHandle(h, collected.commands);
                if (result.droppedTransforms && collected.lastPlugin)
                    warnDroppedTransforms(collected.lastPlugin, result.droppedTransforms, "hast");
                html = result.html;
            }
            else {
                html = renderHandle(h);
            }
            return { html, frontmatter, data };
        }
        finally {
            releaseHandle(h, hastMayHaveStubs);
        }
    };
    if (result instanceof Promise)
        return result.then(runHastThenRender);
    return runHastThenRender(result);
}
export function mdxToJs(source, options = {}) {
    return toJsImpl(source, options, true);
}
export function markdownToJs(source, options = {}) {
    return toJsImpl(source, options, false);
}
/** `mdx` picks the parser; the pipeline is identical from MDAST on. */
function toJsImpl(source, options, mdx) {
    const { mdastPlugins: mdastInput = [], hastPlugins: hastInput = [], features, fileURL, data = {}, ...mdxFields } = options;
    const sourceFormat = mdx ? "mdx" : "markdown";
    const mdastPlugins = normalizePlugins(mdastInput, "mdastPlugins", source, fileURL, sourceFormat, data);
    const hastPlugins = normalizePlugins(hastInput, "hastPlugins", source, fileURL, sourceFormat, data);
    const hastMayHaveStubs = hastPlugins.length > 0;
    const mdxOptions = mdxOptionsToNative(mdxFields);
    const { parseOptions, convertOptions: nativeConvertOptions } = featuresToNative(features);
    // Fast path: same trick as `markdownToHtml`. Parse → MDAST → HAST → JS plus
    // frontmatter extraction all happen inside a single NAPI call. Skips 5 of
    // the 6 handle-based crossings the plugin-capable path needs.
    if (mdastPlugins.length === 0 && hastPlugins.length === 0) {
        const { code, frontmatter } = (mdx ? mdxToJsFast : markdownToJsFast)(source, parseOptions, mdxOptions, nativeConvertOptions);
        return { code, frontmatter: frontmatter ?? null, data };
    }
    // Track positions only when a plugin opts in (see `markdownToHtml`).
    const trackPositions = mdastPlugins.some((p) => p.options?.position) || hastPlugins.some((p) => p.options?.position);
    // MDAST-plugins-only fused tail (no HAST plugins): apply + extract
    // frontmatter + convert + simplify + compile happen in one NAPI call.
    if (hastPlugins.length === 0) {
        const mdastHandle = mdx
            ? createMdxMdastHandle(source, parseOptions, trackPositions)
            : createMdastHandle(source, parseOptions, trackPositions);
        try {
            const mdastResult = runMdastPluginsOnHandle(mdastHandle, mdastPlugins, fileURL, data, mdx ? "mdx" : "markdown", true);
            const finishMdast = (r) => {
                try {
                    const commands = r.pendingCommands ?? EMPTY_COMMAND_BUFFER;
                    const { code, frontmatter, droppedTransforms } = applyMdastCommandsAndConvertAndCompile(r.handle, commands, mdxOptions, nativeConvertOptions);
                    if (droppedTransforms && r.lastPlugin)
                        warnDroppedTransforms(r.lastPlugin, droppedTransforms, "mdast");
                    return {
                        code,
                        frontmatter: frontmatter ?? null,
                        data,
                    };
                }
                finally {
                    releaseHandle(r.handle, true);
                }
            };
            if (mdastResult instanceof Promise) {
                return mdastResult.then(finishMdast, (err) => {
                    releaseHandle(mdastHandle, true);
                    throw err;
                });
            }
            return finishMdast(mdastResult);
        }
        catch (err) {
            releaseHandle(mdastHandle, true);
            throw err;
        }
    }
    const result = createHastHandleFromMdast(source, mdastPlugins, mdx, fileURL, parseOptions, nativeConvertOptions, data, trackPositions);
    const runHastThenCompile = (r) => {
        let collected;
        try {
            collected = runHastPluginsCollectLast(r.hastHandle, hastPlugins, source, fileURL, data, mdx ? "mdx" : "markdown");
        }
        catch (err) {
            releaseHandle(r.hastHandle, hastMayHaveStubs);
            throw err;
        }
        if (collected instanceof Promise) {
            return collected.then((c) => finishHastCompile(r.hastHandle, c, r.frontmatter), (err) => {
                releaseHandle(r.hastHandle, hastMayHaveStubs);
                throw err;
            });
        }
        return finishHastCompile(r.hastHandle, collected, r.frontmatter);
    };
    const finishHastCompile = (h, collected, frontmatter) => {
        try {
            let code;
            if (collected.commands.length > 0) {
                const result = applyCommandsAndCompileHandle(h, collected.commands, mdxOptions);
                if (result.droppedTransforms && collected.lastPlugin)
                    warnDroppedTransforms(collected.lastPlugin, result.droppedTransforms, "hast");
                code = result.code;
            }
            else {
                code = compileHandle(h, mdxOptions);
            }
            return { code, frontmatter, data };
        }
        finally {
            releaseHandle(h, hastMayHaveStubs);
        }
    };
    if (result instanceof Promise)
        return result.then(runHastThenCompile);
    return runHastThenCompile(result);
}
/**
 * Compile and evaluate MDX in one step.
 *
 * Returns the module's exports, including `default` (the MDX component).
 * Returns a Promise when async plugins are used, otherwise returns synchronously.
 *
 * ```ts
 * import * as runtime from "react/jsx-runtime";
 * const { default: Content } = evaluate("# Hello", { ...runtime });
 * ```
 */
export function evaluate(source, options) {
    const { Fragment, jsx, jsxs, jsxDEV, useMDXComponents, ...compileOpts } = options;
    const runtime = { Fragment, jsx, jsxs, jsxDEV, useMDXComponents };
    const result = mdxToJs(source, { ...compileOpts, outputFormat: "function-body" });
    if (result instanceof Promise) {
        return result.then((resolved) => new Function(resolved.code)(runtime));
    }
    return new Function(result.code)(runtime);
}
function readFrontmatter(handle) {
    const raw = getMdastFrontmatter(handle);
    return raw ? { kind: raw.kind === "toml" ? "toml" : "yaml", value: raw.value } : null;
}
/** Parse, run mdast plugins, capture frontmatter, then convert to HAST.
 *  Frontmatter is read from the post-plugin MDAST so visitor mutations to
 *  the yaml/toml node are reflected in the returned value. */
function createHastHandleFromMdast(source, mdastPlugins, mdx, fileURL, parseOptions, nativeConvertOptions, data, trackPositions) {
    if (mdastPlugins.length === 0) {
        const [hastHandle, raw] = mdx
            ? createMdxHastHandleWithFrontmatter(source, parseOptions, nativeConvertOptions, trackPositions)
            : createHastHandleWithFrontmatter(source, parseOptions, nativeConvertOptions, trackPositions);
        return {
            hastHandle,
            frontmatter: raw ? { kind: raw.kind === "toml" ? "toml" : "yaml", value: raw.value } : null,
        };
    }
    const mdastHandle = mdx
        ? createMdxMdastHandle(source, parseOptions, trackPositions)
        : createMdastHandle(source, parseOptions, trackPositions);
    const sourceFormat = mdx ? "mdx" : "markdown";
    const mdastMayHaveStubs = mdastPlugins.length > 0;
    // finally{release} is intentional: convertMdastToHastHandle empties the arena
    // on success, but if any step here throws the handle would otherwise leak.
    const finalize = (r) => {
        try {
            const frontmatter = readFrontmatter(r.handle);
            // convert empties the mdast arena, so invalidate any stub held past this point.
            if (mdastMayHaveStubs)
                markHandleMutated(r.handle);
            const hastHandle = convertMdastToHastHandle(r.handle, nativeConvertOptions);
            return { hastHandle, frontmatter };
        }
        finally {
            releaseHandle(r.handle, mdastMayHaveStubs);
        }
    };
    try {
        const mdastResult = runMdastPluginsOnHandle(mdastHandle, mdastPlugins, fileURL, data, sourceFormat);
        if (mdastResult instanceof Promise) {
            return mdastResult.then(finalize, (err) => {
                releaseHandle(mdastHandle, mdastMayHaveStubs);
                throw err;
            });
        }
        return finalize(mdastResult);
    }
    catch (err) {
        releaseHandle(mdastHandle, mdastMayHaveStubs);
        throw err;
    }
}
/** Parse Markdown source into a materialized mdast tree. */
export function markdownToMdast(source, options = {}) {
    const handle = createMdastHandle(source, featuresToNative(options.features).parseOptions, options.position);
    try {
        return materializeMdastTree(new MdastReader(serializeHandle(handle)));
    }
    finally {
        releaseHandle(handle, true);
    }
}
/** Parse MDX source into a materialized mdast tree. */
export function mdxToMdast(source, options = {}) {
    const handle = createMdxMdastHandle(source, featuresToNative(options.features).parseOptions, options.position);
    try {
        return materializeMdastTree(new MdastReader(serializeHandle(handle)));
    }
    finally {
        releaseHandle(handle, true);
    }
}
/** Convert Markdown source to a materialized hast tree. */
export function markdownToHast(source, options = {}) {
    const { parseOptions, convertOptions } = featuresToNative(options.features);
    const handle = createHastHandle(source, parseOptions, convertOptions, options.position);
    try {
        return materializeHastTree(new HastReader(serializeHandle(handle)));
    }
    finally {
        releaseHandle(handle, true);
    }
}
/** Convert MDX source to a materialized hast tree. */
export function mdxToHast(source, options = {}) {
    const { parseOptions, convertOptions } = featuresToNative(options.features);
    const handle = createMdxHastHandle(source, parseOptions, convertOptions, options.position);
    try {
        return materializeHastTree(new HastReader(serializeHandle(handle)));
    }
    finally {
        releaseHandle(handle, true);
    }
}
/**
 * Parse an HTML string into a materialized hast tree: a `root` whose children
 * are the doctype (if any) and the implied `<html>` subtree, or the string's
 * own top-level nodes with `{ fragment: true }`. Only available in builds that
 * include the `from-html` feature.
 */
export function htmlToHast(html, options = {}) {
    const handle = createHastHandleFromHtml(html, options.fragment, options.space);
    try {
        return materializeHastTree(new HastReader(serializeHandle(handle)));
    }
    finally {
        releaseHandle(handle, true);
    }
}
