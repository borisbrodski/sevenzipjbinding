import { type HastPluginDefinition } from 'satteri';
/**
 * Replaces a `{'...'}`/`` {`...`} `` expression that's a direct child of
 * `<script>`/`<style>` with a plain text node when the expression is a literal
 * with no interpolated values, so plugin-independent literal content is later
 * recognized as static (see `collapseScriptStyleText`).
 */
export declare const literalizeScriptStyleExpression: HastPluginDefinition;
/**
 * Collapses `<script>`/`<style>` children into a `set:html` attribute when
 * every child is now a plain text node (either originally static, or
 * literalized by `literalizeScriptStyleExpression`). Elements with any other
 * child (a real dynamic expression or component) are left untouched.
 */
export declare const collapseScriptStyleText: HastPluginDefinition;
