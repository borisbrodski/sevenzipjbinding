import type { SSRResult } from '../../../types/public/internal.js';
import { type RenderDestination } from './common.js';
export declare function renderStreaming(root: unknown, result: SSRResult, destination: RenderDestination): Promise<void>;
