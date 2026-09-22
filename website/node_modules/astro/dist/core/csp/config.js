import * as z from "zod/v4";
const ALGORITHMS = {
  "SHA-256": "sha256-",
  "SHA-384": "sha384-",
  "SHA-512": "sha512-"
};
const ALGORITHM_VALUES = Object.values(ALGORITHMS);
const cspAlgorithmSchema = z.enum(Object.keys(ALGORITHMS)).optional().default("SHA-256");
const cspHashSchema = z.custom((value) => {
  if (typeof value !== "string") {
    return false;
  }
  return ALGORITHM_VALUES.some((allowedValue) => {
    return value.startsWith(allowedValue);
  });
});
const CSP_KINDS = ["element", "attribute", "default"];
const cspKindSchema = z.enum(CSP_KINDS);
const ATTRIBUTE_ALLOWED_RESOURCES = [
  "'none'",
  "'unsafe-hashes'",
  "'unsafe-inline'",
  "'report-sample'"
];
const cspResourceEntrySchema = z.union([
  z.string(),
  z.object({
    resource: z.string(),
    kind: cspKindSchema
  })
]).superRefine((value, ctx) => {
  const resource = typeof value === "string" ? value : value.resource;
  const kind = typeof value === "string" ? "default" : value.kind;
  if (kind === "element" && resource === "'unsafe-hashes'") {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: `The source \`'unsafe-hashes'\` is not valid for \`element\` resources (it is rejected by \`script-src-elem\`/\`style-src-elem\`).`,
      fatal: true
    });
  } else if (kind === "attribute" && !ATTRIBUTE_ALLOWED_RESOURCES.includes(resource)) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: `The source \`${resource}\` is not valid for \`attribute\` resources. \`script-src-attr\`/\`style-src-attr\` only accept: ${ATTRIBUTE_ALLOWED_RESOURCES.join(", ")}.`,
      fatal: true
    });
  }
});
const cspHashEntrySchema = z.union([
  cspHashSchema,
  z.object({
    hash: cspHashSchema,
    kind: cspKindSchema
  })
]);
const ALLOWED_DIRECTIVES = [
  "base-uri",
  "child-src",
  "connect-src",
  "default-src",
  "fenced-frame-src",
  "font-src",
  "form-action",
  "frame-ancestors",
  "frame-src",
  "img-src",
  "manifest-src",
  "media-src",
  "object-src",
  "referrer",
  "report-to",
  "report-uri",
  "require-trusted-types-for",
  "sandbox",
  "trusted-types",
  "upgrade-insecure-requests",
  "worker-src"
];
const allowedDirectivesSchema = z.custom((v) => typeof v === "string").superRefine((value, ctx) => {
  const isAllowed = ALLOWED_DIRECTIVES.some((allowedValue) => {
    return value.startsWith(allowedValue);
  });
  if (!isAllowed) {
    if (value.startsWith("script-src") || value.startsWith("style-src")) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: `Directives \`script-src\` and \`style-src\` (including their \`-elem\`/\`-attr\` variants) are not allowed in \`security.csp.directives\`. Please use \`security.csp.scriptDirective\` and \`security.csp.styleDirective\` instead, scoping resources/hashes to the more specific directives with the \`kind\` option (\`"element"\` or \`"attribute"\`).`,
        fatal: true
      });
    } else {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: `Invalid directive: "${value}". Allowed directives are: ${ALLOWED_DIRECTIVES.join(", ")}`,
        fatal: true
      });
    }
  }
});
export {
  ALGORITHMS,
  CSP_KINDS,
  allowedDirectivesSchema,
  cspAlgorithmSchema,
  cspHashEntrySchema,
  cspHashSchema,
  cspKindSchema,
  cspResourceEntrySchema
};
