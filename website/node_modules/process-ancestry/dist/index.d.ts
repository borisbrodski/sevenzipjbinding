interface ProcessInfo {
    /** Process ID */
    pid: number;
    /** Parent Process ID */
    ppid: number;
    /** Command line or executable name */
    command?: string;
}

declare function getProcessAncestry(pid?: number): Array<ProcessInfo>;

export { type ProcessInfo, getProcessAncestry };
