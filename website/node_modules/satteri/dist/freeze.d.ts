/** Freeze JSON-shaped data recursively; never call on objects with lazy getters. */
export declare function deepFreeze<T>(value: T): T;
