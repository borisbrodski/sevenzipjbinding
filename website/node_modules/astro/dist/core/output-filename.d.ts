import type { AstroConfig } from '../types/public/config.js';
import type { RouteData } from '../types/public/internal.js';
export declare function getOutputFilename(buildFormat: NonNullable<AstroConfig['build']>['format'], name: string, routeData: RouteData): string;
