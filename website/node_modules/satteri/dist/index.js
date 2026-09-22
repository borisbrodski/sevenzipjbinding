// Public API: compile functions
export { markdownToHtml, markdownToJs, mdxToJs, evaluate, markdownToMdast, mdxToMdast, markdownToHast, mdxToHast, htmlToHast, } from "./compile.js";
// Plugin definitions
export { defineMdastPlugin, defineHastPlugin } from "./plugin.js";
// Visitor pipeline (for manual plugin execution)
export { normalizePlugins } from "./plugin.js";
export { visitMdastHandle, visitMdastHook, resolveMdastSubscriptions, } from "./mdast/mdast-visitor.js";
export { visitHastHandle, visitHastHook, resolveSubscriptions as resolveHastSubscriptions, } from "./hast/hast-visitor.js";
// Step-by-step API: readers, materializers, and handle functions
export { MdastReader } from "./mdast/mdast-reader.js";
export { materializeMdastTree } from "./mdast/mdast-materializer.js";
export { HastReader } from "./hast/hast-reader.js";
export { materializeHastTree } from "./hast/hast-materializer.js";
export { serializeHandle, renderHandle, compileHandle, getHandleSource } from "#binding";
import { applyCommandsToMdastHandle as napiApplyCommandsToMdastHandle, applyCommandsAndConvertToHastHandle as napiApplyCommandsAndConvertToHastHandle, convertMdastToHastHandle as napiConvertMdastToHastHandle, createHastHandle as napiCreateHastHandle, createMdastHandle as napiCreateMdastHandle, createMdxHastHandle as napiCreateMdxHastHandle, createMdxMdastHandle as napiCreateMdxMdastHandle, dropHandle as napiDropHandle, } from "#binding";
import { featuresToNative } from "./compile.js";
import { markHandleMutated } from "./lazy-child-resolver.js";
// The napi creators take pre-packed parser bits; these keep `Features` the public shape.
export function createMdastHandle(source, features, trackPositions) {
    return napiCreateMdastHandle(source, featuresToNative(features).parseOptions, trackPositions);
}
export function createMdxMdastHandle(source, features, trackPositions) {
    return napiCreateMdxMdastHandle(source, featuresToNative(features).parseOptions, trackPositions);
}
export function createHastHandle(source, features, convertOptions, trackPositions) {
    const native = featuresToNative(features);
    return napiCreateHastHandle(source, native.parseOptions, mergeConvertOptions(native.convertOptions, convertOptions), trackPositions);
}
export function createMdxHastHandle(source, features, convertOptions, trackPositions) {
    const native = featuresToNative(features);
    return napiCreateMdxHastHandle(source, native.parseOptions, mergeConvertOptions(native.convertOptions, convertOptions), trackPositions);
}
function mergeConvertOptions(fromFeatures, explicit) {
    if (fromFeatures === undefined)
        return explicit;
    if (explicit === undefined)
        return fromFeatures;
    return { ...fromFeatures, ...explicit };
}
// The raw NAPI mutators renumber or empty the arena; without the epoch bump a
// child stub retained past a manual-pipeline pass would silently snapshot the
// changed arena (or die with an opaque RangeError) instead of hitting the
// retention error.
export function applyCommandsToMdastHandle(handle, commandBuf) {
    markHandleMutated(handle);
    return napiApplyCommandsToMdastHandle(handle, commandBuf);
}
export function convertMdastToHastHandle(handle, convertOptions) {
    markHandleMutated(handle);
    return napiConvertMdastToHastHandle(handle, convertOptions);
}
export function dropHandle(handle) {
    markHandleMutated(handle);
    napiDropHandle(handle);
}
export function applyCommandsAndConvertToHastHandle(handle, commandBuf, convertOptions) {
    markHandleMutated(handle);
    return napiApplyCommandsAndConvertToHastHandle(handle, commandBuf, convertOptions);
}
