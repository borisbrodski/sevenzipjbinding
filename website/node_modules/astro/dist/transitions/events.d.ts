import type { Direction, NavigationTypeString } from './types.js';
type Events = 'astro:after-preparation' | 'astro:after-swap' | 'astro:page-load';
export declare const triggerEvent: (name: Events) => boolean;
export declare const onPageLoad: () => boolean;
declare class BeforeEvent extends Event {
    readonly from: URL;
    to: URL;
    direction: Direction | string;
    readonly navigationType: NavigationTypeString;
    readonly sourceElement: Element | undefined;
    readonly info: any;
    newDocument: Document;
    readonly signal: AbortSignal;
    constructor(type: string, eventInitDict: EventInit | undefined, from: URL, to: URL, direction: Direction | string, navigationType: NavigationTypeString, sourceElement: Element | undefined, info: any, newDocument: Document, signal: AbortSignal);
}
export declare class TransitionBeforePreparationEvent extends BeforeEvent {
    formData: FormData | undefined;
    loader: () => Promise<void>;
    constructor(from: URL, to: URL, direction: Direction | string, navigationType: NavigationTypeString, sourceElement: Element | undefined, info: any, newDocument: Document, signal: AbortSignal, formData: FormData | undefined, loader: (event: TransitionBeforePreparationEvent) => Promise<void>);
}
export declare class TransitionBeforeSwapEvent extends BeforeEvent {
    readonly direction: Direction | string;
    readonly viewTransition: ViewTransition;
    swap: () => void;
    constructor(afterPreparation: BeforeEvent, viewTransition: ViewTransition);
}
export declare function doPreparation(from: URL, to: URL, direction: Direction | string, navigationType: NavigationTypeString, sourceElement: Element | undefined, info: any, signal: AbortSignal, formData: FormData | undefined, defaultLoader: (event: TransitionBeforePreparationEvent) => Promise<void>): Promise<TransitionBeforePreparationEvent>;
export declare const updateScrollPosition: (positions: {
    scrollX: number;
    scrollY: number;
}) => void;
export declare function doSwap(afterPreparation: BeforeEvent, viewTransition: ViewTransition, afterDispatch?: () => Promise<void>): Promise<TransitionBeforeSwapEvent>;
export {};
