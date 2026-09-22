//#region src/types.d.ts
/**
 * Process information interface
 */
interface ProcessInfo {
  pid: number;
  ppid: number;
  uid?: number;
  gid?: number;
  name: string;
  bin?: string;
  cmd: string;
}
/**
 * Configuration options for find operations
 */
interface FindConfig {
  logLevel?: "warn" | "error";
  strict?: boolean;
}
/**
 * Supported find methods
 */
type FindMethod = "port" | "pid" | "name";
//#endregion
//#region src/find.d.ts
/**
 * find process by condition
 *
 * If no process found, resolve process with empty array (only reject when error occured)
 */
declare function find(by: FindMethod, value: string | RegExp | number, options?: FindConfig | boolean): Promise<ProcessInfo[]>;
//#endregion
export { type FindConfig, type FindMethod, type ProcessInfo, find as default };