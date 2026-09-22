import { partitionByKind } from "../../../core/csp/runtime.js";
function renderCspContent(result) {
  const { scriptDirective, styleDirective, directives } = result;
  const script = partitionByKind(scriptDirective);
  const style = partitionByKind(styleDirective);
  const finalScriptHashes = /* @__PURE__ */ new Set();
  for (const scriptHash of script.default.hashes) {
    finalScriptHashes.add(`'${scriptHash}'`);
  }
  for (const scriptHash of result._metadata.extraScriptHashes) {
    finalScriptHashes.add(`'${scriptHash}'`);
  }
  const finalStyleHashes = /* @__PURE__ */ new Set();
  for (const styleHash of style.default.hashes) {
    finalStyleHashes.add(`'${styleHash}'`);
  }
  for (const styleHash of result._metadata.extraStyleHashes) {
    finalStyleHashes.add(`'${styleHash}'`);
  }
  let directivesContent;
  if (directives.length > 0) {
    directivesContent = directives.join(";") + ";";
  }
  const scriptResources = script.default.resources.length > 0 ? script.default.resources.join(" ") : "'self'";
  const styleResources = style.default.resources.length > 0 ? style.default.resources.join(" ") : "'self'";
  const scriptElementDefaultResource = script.default.resources.length > 0 ? "" : "'self'";
  const styleElementDefaultResource = style.default.resources.length > 0 ? "" : "'self'";
  const scriptElemActive = isEnabled(script.element);
  const styleElemActive = isEnabled(style.element);
  const strictDynamicSuffix = scriptDirective.strictDynamic ? ` 'strict-dynamic'` : "";
  const scriptDefaultHasUnsafeInline = hasUnsafeInline(script.default.resources);
  const styleDefaultHasUnsafeInline = hasUnsafeInline(style.default.resources);
  const scriptBaselineTokens = [
    ...scriptElemActive || scriptDefaultHasUnsafeInline ? [] : [...finalScriptHashes],
    ...scriptDirective.strictDynamic ? [`'strict-dynamic'`] : []
  ];
  const scriptSrc = `script-src ${scriptResources} ${scriptBaselineTokens.join(" ")};`;
  const styleSrc = `style-src ${styleResources} ${(styleElemActive || styleDefaultHasUnsafeInline ? [] : [...finalStyleHashes]).join(" ")};`;
  const scriptSrcElem = scriptElemActive ? renderSpecificDirective(
    "script-src-elem",
    script.element.resources,
    scriptElementDefaultResource,
    finalScriptHashes,
    script.element.hashes,
    strictDynamicSuffix
  ) : void 0;
  const scriptSrcAttr = isEnabled(script.attribute) ? renderSpecificDirective(
    "script-src-attr",
    script.attribute.resources,
    "'none'",
    void 0,
    script.attribute.hashes
  ) : void 0;
  const styleSrcElem = styleElemActive ? renderSpecificDirective(
    "style-src-elem",
    style.element.resources,
    styleElementDefaultResource,
    finalStyleHashes,
    style.element.hashes
  ) : void 0;
  const styleSrcAttr = isEnabled(style.attribute) ? renderSpecificDirective(
    "style-src-attr",
    style.attribute.resources,
    "'none'",
    void 0,
    style.attribute.hashes
  ) : void 0;
  return [
    directivesContent,
    scriptSrc,
    scriptSrcElem,
    scriptSrcAttr,
    styleSrc,
    styleSrcElem,
    styleSrcAttr
  ].filter(Boolean).join(" ");
}
function hasUnsafeInline(resources) {
  return resources.includes("'unsafe-inline'");
}
function isEnabled(sources) {
  return sources.resources.length > 0 || sources.hashes.length > 0;
}
function renderSpecificDirective(name, resources, defaultResource, sharedHashes, ownHashes, suffix = "") {
  const unsafeInline = hasUnsafeInline(resources);
  const hashes = new Set(unsafeInline ? void 0 : sharedHashes);
  if (!unsafeInline) {
    for (const hash of ownHashes) {
      hashes.add(`'${hash}'`);
    }
  }
  let finalResources;
  if (resources.length > 0) {
    finalResources = resources.map((r) => `${r}`).join(" ");
  } else if (defaultResource === "'none'" && hashes.size > 0) {
    finalResources = "";
  } else {
    finalResources = defaultResource;
  }
  return `${name} ${[finalResources, ...hashes].filter(Boolean).join(" ")}${suffix};`;
}
export {
  renderCspContent
};
