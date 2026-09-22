import { ELEMENT_NODE, TEXT_NODE, parse, renderSync } from "ultrahtml";
import { AstroError, AstroErrorData } from "../../core/errors/index.js";
import { dropAttributes } from "../runtime.js";
function collectStyleTexts(nodes) {
  const styles = [];
  const stack = [...nodes].reverse();
  while (stack.length > 0) {
    const node = stack.pop();
    if (node.type === ELEMENT_NODE && node.name === "style") {
      const textContent = node.children?.filter((c) => c.type === TEXT_NODE).map((c) => c.value).join("");
      if (textContent) {
        styles.push(textContent);
      }
    } else if (node.children) {
      for (let i = node.children.length - 1; i >= 0; i--) {
        stack.push(node.children[i]);
      }
    }
  }
  return styles;
}
async function parseSvg({
  path,
  contents,
  svgOptimizer
}) {
  let processedContents = contents;
  if (svgOptimizer) {
    try {
      processedContents = await svgOptimizer.optimize(contents, path);
    } catch (cause) {
      throw new AstroError(
        {
          ...AstroErrorData.CannotOptimizeSvg,
          message: AstroErrorData.CannotOptimizeSvg.message(path, svgOptimizer.name)
        },
        { cause }
      );
    }
  }
  const root = parse(processedContents);
  const svgNode = root.children.find(
    ({ name, type }) => type === ELEMENT_NODE && name === "svg"
  );
  if (!svgNode) {
    throw new Error("SVG file does not contain an <svg> element");
  }
  const { attributes, children } = svgNode;
  const body = renderSync({ ...root, children });
  const styles = collectStyleTexts(children);
  return { attributes, body, styles };
}
async function makeSvgComponent(meta, contents, svgOptimizer) {
  const file = typeof contents === "string" ? contents : contents.toString("utf-8");
  const {
    attributes,
    body: children,
    styles
  } = await parseSvg({
    path: meta.fsPath,
    contents: file,
    svgOptimizer
  });
  const props = {
    meta,
    attributes: dropAttributes(attributes),
    children,
    styles
  };
  return `import { createSvgComponent } from 'astro/assets/runtime';
export default createSvgComponent(${JSON.stringify(props)})`;
}
async function parseSvgComponentData(meta, contents, svgOptimizer) {
  const file = typeof contents === "string" ? contents : contents.toString("utf-8");
  const {
    attributes,
    body: children,
    styles
  } = await parseSvg({
    path: meta.fsPath,
    contents: file,
    svgOptimizer
  });
  return { attributes: dropAttributes(attributes), children, styles };
}
export {
  makeSvgComponent,
  parseSvgComponentData
};
