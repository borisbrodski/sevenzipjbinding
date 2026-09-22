import { BaseApp } from "./base.js";
class App extends BaseApp {
  isDev() {
    return false;
  }
  // Should we log something for our users?
  logRequest(_options) {
  }
}
export {
  App
};
