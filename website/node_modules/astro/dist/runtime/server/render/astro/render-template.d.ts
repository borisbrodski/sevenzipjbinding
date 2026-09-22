import type { RenderDestination } from '../common.js';
declare const renderTemplateResultSym: unique symbol;
export declare class RenderTemplateResult {
    [renderTemplateResultSym]: boolean;
    readonly htmlParts: TemplateStringsArray;
    expressions: any[];
    private error;
    private wrapped;
    constructor(htmlParts: TemplateStringsArray, expressions: unknown[]);
    catchExpressionError(index: number, expression: unknown): Promise<unknown>;
    render(destination: RenderDestination): void | Promise<void>;
}
export declare function isRenderTemplateResult(obj: unknown): obj is RenderTemplateResult;
export declare function renderTemplate(htmlParts: TemplateStringsArray, ...expressions: any[]): RenderTemplateResult;
export {};
