const NOOP_MIDDLEWARE_FN = async (_ctx, next) => {
  const response = await next();
  return response;
};
export {
  NOOP_MIDDLEWARE_FN
};
