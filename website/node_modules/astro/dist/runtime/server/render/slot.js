import { HTMLString, markHTMLString, unescapeHTML } from "../escape.js";
import { renderChild } from "./any.js";
import { renderTemplate } from "./astro/render-template.js";
import { chunkToString } from "./common.js";
import {
  isScriptInstruction
} from "./instruction.js";
const slotString = /* @__PURE__ */ Symbol.for("astro:slot-string");
class SlotString extends HTMLString {
  instructions;
  /**
   * The slot's content as an ordered stream. Unlike `instructions` (which holds
   * position-independent instructions like head/hydration content), scripts are
   * kept inline here so they render at their original position instead of being
   * hoisted to the start of the slot output.
   */
  chunks;
  [slotString];
  constructor(content, instructions, chunks = []) {
    super(content);
    this.instructions = instructions;
    this.chunks = chunks;
    this[slotString] = true;
  }
}
function isSlotString(str) {
  return !!str[slotString];
}
function mergeSlotInstructions(target, source) {
  if (source.instructions?.length) {
    target ??= [];
    target.push(...source.instructions);
  }
  return target;
}
class SlotRenderInstance {
  result;
  slotted;
  constructor(result, slotted) {
    this.result = result;
    this.slotted = slotted;
  }
  /** Must be called at most once per instance. */
  evaluate() {
    const { slotted } = this;
    return typeof slotted === "function" ? slotted(this.result) : slotted;
  }
  // Not `async`: that would return a promise even for sync slots, forcing the enclosing template to buffer.
  render(destination) {
    return renderChild(destination, this.evaluate());
  }
}
function renderSlot(result, slotted, fallback) {
  if (!slotted && fallback) {
    return renderSlot(result, fallback);
  }
  return new SlotRenderInstance(result, slotted);
}
async function renderSlotToString(result, slotted, fallback) {
  let content = "";
  let instructions = null;
  const chunks = [];
  const temporaryDestination = {
    write(chunk) {
      if (chunk instanceof SlotString) {
        content += chunk;
        if (chunk.chunks.length) {
          chunks.push(...chunk.chunks);
        }
        instructions = mergeSlotInstructions(instructions, chunk);
      } else if (chunk instanceof Response) return;
      else if (typeof chunk === "object" && "type" in chunk && typeof chunk.type === "string") {
        if (isScriptInstruction(chunk)) {
          chunks.push(chunk);
        } else {
          if (instructions === null) {
            instructions = [];
          }
          instructions.push(chunk);
        }
      } else {
        const str = chunkToString(result, chunk);
        content += str;
        chunks.push(str);
      }
    }
  };
  const renderInstance = renderSlot(result, slotted, fallback);
  await renderInstance.render(temporaryDestination);
  return markHTMLString(new SlotString(content, instructions, chunks));
}
async function renderSlots(result, slots = {}) {
  let slotInstructions = null;
  let children = {};
  if (slots) {
    await Promise.all(
      Object.entries(slots).map(
        ([key, value]) => renderSlotToString(result, value).then((output) => {
          if (output.instructions) {
            if (slotInstructions === null) {
              slotInstructions = [];
            }
            slotInstructions.push(...output.instructions);
          }
          if (output.chunks) {
            for (const part of output.chunks) {
              if (typeof part !== "string") {
                if (slotInstructions === null) {
                  slotInstructions = [];
                }
                slotInstructions.push(part);
              }
            }
          }
          children[key] = output;
        })
      )
    );
  }
  return { slotInstructions, children };
}
function createSlotValueFromString(content) {
  return function() {
    return renderTemplate`${unescapeHTML(content)}`;
  };
}
export {
  SlotRenderInstance,
  SlotString,
  createSlotValueFromString,
  isSlotString,
  mergeSlotInstructions,
  renderSlot,
  renderSlotToString,
  renderSlots
};
