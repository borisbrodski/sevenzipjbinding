import { createRenderInstruction } from "./instruction.js";
const TEMPLATE_ENTER = Object.freeze(createRenderInstruction({ type: "template-enter" }));
const TEMPLATE_EXIT = Object.freeze(createRenderInstruction({ type: "template-exit" }));
function templateEnter(_result) {
  return TEMPLATE_ENTER;
}
function templateExit(_result) {
  return TEMPLATE_EXIT;
}
export {
  templateEnter,
  templateExit
};
