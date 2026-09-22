import type { SystemFallbacksProvider } from '../definitions.js';
import type { FallbackVariant, FontFaceMetrics, GenericFallbackName } from '../types.js';
export declare class RealSystemFallbacksProvider implements SystemFallbacksProvider {
    getLocalFonts(fallback: GenericFallbackName, variant: FallbackVariant): Array<string> | null;
    getMetricsForLocalFont(family: string): FontFaceMetrics;
}
