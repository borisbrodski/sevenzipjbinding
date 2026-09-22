// Manually-defined directive AST node types.
//
// These replicate the type definitions from mdast-util-directive so we can
// avoid pulling in that package (and its transitive deps) just for the
// node interfaces. The shapes match what `getDirectiveData` returns from
// the Rust arena: `name: string`, `attributes: Record<string, string>`.
export {};
