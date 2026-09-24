const decoder = new TextDecoder();
const toUTF8String = (input, start = 0, end = input.length) => decoder.decode(input.slice(start, end));
const toHexString = (input, start = 0, end = input.length) => input.slice(start, end).reduce((memo, i) => memo + `0${i.toString(16)}`.slice(-2), "");
const getView = (input, offset) => new DataView(input.buffer, input.byteOffset + offset);
const readInt16LE = (input, offset = 0) => getView(input, offset).getInt16(0, true);
const readUInt16BE = (input, offset = 0) => getView(input, offset).getUint16(0, false);
const readUInt16LE = (input, offset = 0) => getView(input, offset).getUint16(0, true);
const readUInt24LE = (input, offset = 0) => {
  const view = getView(input, offset);
  return view.getUint16(0, true) + (view.getUint8(2) << 16);
};
const readInt32LE = (input, offset = 0) => getView(input, offset).getInt32(0, true);
const readUInt32BE = (input, offset = 0) => getView(input, offset).getUint32(0, false);
const readUInt32LE = (input, offset = 0) => getView(input, offset).getUint32(0, true);
const readUInt64 = (input, offset, isBigEndian) => getView(input, offset).getBigUint64(0, !isBigEndian);
const methods = {
  readUInt16BE,
  readUInt16LE,
  readUInt32BE,
  readUInt32LE
};
function readUInt(input, bits, offset = 0, isBigEndian = false) {
  const endian = isBigEndian ? "BE" : "LE";
  const methodName = `readUInt${bits}${endian}`;
  return methods[methodName](input, offset);
}
const MIN_BOX_HEADER = 8;
function readBox(input, offset) {
  if (input.length - offset < MIN_BOX_HEADER) return;
  const boxSize = readUInt32BE(input, offset);
  if (boxSize === 0) {
    return {
      name: toUTF8String(input, offset + 4, offset + 8),
      offset,
      size: input.length - offset
    };
  }
  if (boxSize === 1) return;
  if (boxSize < MIN_BOX_HEADER) return;
  if (input.length - offset < boxSize) return;
  return {
    name: toUTF8String(input, offset + 4, offset + 8),
    offset,
    size: boxSize
  };
}
function findBox(input, boxName, startOffset) {
  let offset = startOffset;
  while (offset < input.length) {
    const box = readBox(input, offset);
    if (!box) break;
    if (box.name === boxName) return box;
    if (box.size < MIN_BOX_HEADER) break;
    const nextOffset = offset + box.size;
    if (nextOffset <= offset) break;
    offset = nextOffset;
  }
}
export {
  findBox,
  readInt16LE,
  readInt32LE,
  readUInt,
  readUInt16BE,
  readUInt16LE,
  readUInt24LE,
  readUInt32BE,
  readUInt32LE,
  readUInt64,
  toHexString,
  toUTF8String
};
