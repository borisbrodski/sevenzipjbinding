import { isPromise } from "../../util.js";
import { renderChild } from "../any.js";
import { registerIfPropagating } from "../head-propagation/runtime.js";
import { isHeadAndContent } from "./head-and-content.js";
const astroComponentInstanceSym = /* @__PURE__ */ Symbol.for("astro.componentInstance");
const CLIENT_DIRECTIVE_PREFIX = "client:";
class AstroComponentInstance {
  [astroComponentInstanceSym] = true;
  result;
  props;
  slotValues;
  factory;
  returnValue;
  constructor(result, props, slots, factory) {
    this.result = result;
    this.props = props;
    this.factory = factory;
    this.slotValues = slots;
    for (const name in slots) {
      if (this.slotValues === slots) {
        this.slotValues = {};
      }
      let didRender = false;
      let value = slots[name](result);
      if (result._metadata.routeHasPropagation && isPromise(value)) {
        result._metadata.pendingSlotEvaluations.push(value);
      }
      this.slotValues[name] = () => {
        if (!didRender) {
          didRender = true;
          return value;
        }
        return slots[name](result);
      };
    }
  }
  init(result) {
    if (this.returnValue !== void 0) {
      return this.returnValue;
    }
    this.returnValue = this.factory(result, this.props, this.slotValues);
    if (isPromise(this.returnValue)) {
      this.returnValue.then((resolved) => {
        this.returnValue = resolved;
      }).catch(() => {
      });
    }
    return this.returnValue;
  }
  // NOTE: This render call can't be pre-invoked outside of this function as it'll also initialize the slots
  // recursively, which causes each Astro components in the tree to be called bottom-up, and is incorrect.
  // The slots are initialized eagerly for head propagation.
  render(destination) {
    const returnValue = this.init(this.result);
    if (isPromise(returnValue)) {
      return returnValue.then((x) => this.renderImpl(destination, x));
    }
    return this.renderImpl(destination, returnValue);
  }
  renderImpl(destination, returnValue) {
    if (isHeadAndContent(returnValue)) {
      return returnValue.content.render(destination);
    } else {
      return renderChild(destination, returnValue);
    }
  }
}
function validateComponentProps(props, clientDirectives, displayName) {
  if (props != null) {
    for (const prop in props) {
      if (prop.startsWith(CLIENT_DIRECTIVE_PREFIX) && clientDirectives.has(prop.slice(CLIENT_DIRECTIVE_PREFIX.length))) {
        console.warn(
          `You are attempting to render <${displayName} ${prop} />, but ${displayName} is an Astro component. Astro components do not render in the client and should not have a hydration directive. Please use a framework component for client rendering.`
        );
      }
    }
  }
}
function createAstroComponentInstance(result, displayName, factory, props, slots = {}) {
  validateComponentProps(props, result.clientDirectives, displayName);
  const instance = new AstroComponentInstance(result, props, slots, factory);
  registerIfPropagating(result, factory, instance);
  return instance;
}
function isAstroComponentInstance(obj) {
  return typeof obj === "object" && obj !== null && !!obj[astroComponentInstanceSym];
}
export {
  AstroComponentInstance,
  createAstroComponentInstance,
  isAstroComponentInstance
};
