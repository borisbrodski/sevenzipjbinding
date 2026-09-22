class InMemorySource {
  #store;
  constructor(store) {
    this.#store = store;
  }
  hasCollection(collection) {
    return this.#store.hasCollection(collection);
  }
  get(collection, key) {
    return this.#store.get(collection, key);
  }
  entries(collection) {
    return this.#store.entries(collection);
  }
  values(collection) {
    return this.#store.values(collection);
  }
  keys(collection) {
    return this.#store.keys(collection);
  }
  has(collection, key) {
    return this.#store.has(collection, key);
  }
  collections() {
    return this.#store.collections();
  }
}
export {
  InMemorySource
};
