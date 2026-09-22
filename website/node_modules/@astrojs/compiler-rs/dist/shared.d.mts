import { AsyncTransformOptions, ParseResult, PreprocessedStyles, PreprocessorError, PreprocessorResult, TransformOptions, TransformResult } from "./types.mjs";
import { CompileOptions, CompileResult } from "@astrojs/compiler-binding";
//#region src/shared.d.ts
/**
 * Map public `TransformOptions` to the NAPI `CompileOptions`.
 *
 * Handles:
 * - `resolvePath` callback → `resolvePathProvided` flag
 * - `preprocessedStyles` opaque type → raw array
 */
declare function mapOptions(options?: TransformOptions | AsyncTransformOptions): CompileOptions | undefined;
/**
 * Map the NAPI `CompileResult` to the public `TransformResult`.
 *
 * Merges preprocessed style errors into the result.
 */
declare function mapResult(result: CompileResult, preprocessedStyles?: PreprocessedStyles): TransformResult;
/**
 * Map the NAPI `ParseResult` to the public `ParseResult`.
 *
 * The only transformation is parsing the AST JSON string into an object.
 */
declare function mapParseResult(result: {
  ast: string;
  diagnostics: CompileResult['diagnostics'];
}): ParseResult;
/** Sync callback signature for {@link preprocessStyles}. */
type SyncPreprocessStyleFn = (content: string, attrs: Record<string, string>) => null | PreprocessorResult | PreprocessorError;
/** Async callback signature for {@link preprocessStyles}. */
type AsyncPreprocessStyleFn = (content: string, attrs: Record<string, string>) => Promise<PreprocessorResult | PreprocessorError | null>;
/**
 * Extract and preprocess `<style>` blocks from an Astro source string.
 *
 * Call this **before** `transform()` and pass the result via
 * `options.preprocessedStyles`.
 *
 * The return type mirrors the callback: if `preprocessStyle` is sync,
 * this returns `PreprocessedStyles` synchronously.
 * If the callback is async, this returns `Promise<PreprocessedStyles>`.
 */
declare function preprocessStyles(input: string, preprocessStyle: SyncPreprocessStyleFn): PreprocessedStyles;
declare function preprocessStyles(input: string, preprocessStyle: AsyncPreprocessStyleFn): Promise<PreprocessedStyles>;
//#endregion
export { mapOptions, mapParseResult, mapResult, preprocessStyles };