import type { ResolvedServerUrls } from 'vite';
export type ServerCommand = 'dev' | 'preview';
/** Maximum time (ms) to wait for a process to exit after SIGTERM before escalating to SIGKILL. */
export declare const GRACEFUL_SHUTDOWN_TIMEOUT = 5000;
export interface LockFileData {
    pid: number;
    port: number;
    url: string;
    urls?: ResolvedServerUrls;
    background: boolean;
    startedAt: string;
}
export interface ExistingServer {
    data: LockFileData;
    stale: boolean;
}
/**
 * Get the URL of a server log file for a given project root.
 */
export declare function getLogFileURL(root: URL, command?: ServerCommand): URL;
/**
 * Parse a lock file JSON string into a LockFileData object.
 * Returns null if the content is invalid.
 */
export declare function parseLockFile(content: string): LockFileData | null;
/**
 * Serialize lock file data to a JSON string.
 */
export declare function serializeLockFile(data: LockFileData): string;
/**
 * Check if a process with the given PID is alive.
 * Signal 0 does not kill the process — it only checks whether the process exists.
 */
export declare function isProcessAlive(pid: number): boolean;
/**
 * Check whether a process command points to the Astro CLI.
 */
export declare function isAstroCommand(command: string): boolean;
interface ProcessInfo {
    pid: number;
    cmd?: string;
}
type ProcessLookup = (by: 'pid', value: number, options: {
    logLevel: 'error';
}) => Promise<ProcessInfo[]>;
/**
 * Check whether the live process recorded in a lock file is still Astro.
 * If the command cannot be inspected, keep the existing PID-only behavior.
 */
export declare function isLockFileProcessAlive(data: LockFileData, find?: ProcessLookup): Promise<boolean>;
/**
 * Read the lock file from disk. Returns null if it doesn't exist or is invalid.
 */
export declare function readLockFile(root: URL, command?: ServerCommand): LockFileData | null;
/**
 * Write the lock file to disk.
 */
export declare function writeLockFile(root: URL, data: LockFileData, command?: ServerCommand): void;
/**
 * Remove the lock file from disk. No-op if it doesn't exist.
 */
export declare function removeLockFile(root: URL, command?: ServerCommand): void;
/**
 * Given lock file data and a liveness result, determine the state of the existing server.
 * This is the pure decision logic, separated from I/O for testability.
 */
export declare function evaluateExistingServer(data: LockFileData | null, alive: boolean): ExistingServer | null;
/**
 * Kill the dev server identified by `data` and clean up its lock file.
 *
 * Sends SIGTERM and waits up to {@link GRACEFUL_SHUTDOWN_TIMEOUT} for the
 * process to exit, escalating to SIGKILL if it is still alive. The lock file
 * is always removed afterwards so a new server can start.
 */
export declare function killDevServer(root: URL, data: LockFileData): Promise<void>;
/**
 * Check for an existing server by reading the lock file and checking process identity.
 * Automatically cleans up stale lock files.
 * Returns the server info if a live server is found, null otherwise.
 */
export declare function checkExistingServer(root: URL, command?: ServerCommand): Promise<LockFileData | null>;
export {};
