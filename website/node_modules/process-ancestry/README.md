# process-ancestry

A cross-platform Node.js library for retrieving process ancestry information. Get the parent process chain for any process ID on Unix/Linux, macOS, and Windows systems.

## Features

- **Cross-platform**: Works on Unix/Linux/macOS (using `ps`) and Windows (using `wmic`)
- **Robust**: Includes timeout handling, cycle detection, and comprehensive error handling
- **TypeScript**: Full TypeScript support with type definitions
- **Zero dependencies**: No external dependencies beyond Node.js built-ins
- **ESM**: Native ES module support

## Installation

```bash
npm install process-ancestry
```

## Usage

### Basic Usage

```javascript
import { getProcessAncestry } from "process-ancestry";

// Get ancestry for current process
const ancestry = getProcessAncestry();
console.log(ancestry);

// Get ancestry for specific PID
const ancestry = getProcessAncestry(1234);
console.log(ancestry);
```

### Example Output

```javascript
[
  {
    pid: 1234,
    ppid: 5678,
    command: "node script.js",
  },
  {
    pid: 5678,
    ppid: 1,
    command: "bash",
  },
];
```

## API Reference

### `getProcessAncestry(pid?: number): ProcessInfo[]`

Retrieves the process ancestry chain for a given process ID.

#### Parameters

- `pid` (optional): Process ID to get ancestry for. Defaults to `process.pid` (current process).

#### Returns

An array of `ProcessInfo` objects representing the process ancestry chain, ordered from the given process up to the root.

#### Throws

- `Error`: If `pid` is not a positive integer

### `ProcessInfo`

```typescript
interface ProcessInfo {
  /** Process ID */
  pid: number;
  /** Parent Process ID */
  ppid: number;
  /** Command line or executable name */
  command?: string;
}
```

## Platform Support

- **Unix/Linux/macOS**: Uses `ps -p <pid> -o pid=,ppid=,command=`
- **Windows**: Uses `wmic process where (ProcessId=<pid>) get ProcessId,ParentProcessId,CommandLine /format:csv`

## Development

```bash
# Install dependencies
pnpm install

# Run tests
pnpm test

# Build
pnpm build

# Run checks
pnpm check
```

## License

MIT

## Author

Matt Kane
