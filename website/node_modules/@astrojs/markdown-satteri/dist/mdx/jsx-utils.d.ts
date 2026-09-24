import type { MdxJsxAttributeNode, MdxJsxFlowElementHast, MdxJsxTextElementHast } from 'satteri';
export type MdxJsxHastNode = Readonly<MdxJsxFlowElementHast | MdxJsxTextElementHast>;
export declare function isComponent(tagName: string): boolean;
export declare function hasDirective(node: MdxJsxHastNode, prefix: string): boolean;
export declare function findAttrValue(node: MdxJsxHastNode, name: string): string | null;
export declare function makeJsxAttr(name: string, value: string): MdxJsxAttributeNode;
export declare function makeJsxExprAttr(name: string, expression: string): MdxJsxAttributeNode;
