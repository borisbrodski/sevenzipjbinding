const RenderInstructionSymbol = /* @__PURE__ */ Symbol.for("astro:render");
function createRenderInstruction(instruction) {
  instruction[RenderInstructionSymbol] = true;
  return instruction;
}
function isRenderInstruction(chunk) {
  return chunk && typeof chunk === "object" && chunk[RenderInstructionSymbol];
}
function isScriptInstruction(chunk) {
  return chunk && typeof chunk === "object" && "type" in chunk && chunk.type === "script";
}
export {
  createRenderInstruction,
  isRenderInstruction,
  isScriptInstruction
};
