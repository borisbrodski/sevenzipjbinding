import type fsMod from 'node:fs';
import type { RunnableDevEnvironment } from 'vite';
import type { AstroLogger } from '../core/logger/core.js';
import type { AstroSettings } from '../types/astro.js';
export declare function getContentConfigLoadPromise(): Promise<void> | undefined;
export declare function kickOffContentConfigLoad({ settings, fs, logger, environment, }: {
    settings: AstroSettings;
    fs: typeof fsMod;
    logger: AstroLogger;
    environment: RunnableDevEnvironment;
}): Promise<void>;
