package net.sf.sevenzipjbinding;

public enum FileAttribute {
    FILE_ATTRIBUTE_READONLY(1), //
    FILE_ATTRIBUTE_HIDDEN(2), //
    FILE_ATTRIBUTE_SYSTEM(4), //
    FILE_ATTRIBUTE_DIRECTORY(16), //
    FILE_ATTRIBUTE_ARCHIVE(32), //
    FILE_ATTRIBUTE_DEVICE(64), //
    FILE_ATTRIBUTE_NORMAL(128), //
    FILE_ATTRIBUTE_TEMPORARY(256), //
    FILE_ATTRIBUTE_SPARSE_FILE(512), //
    FILE_ATTRIBUTE_REPARSE_POINT(1024), //
    FILE_ATTRIBUTE_COMPRESSED(2048), //
    FILE_ATTRIBUTE_OFFLINE(0x1000), //
    FILE_ATTRIBUTE_ENCRYPTED(0x4000), //
    ;

    private final int value;

    private FileAttribute(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
