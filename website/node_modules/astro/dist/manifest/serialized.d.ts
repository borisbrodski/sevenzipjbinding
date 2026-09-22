import { type Plugin } from 'vite';
import type { AstroSettings } from '../types/astro.js';
export declare const SERIALIZED_MANIFEST_ID = "virtual:astro:manifest";
export declare const SERIALIZED_MANIFEST_RESOLVED_ID: string;
export declare const AMBIENT_MANIFEST_SPECIFIER = "#astro-internal/ambient-manifest";
export declare function serializedManifestPlugin({ settings, command, sync, }: {
    settings: AstroSettings;
    command: 'dev' | 'build';
    sync: boolean;
}): Plugin;
