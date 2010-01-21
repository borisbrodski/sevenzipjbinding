package net.sf.sevenzipjbinding;

/**
 * Enumeration for possible archive and archive item properties
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public enum PropID {
    /**
     * Dummy property1. First real property should have an item 2. So skip the position 2.
     */
    NO_PROPERTY1,

    /**
     * Dummy property2. First real property should have an item 2. So skip the position 1.
     */
    NO_PROPERTY2,

    /**
     * Unknown purpose
     */
    HANDLER_ITEM_INDEX, // = 2

    /**
     * Full path, name and extension of the file inside the archive. Example. <code>'dir/file.ext'</code>. Please note, that stream archive
     * formats such as gzip does not support this property, since it is always a single file (or stream) being
     * compressed.<br>
     * <br>
     * Type: {@link String}. <code>null</code> will be returned, if current archive type doesn't support this property.
     */
    PATH, //

    /**
     * In {@link IArchiveOpenCallback}: Name of the volume to get
     */
    NAME, //

    EXTENSION,

    /**
     * Flag either a item represents a folder or not. Please note, that some archive formats doesn't define special
     * items for folders. In this case you may get a item with a path <code>'dir/file'</code> without having an item for <code>'dir'</code> at all. <br>
     * <br>
     * Type: {@link Boolean}. <code>true</code> if item is a folder, otherwise <code>false</code>.
     * <code>Boolean.FALSE</code> is returned, if archive format doesn't support this property. This property is never
     * <code>null</code>.
     */
    IS_FOLDER,

    /**
     * Size of the original file. <code>-1</code> is returned, if no size known for this item.
     * 
     * Type: {@link Long} <code>null</code> will be returned, if current archive type doesn't support this property.
     */
    SIZE,

    /**
     * Size of the packed item in archive. Sometimes <code>0</code> will be returned. It means either <i>unknown</i> or
     * the item shares compressed data with other items and so take no additional space in archive.
     * 
     * Type: {@link Long} <code>null</code> will be returned, if current archive type doesn't support this property.
     */
    PACKED_SIZE, //

    /**
     * Archive item or file attributes. See {@link FileAttribute} enumeration for the supported bit masks.
     */
    ATTRIBUTES, //

    /**
     * Date and time of the file creation.
     */
    CREATION_TIME, //

    /**
     * Date and time of last access of the file.
     */
    LAST_ACCESS_TIME, //

    /**
     * Date and time the file was last time modified.
     */
    LAST_MODIFICATION_TIME, //

    SOLID, //
    COMMENTED,

    /**
     * Flag either a item encrypted or not. <br>
     * Type: {@link Boolean}. <code>true</code> if item is encrypted, otherwise <code>false</code>.
     * <code>Boolean.FALSE</code> is returned, if archive format doesn't support this property. This property is never
     * <code>null</code>.
     */
    ENCRYPTED, //
    SPLIT_BEFORE, //
    SPLIT_AFTER, //
    DICTIONARY_SIZE, // 
    CRC, //
    TYPE, //
    IS_ANTI, //
    METHOD, //
    HOST_OS, //
    FILE_SYSTEM, //
    USER, //
    GROUP, //
    BLOCK, //
    COMMENT, //
    POSITION, //
    PREFIX, //

    TOTAL_SIZE(0x1100), //
    FREE_SPACE(0x1101), //
    CLUSTER_SIZE(0x1102), //
    VOLUME_NAME(0x1103), //

    LOCAL_NAME(0x1200), //
    PROVIDER(0x1201), //

    USER_DEFINED(0x10000), //

    /**
     * Unknown PropID. This PropID shouldn't be used.
     */
    UNKNOWN(-1);

    private final int propIDIndex;

    private PropID(int propIDIndex) {
        this.propIDIndex = propIDIndex;
    }

    private PropID() {
        this.propIDIndex = ordinal();
    }

    /**
     * Native index of the property
     * 
     * @return Native index of the property
     */
    public int getPropIDIndex() {
        return propIDIndex;
    }

    /**
     * Get enumeration element by native property index
     * 
     * @param propIDIndex
     *            native property index
     * @return enumeration element
     */
    public static PropID getPropIDByIndex(int propIDIndex) {
        if (propIDIndex >= 0 && propIDIndex < values().length && values()[propIDIndex].getPropIDIndex() == propIDIndex) {
            return values()[propIDIndex];
        }

        for (int i = values().length - 1; i != -1; i--) {
            if (values()[i].getPropIDIndex() == propIDIndex) {
                return values()[i];
            }
        }

        return UNKNOWN;
    }
}
