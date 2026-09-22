import { CompileOptions, CompileResult, CompileResult as CompileResult$1, Component, DiagnosticLabel, DiagnosticMessage, HoistedScript, ParseResult as BindingParseResult, StyleBlock } from "@astrojs/compiler-binding";
//#region src/types.d.ts
/** Severity level for a diagnostic message. */
type DiagnosticSeverity = 'error' | 'warning' | 'information' | 'hint';
interface PreprocessorResult {
  code: string;
  map?: string;
}
interface PreprocessorError {
  error: string;
}
/**
 * Result of preprocessing `<style>` blocks via {@link preprocessStyles}.
 *
 * This is an opaque value — pass it to `transform()` via the
 * `preprocessedStyles` option. Do not inspect or modify it.
 */
interface PreprocessedStyles {
  /** Preprocessed CSS per extractable `<style>`, in document order.
   *  `undefined` = use original content, `""` = error (empty). */
  styles: (string | undefined)[];
  /** Error messages from the preprocessor. */
  styleError: string[];
}
interface ParseOptions {
  position?: boolean;
}
/**
 * Options for compiling Astro files to JavaScript.
 *
 * Extends the NAPI `CompileOptions` with TS-only features:
 * - `resolvePath` callback for post-compilation path resolution
 * - `preprocessedStyles` uses the opaque `PreprocessedStyles` type
 *
 * Fields that are internal to the NAPI layer (`resolvePathProvided`,
 * `stripSlotComments`) are omitted — they are set automatically by
 * the wrapper functions.
 */
interface TransformOptions extends Omit<CompileOptions, 'resolvePathProvided' | 'preprocessedStyles' | 'compact'> {
  /**
   * Controls whitespace collapsing in the HTML output.
   *
   * - `false`: no whitespace modification.
   * - `true` (default): HTML-aware whitespace collapsing.
   * - `"jsx"`: strips all whitespace-only text nodes and leading/trailing
   *   whitespace from text content.
   *
   * @default `true`
   */
  compact?: boolean | 'jsx';
  resolvePath?: (specifier: string) => string;
  /**
   * Preprocessed style content from {@link preprocessStyles}.
   *
   * When provided, the compiler uses these preprocessed CSS strings
   * instead of the raw `<style>` content from the template.
   */
  preprocessedStyles?: PreprocessedStyles;
}
/** TransformOptions variant for the async entrypoint, where resolvePath may return a Promise. */
interface AsyncTransformOptions extends Omit<TransformOptions, 'resolvePath'> {
  resolvePath?: (specifier: string) => Promise<string> | string;
}
type ConvertToTSXOptions = Pick<TransformOptions, 'filename' | 'normalizedFilename' | 'sourcemap'> & {
  /** If set to true, script tags content will be included in the generated TSX
   * Scripts will be wrapped in an arrow function to be compatible with JSX's spec
   */
  includeScripts?: boolean;
  /** If set to true, style tags content will be included in the generated TSX
   * Styles will be wrapped in a template literal to be compatible with JSX's spec
   */
  includeStyles?: boolean;
};
/** The public result type returned by `transform()` / async `transform()`. */
type TransformResult = CompileResult$1;
interface SourceMap {
  file: string;
  mappings: string;
  names: string[];
  sources: string[];
  sourcesContent: string[];
  version: number;
}
/** Result of parsing an Astro file into an AST. */
interface ParseResult {
  /** The oxc AST in ESTree-compatible JSON format. */
  ast: Record<string, any>;
  /** Diagnostic messages (parse errors, warnings). */
  diagnostics: import('@astrojs/compiler-binding').DiagnosticMessage[];
}
type TSXResult = any;
//#endregion
export { AsyncTransformOptions, type BindingParseResult, type CompileResult, type Component, ConvertToTSXOptions, type DiagnosticLabel, type DiagnosticMessage, DiagnosticSeverity, type HoistedScript, ParseOptions, ParseResult, PreprocessedStyles, PreprocessorError, PreprocessorResult, SourceMap, type StyleBlock, TSXResult, TransformOptions, TransformResult };