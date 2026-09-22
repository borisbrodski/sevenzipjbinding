import { CompileResult, ConvertToTSXOptions, DiagnosticLabel, DiagnosticMessage, DiagnosticSeverity, HoistedScript, ParseOptions, ParseResult, PreprocessedStyles, PreprocessorError, PreprocessorResult, TransformOptions, TransformResult } from "./types.mjs";
import { preprocessStyles } from "./shared.mjs";
//#region src/index.d.ts
declare function transform(input: string, options?: TransformOptions): TransformResult;
declare function parse(input: string): ParseResult;
declare function convertToTSX(_input: string, _options: ConvertToTSXOptions): never;
//#endregion
export { type CompileResult, type DiagnosticLabel, type DiagnosticMessage, type DiagnosticSeverity, type HoistedScript, type ParseOptions, type ParseResult, type PreprocessedStyles, type PreprocessorError, type PreprocessorResult, type TransformOptions, type TransformResult, convertToTSX, parse, preprocessStyles, transform };