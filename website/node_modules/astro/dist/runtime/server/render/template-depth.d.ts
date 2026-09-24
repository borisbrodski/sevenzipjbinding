import type { SSRResult } from '../../../types/public/internal.js';
/**
 * Emitted by the compiler when entering an HTML `<template>` element.
 */
export declare function templateEnter(_result: SSRResult): Readonly<{
    readonly type: "template-enter";
}>;
/**
 * Emitted by the compiler when exiting an HTML `<template>` element.
 */
export declare function templateExit(_result: SSRResult): Readonly<{
    readonly type: "template-exit";
}>;
