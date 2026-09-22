import { BaseApp, type LogRequestPayload } from './base.js';
export declare class App extends BaseApp {
    isDev(): boolean;
    logRequest(_options: LogRequestPayload): void;
}
