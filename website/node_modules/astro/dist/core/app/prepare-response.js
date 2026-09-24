import { responseSentSymbol } from "../constants.js";
import { getSetCookiesFromResponse } from "../cookies/index.js";
function prepareResponse(response, { addCookieHeader }) {
  if (addCookieHeader) {
    for (const setCookieHeaderValue of getSetCookiesFromResponse(response)) {
      response.headers.append("set-cookie", setCookieHeaderValue);
    }
  }
  Reflect.set(response, responseSentSymbol, true);
}
export {
  prepareResponse
};
