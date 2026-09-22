class RemoteRuntimeFontFileUrlResolver {
  #urls;
  #address;
  constructor({
    urls,
    address
  }) {
    this.#urls = urls;
    this.#address = address;
  }
  resolve(url, requestUrl) {
    if (!this.#urls.has(url)) {
      return null;
    }
    if (!url.startsWith("/")) {
      if (this.#address) {
        url = new URL(url).pathname;
      } else {
        return url;
      }
    }
    if (this.#address) {
      const host = this.#address.family === "IPv6" ? `[${this.#address.address}]` : this.#address.address;
      return `http://${host}:${this.#address.port}${url}`;
    }
    if (requestUrl) {
      return `${requestUrl.origin}${url}`;
    }
    throw new Error("Server address unavailable, this should not happen. Open an issue.");
  }
}
export {
  RemoteRuntimeFontFileUrlResolver
};
