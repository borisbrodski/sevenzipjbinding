import { AstroError, AstroErrorData } from "../../core/errors/index.js";
import { resolveDefaultOutputFormat } from "../utils/inferSourceFormat.js";
import { detector } from "../utils/vendor/image-size/detector.js";
import {
  baseService,
  parseQuality
} from "./service.js";
let sharp;
const qualityTable = {
  low: 25,
  mid: 50,
  high: 80,
  max: 100
};
function resolveSharpQuality(quality) {
  if (!quality) return void 0;
  const parsedQuality = parseQuality(quality);
  if (typeof parsedQuality === "number") {
    return parsedQuality;
  }
  return quality in qualityTable ? qualityTable[quality] : void 0;
}
function resolveSharpEncoderOptions(transform, inputFormat, serviceConfig = {}) {
  const quality = resolveSharpQuality(transform.quality);
  if (transform.format === void 0) {
    return quality === void 0 ? void 0 : { quality };
  }
  switch (transform.format) {
    case "jpg":
    case "jpeg":
      return {
        ...serviceConfig.jpeg,
        ...quality === void 0 ? {} : { quality }
      };
    case "png":
      return {
        ...serviceConfig.png,
        ...quality === void 0 ? {} : { quality }
      };
    case "webp": {
      const webpOptions = {
        ...serviceConfig.webp,
        ...quality === void 0 ? {} : { quality }
      };
      if (inputFormat === "gif") {
        webpOptions.loop ??= 0;
      }
      return webpOptions;
    }
    case "avif":
      return {
        ...serviceConfig.avif,
        ...quality === void 0 ? {} : { quality }
      };
    default:
      return quality === void 0 ? void 0 : { quality };
  }
}
async function loadSharp() {
  let sharpImport;
  try {
    sharpImport = (await import("sharp")).default;
  } catch {
    throw new AstroError(AstroErrorData.MissingSharp);
  }
  sharpImport.cache(false);
  return sharpImport;
}
const fitMap = {
  fill: "fill",
  contain: "inside",
  cover: "cover",
  none: "outside",
  "scale-down": "inside",
  outside: "outside",
  inside: "inside"
};
const sharpService = {
  validateOptions: baseService.validateOptions,
  getURL: baseService.getURL,
  parseURL: baseService.parseURL,
  getHTMLAttributes: baseService.getHTMLAttributes,
  getSrcSet: baseService.getSrcSet,
  getRemoteSize: baseService.getRemoteSize,
  async transform(inputBuffer, transformOptions, config, logger) {
    if (!sharp) sharp = await loadSharp();
    const transform = transformOptions;
    const kernel = config.service.config.kernel;
    const bufferFormat = detector(inputBuffer);
    const outputFormat = transform.format ?? resolveDefaultOutputFormat(bufferFormat);
    if (outputFormat === "svg") {
      if (bufferFormat && bufferFormat !== "svg") {
        logger.warn(
          `Astro expected an SVG for "${transform.src}" but the source is ${bufferFormat}. Passing it through as ${bufferFormat} instead.`
        );
        return { data: inputBuffer, format: bufferFormat };
      }
      return { data: inputBuffer, format: "svg" };
    }
    if (!bufferFormat) {
      throw new AstroError({
        ...AstroErrorData.NoImageMetadata,
        message: AstroErrorData.NoImageMetadata.message(transform.src)
      });
    }
    if (bufferFormat === "svg" && !config.dangerouslyProcessSVG) {
      throw new AstroError({
        ...AstroErrorData.UnsupportedImageFormat,
        message: `SVG image processing is disabled, but the source for "${transform.src}" is an SVG. Pass it through unchanged by setting \`format="svg"\` on the component, or set \`image.dangerouslyProcessSVG: true\` to rasterize SVG sources.`
      });
    }
    const result = sharp(inputBuffer, {
      failOn: "none",
      pages: -1,
      limitInputPixels: config.service.config.limitInputPixels
    });
    result.rotate();
    if (transform.width && transform.height) {
      const fit = transform.fit ? fitMap[transform.fit] ?? "inside" : void 0;
      result.resize({
        width: Math.round(transform.width),
        height: Math.round(transform.height),
        kernel,
        fit,
        position: transform.position,
        withoutEnlargement: true
      });
    } else if (transform.height && !transform.width) {
      result.resize({
        height: Math.round(transform.height),
        withoutEnlargement: true,
        kernel
      });
    } else if (transform.width) {
      result.resize({
        width: Math.round(transform.width),
        withoutEnlargement: true,
        kernel
      });
    }
    if (transform.background) {
      result.flatten({ background: transform.background });
    }
    const encoderOptions = resolveSharpEncoderOptions(
      { format: outputFormat, quality: transform.quality },
      bufferFormat,
      config.service.config
    );
    if (outputFormat === "webp") {
      result.webp(encoderOptions);
    } else if (outputFormat === "png") {
      result.png(encoderOptions);
    } else if (outputFormat === "avif") {
      result.avif(encoderOptions);
    } else if (outputFormat === "jpeg" || outputFormat === "jpg") {
      result.jpeg(encoderOptions);
    } else {
      result.toFormat(outputFormat, encoderOptions);
    }
    let data;
    let info;
    try {
      ({ data, info } = await result.toBuffer({ resolveWithObject: true }));
    } catch {
      logger.warn(
        `Astro could not optimize image "${transform.src}". Sharp doesn't support this format. The image will be used unoptimized. Consider converting to WebP or placing in the public/ folder.`
      );
      return { data: inputBuffer, format: bufferFormat };
    }
    const needsCopy = "buffer" in data && data.buffer instanceof SharedArrayBuffer;
    return {
      data: needsCopy ? new Uint8Array(data) : data,
      format: info.format
    };
  }
};
var sharp_default = sharpService;
export {
  sharp_default as default,
  resolveSharpEncoderOptions
};
