import type { SSRResult } from '../../../types/public/internal.js';
import { HTMLString } from '../escape.js';
import { renderTemplate } from './astro/render-template.js';
import { type RenderDestination, type RenderInstance } from './common.js';
import { type RenderInstruction, type RenderScriptInstruction } from './instruction.js';
type RenderTemplateResult = ReturnType<typeof renderTemplate>;
export type ComponentSlots = Record<string, ComponentSlotValue>;
export type ComponentSlotValue = (result: SSRResult) => RenderTemplateResult | Promise<RenderTemplateResult>;
declare const slotString: unique symbol;
/**
 * A part of a slot's content stream: either already-stringified HTML or a
 * position-sensitive script instruction that is resolved (and deduplicated)
 * lazily when the slot is finally stringified.
 */
export type SlotStringChunk = string | RenderScriptInstruction;
export declare class SlotString extends HTMLString {
    instructions: null | RenderInstruction[];
    /**
     * The slot's content as an ordered stream. Unlike `instructions` (which holds
     * position-independent instructions like head/hydration content), scripts are
     * kept inline here so they render at their original position instead of being
     * hoisted to the start of the slot output.
     */
    chunks: SlotStringChunk[];
    [slotString]: boolean;
    constructor(content: string, instructions: null | RenderInstruction[], chunks?: SlotStringChunk[]);
}
export declare function isSlotString(str: string): str is any;
/**
 * Collects instructions from a SlotString into the target array.
 * Returns the (possibly newly created) instructions array.
 */
export declare function mergeSlotInstructions(target: RenderInstruction[] | null, source: SlotString): RenderInstruction[] | null;
export declare class SlotRenderInstance implements RenderInstance {
    private readonly result;
    private readonly slotted;
    constructor(result: SSRResult, slotted: ComponentSlotValue | RenderTemplateResult);
    /** Must be called at most once per instance. */
    evaluate(): unknown;
    render(destination: RenderDestination): void | Promise<void>;
}
export declare function renderSlot(result: SSRResult, slotted: ComponentSlotValue | RenderTemplateResult, fallback?: ComponentSlotValue | RenderTemplateResult): RenderInstance;
export declare function renderSlotToString(result: SSRResult, slotted: ComponentSlotValue | RenderTemplateResult, fallback?: ComponentSlotValue | RenderTemplateResult): Promise<string>;
interface RenderSlotsResult {
    slotInstructions: null | RenderInstruction[];
    children: Record<string, string>;
}
export declare function renderSlots(result: SSRResult, slots?: ComponentSlots): Promise<RenderSlotsResult>;
export declare function createSlotValueFromString(content: string): ComponentSlotValue;
export {};
