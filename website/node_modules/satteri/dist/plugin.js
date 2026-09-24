/** Bounds factory-in-factory nesting. Real presets nest one level; anything
 *  deeper is a factory that leads back to itself, which would otherwise recurse
 *  until the stack overflows. */
const MAX_FACTORY_DEPTH = 10;
/** The one place a plugin option becomes the definition array the pipeline
 *  runs. Factories resolve here and nowhere else, so each is called once per
 *  compile no matter how deeply it is nested. */
export function normalizePlugins(entries, option, source, fileURL, sourceFormat, data) {
    const out = [];
    // Built lazily so a list with no factories allocates no context.
    let ctx;
    const walk = (entry, factoryDepth) => {
        if (entry === null || entry === undefined || entry === false)
            return;
        if (Array.isArray(entry)) {
            for (const item of entry)
                walk(item, factoryDepth);
            return;
        }
        if (typeof entry === "function") {
            if (factoryDepth === 0) {
                throw new Error(`${option}: plugin factory nesting is too deep. A factory most likely returns itself. ` +
                    `A factory may return a plugin or a list of plugins, but that list must not lead back to the same factory.`);
            }
            // `data` stays mutable on purpose: it is the live bag the visitors share.
            ctx ??= Object.freeze({
                fileURL,
                sourceFormat,
                // The parser drops a leading BOM, so `ctx.source` in a visitor lacks it too.
                source: source.startsWith("\uFEFF") ? source.slice(1) : source,
                data,
            });
            walk(entry(ctx), factoryDepth - 1);
            return;
        }
        if (typeof entry !== "object") {
            throw new Error(`${option}: expected a plugin, a factory, a list, or null/undefined/false`);
        }
        if (typeof entry.then === "function") {
            throw new Error(`${option}: a Promise is not a plugin. Plugin factories must be synchronous; ` +
                `await the value first and pass the plugin itself.`);
        }
        out.push(entry);
    };
    for (const entry of entries)
        walk(entry, MAX_FACTORY_DEPTH);
    return out;
}
// Generic so the inferred plugin type preserves each visitor's *actual* return
// type. That lets the compile entry points distinguish sync plugins from async
// ones in their conditional return type.
export function defineMdastPlugin(definition) {
    if (!definition.name) {
        throw new Error("Plugin definition must have a name");
    }
    return definition;
}
export function defineHastPlugin(definition) {
    if (!definition.name) {
        throw new Error("Plugin definition must have a name");
    }
    return definition;
}
