/** `b"MDAR"` magic, read as a little-endian u32. */
export declare const ARENA_MAGIC = 1380009037;
/** `Arena<K>` kind tags carried in the header's `kind` field. */
export declare const KIND_MDAST = 1;
export declare const KIND_HAST = 2;
/** `ArenaNode` `#[repr(C)]` field byte offsets (u32 fields; `node_type` is a u8). */
export declare const FIELD: {
    readonly id: 0;
    readonly node_type: 4;
    readonly parent: 8;
    readonly start_offset: 12;
    readonly end_offset: 16;
    readonly start_line: 20;
    readonly start_column: 24;
    readonly end_line: 28;
    readonly end_column: 32;
    readonly children_start: 36;
    readonly children_count: 40;
    readonly data_offset: 44;
    readonly data_len: 48;
};
/** Raw-buffer header byte offsets (4-byte fields, u32 LE). */
export declare const HEADER: {
    readonly magic: 0;
    readonly kind: 4;
    readonly node_struct_size: 8;
    readonly node_count: 12;
    readonly nodes_offset: 16;
    readonly children_count: 20;
    readonly children_offset: 24;
    readonly type_data_len: 28;
    readonly type_data_offset: 32;
    readonly string_pool_len: 36;
    readonly string_pool_offset: 40;
    readonly node_data_count: 44;
    readonly node_data_offset: 48;
};
