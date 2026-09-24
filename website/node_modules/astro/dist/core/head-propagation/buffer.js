async function collectPropagatedHeadParts(input) {
  const collectedHeadParts = [];
  const pendingSlotEvaluations = input.result._metadata?.pendingSlotEvaluations ?? [];
  const drainPendingSlots = async () => {
    while (pendingSlotEvaluations.length > 0) {
      const batch = pendingSlotEvaluations.splice(0, pendingSlotEvaluations.length);
      await Promise.all(batch);
    }
  };
  await drainPendingSlots();
  for (const propagator of input.propagators) {
    const returnValue = await propagator.init(input.result);
    if (input.isHeadAndContent(returnValue) && returnValue.head) {
      collectedHeadParts.push(returnValue.head);
    }
    await drainPendingSlots();
  }
  return collectedHeadParts;
}
export {
  collectPropagatedHeadParts
};
