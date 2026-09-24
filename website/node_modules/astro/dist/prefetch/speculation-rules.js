function generateSpeculationRulesContent(prefetchAll) {
  const selector = prefetchAll ? "a" : "a[data-astro-prefetch]";
  return JSON.stringify({
    prerender: [
      {
        source: "document",
        where: { selector_matches: selector },
        eagerness: "moderate"
      }
    ],
    prefetch: [
      {
        source: "document",
        where: { selector_matches: selector },
        eagerness: "moderate"
      }
    ]
  });
}
export {
  generateSpeculationRulesContent
};
