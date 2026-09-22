import { AsyncTransformOptions, CompileResult, DiagnosticLabel, DiagnosticMessage, DiagnosticSeverity, HoistedScript, ParseOptions, ParseResult, PreprocessedStyles, PreprocessorError, PreprocessorResult, TransformResult } from "./types.mjs";
import { preprocessStyles } from "./shared.mjs";
//#region src/async.d.ts
declare function transform(input: string, options?: AsyncTransformOptions): Promise<TransformResult>;
declare function parse(input: string): Promise<ParseResult>;
declare function convertToTSX(_input: string): Promise<never>;
//#endregion
export { type CompileResult, type DiagnosticLabel, type DiagnosticMessage, type DiagnosticSeverity, type HoistedScript, type ParseOptions, type ParseResult, type PreprocessedStyles, type PreprocessorError, type PreprocessorResult, type AsyncTransformOptions as TransformOptions, type TransformResult, convertToTSX, parse, preprocessStyles, transform };