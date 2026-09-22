const PATH_TAG_PREFIX = "astro-path:";
function pathTag(path) {
  return `${PATH_TAG_PREFIX}${path}`;
}
function buildCacheControlDirectives(options, extraDirectives) {
  const directives = [];
  if (extraDirectives) {
    directives.push(...extraDirectives);
  }
  if (options.maxAge !== void 0) {
    directives.push(`max-age=${options.maxAge}`);
  }
  if (options.swr !== void 0) {
    directives.push(`stale-while-revalidate=${options.swr}`);
  }
  return directives.length > 0 ? directives.join(", ") : void 0;
}
function setConditionalHeaders(headers, options) {
  if (options.lastModified) {
    headers.set("Last-Modified", options.lastModified.toUTCString());
  }
  if (options.etag) {
    headers.set("ETag", options.etag);
  }
}
function normalizeTags(tags) {
  if (!tags) return [];
  return Array.isArray(tags) ? tags : [tags];
}
function collectInvalidationTags(options) {
  const tags = normalizeTags(options.tags);
  if (options.path) {
    tags.push(pathTag(options.path));
  }
  return tags;
}
export {
  buildCacheControlDirectives,
  collectInvalidationTags,
  normalizeTags,
  pathTag,
  setConditionalHeaders
};
