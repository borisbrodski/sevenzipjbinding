import { spawn } from 'node:child_process';
const TIMEOUT = 3000;
function checkUnixCommandExists(command) {
    return new Promise((resolve) => {
        const proc = spawn('which', [command]);
        proc.on('error', () => resolve(false));
        proc.on('close', (code) => resolve(code === 0));
    });
}
/**
 * Builds an Error describing a clipboard tool that exited with a non-zero code,
 * surfacing the command, exit code and any stderr output as the failure cause.
 */
function createUnknownError(command, code, stderr) {
    const trimmed = stderr.trim();
    const details = [
        `command \`${command[0]}\` exited with code ${code ?? 'unknown'}`,
        trimmed && `stderr: ${trimmed}`
    ]
        .filter(Boolean)
        .join('. ');
    return new Error(details);
}
const WINDOWS_READ_COMMAND = [
    'powershell',
    [
        '-NoProfile',
        '-Command',
        '[Console]::OutputEncoding = [Text.UTF8Encoding]::new($false); Get-Clipboard'
    ]
];
async function getReadCommand() {
    switch (process.platform) {
        case 'darwin':
            return ['pbpaste', []];
        case 'win32':
            return WINDOWS_READ_COMMAND;
        case 'linux':
        case 'freebsd':
        case 'openbsd':
            if (process.env.WSL_DISTRO_NAME) {
                return WINDOWS_READ_COMMAND;
            }
            if (process.env.WAYLAND_DISPLAY) {
                return ['wl-paste', []];
            }
            if (await checkUnixCommandExists('xsel')) {
                return ['xsel', ['--clipboard', '--output']];
            }
            return ['xclip', ['-selection', 'clipboard', '-o']];
        case 'android':
            return ['termux-clipboard-get', []];
        default:
            return undefined;
    }
}
/**
 * Reads text from the clipboard.
 */
export function readText() {
    return new Promise(async (resolve, reject) => {
        const command = await getReadCommand();
        if (!command) {
            return reject(new Error('No clipboard tool found'));
        }
        const proc = spawn(...command, {
            signal: AbortSignal.timeout(TIMEOUT)
        });
        let stdout = '';
        let stderr = '';
        proc.stdout.on('data', (chunk) => (stdout += chunk));
        proc.stderr.on('data', (chunk) => (stderr += chunk));
        proc.on('error', (cause) => reject(new Error('An error occurred while reading from clipboard', { cause })));
        proc.on('close', (code) => code === 0
            ? resolve(stdout.trim())
            : reject(createUnknownError(command, code, stderr)));
    });
}
const WINDOWS_WRITE_COMMAND = [
    'powershell',
    [
        '-NoProfile',
        '-Command',
        // `Set-Clipboard` rejects empty input, so clear the clipboard instead
        // when there is nothing to write (e.g. `writeText('')`).
        '[Console]::InputEncoding = [Text.UTF8Encoding]::new($false); $text = [Console]::In.ReadToEnd(); if ($text.Length -gt 0) { $text | Set-Clipboard } else { Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.Clipboard]::Clear() }'
    ]
];
async function getWriteCommand() {
    switch (process.platform) {
        case 'darwin':
            return ['pbcopy', []];
        case 'win32':
            return WINDOWS_WRITE_COMMAND;
        case 'linux':
        case 'freebsd':
        case 'openbsd':
            if (process.env.WSL_DISTRO_NAME) {
                return WINDOWS_WRITE_COMMAND;
            }
            if (process.env.WAYLAND_DISPLAY) {
                return ['wl-copy', []];
            }
            if (await checkUnixCommandExists('xsel')) {
                return ['xsel', ['--clipboard', '--input']];
            }
            return ['xclip', ['-selection', 'clipboard', '-i']];
        case 'android':
            return ['termux-clipboard-set', []];
        default:
            return undefined;
    }
}
/**
 * Writes text to the clipboard.
 */
export function writeText(text) {
    return new Promise(async (resolve, reject) => {
        const command = await getWriteCommand();
        if (!command) {
            return reject(new Error('No clipboard tool found'));
        }
        const proc = spawn(...command, {
            stdio: ['pipe', 'ignore', 'pipe'],
            signal: AbortSignal.timeout(TIMEOUT)
        });
        let stderr = '';
        proc.stderr.on('data', (chunk) => (stderr += chunk));
        proc.on('error', (cause) => reject(new Error('An error occurred while copying', { cause })));
        // Use 'exit' rather than 'close': some tools (e.g. xclip) daemonize a child
        // that keeps the stderr pipe open, so 'close' would never fire.
        proc.on('exit', (code) => {
            // The daemonized child inherits the stderr pipe, keeping it (and the event
            // loop) open after the parent exits. Destroy it so callers can exit cleanly.
            proc.stderr?.destroy();
            code === 0
                ? resolve()
                : reject(createUnknownError(command, code, stderr));
        });
        proc.stdin.write(text);
        proc.stdin.end();
    });
}
