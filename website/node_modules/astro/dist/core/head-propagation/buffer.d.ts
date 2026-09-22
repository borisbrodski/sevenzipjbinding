import type { SSRResult } from '../../types/public/internal.js';
export interface HeadPropagator {
    init(result: SSRResult): unknown | Promise<unknown>;
}
/**
 * Runs all registered propagators and collects the head HTML they emit.
 *
 * Components with head content are discovered as we go. Initializing one
 * propagator can register more of them: a component marked `in-tree` renders
 * its children, and one of those children may be a `self` component that emits
 * styles. Slots add a second way to find them — a slot whose markup contains an
 * `await` only reaches the components after that `await` once it resolves, so
 * the pending slot pre-renders are fully drained before moving on to the next
 * propagator.
 *
 * A single pass over the live `Set` reaches every late registration: a `Set`
 * iterator visits entries added during iteration (in insertion order), and
 * because slots are drained before the iterator advances, nothing can register
 * a propagator after the iterator has reported `done`.
 *
 * @example
 * If a layout initializes and discovers a nested component that also emits
 * `<link rel="stylesheet">`, both head chunks are collected before flush.
 */
export declare function collectPropagatedHeadParts(input: {
    propagators: Set<HeadPropagator>;
    result: SSRResult;
    isHeadAndContent: (value: unknown) => value is {
        head: string;
    };
}): Promise<string[]>;
