package net.sf.sevenzipjbinding;

import net.sf.sevenzipjbinding.impl.OutArchiveBZip2Impl;
import net.sf.sevenzipjbinding.impl.OutArchiveGZipImpl;
import net.sf.sevenzipjbinding.impl.OutArchiveImpl;
import net.sf.sevenzipjbinding.impl.OutArchiveSevenZipImpl;
import net.sf.sevenzipjbinding.impl.OutArchiveTarImpl;
import net.sf.sevenzipjbinding.impl.OutArchiveZipImpl;

/**
 * Enumeration of all supported archive types. <blockquote>
 * 
 * <table border="1">
 * <tr>
 * <td><b>Format</b></td>
 * <td><b>Implementation</b></td>
 * <td><b>Test</b></td>
 * <td><b>Enum value</b></td>
 * </tr>
 * <tr align="center">
 * <td>7z</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #SEVEN_ZIP}</td>
 * </tr>
 * <tr align="center">
 * <td>Arj</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #ARJ}</td>
 * </tr>
 * <tr align="center">
 * <td>BZip2</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #BZIP2}</td>
 * </tr>
 * <tr align="center">
 * <td>Cab</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #CAB}</td>
 * </tr>
 * <tr align="center">
 * <td>Chm</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #CHM}</td>
 * </tr>
 * <tr align="center">
 * <td>Compound</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Cpio</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #CPIO}</td>
 * </tr>
 * <tr align="center">
 * <td>Deb</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #DEB}</td>
 * </tr>
 * <tr align="center">
 * <td>Dmg</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Elf</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>GZip</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #GZIP}</td>
 * </tr>
 * <tr align="center">
 * <td>Hfs</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Iso</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #ISO}</td>
 * </tr>
 * <tr align="center">
 * <td>Lzh</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #LZH}</td>
 * </tr>
 * <tr align="center">
 * <td>Lzma</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #LZMA}</td>
 * </tr>
 * <tr align="center">
 * <td>Macho</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Mub</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Nsis</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #NSIS}</td>
 * </tr>
 * <tr align="center">
 * <td>Pa</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Rar</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #RAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Rpm</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #RAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Split</td>
 * <td>-</td>
 * <td>-</td>
 * <td>-</td>
 * </tr>
 * <tr align="center">
 * <td>Tar</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #TAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Udf</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #UDF}</td>
 * </tr>
 * <tr align="center">
 * <td>Wim</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #WIM}</td>
 * </tr>
 * <tr align="center">
 * <td>Xar</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #XAR}</td>
 * </tr>
 * <tr align="center">
 * <td>Z</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #Z}</td>
 * </tr>
 * <tr align="center">
 * <td>Zip</td>
 * <td>X</td>
 * <td>X</td>
 * <td>{@link #ZIP}</td>
 * </tr>
 * </table>
 * <blockquote> <br>
 * 
 * @author Boris Brodski
 * @version 4.65-1
 */
public enum ArchiveFormat {
    /**
     * Zip format.
     */
    ZIP("Zip", OutArchiveZipImpl.class),

    /**
     * Tar format.
     */
    TAR("Tar", OutArchiveTarImpl.class),

    /**
     * Split format.
     */
    SPLIT("Split"),

    /**
     * Rar format.
     */
    RAR("Rar"), //

    /**
     * Lzma format.
     */
    LZMA("Lzma"),

    /**
     * Iso format.
     */
    ISO("Iso"),

    /**
     * Hfs format
     */
    HFS("HFS"),

    /**
     * Gzip format
     */
    GZIP("GZip", OutArchiveGZipImpl.class),

    /**
     * Cpio format.
     */
    CPIO("Cpio"),

    /**
     * BZip2 format.
     */
    BZIP2("BZIP2", OutArchiveBZip2Impl.class),

    /**
     * 7z format.
     */
    SEVEN_ZIP("7z", OutArchiveSevenZipImpl.class),

    /**
     * Z format.
     */
    Z("Z"),

    /**
     * Arj format
     */
    ARJ("Arj"), //

    /**
     * Cab format.
     */
    CAB("Cab"),

    /**
     * Lzh
     */
    LZH("Lzh"),

    /**
     * Chm
     */
    CHM("Chm"),

    /**
     * Nsis
     */
    NSIS("Nsis"),

    /**
     * Deb
     */
    DEB("Deb"),

    /**
     * Rpm
     */
    RPM("Rpm"),

    /**
     * Udf
     */
    UDF("Udf"),

    /**
     * Udf
     */
    WIM("Wim"),

    /**
     * Xar
     */
    XAR("Xar");

    private String methodName;
    Class<? extends OutArchiveImpl> outArchiveImplementation;

    private ArchiveFormat(String methodName) {
        this(methodName, null);
    }

    private ArchiveFormat(String methodName, Class<? extends OutArchiveImpl> outArchiveImplementation) {
        this.methodName = methodName;
        this.outArchiveImplementation = outArchiveImplementation;
    }

    /**
     * Return name of the archive method
     * 
     * @return name of the archive method
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Return whether this archive type supports creation/update operations
     * 
     * @return <code>true</code> - creation/update operations are supported,<br>
     *         <code>false</code> - only archive extraction is supported
     */
    public boolean isOutArchiveSupported() {
        return outArchiveImplementation != null;
    }

    /**
     * Get corresponding implementation class for archive update operations.
     * 
     * @return the {@link IOutArchive} implementation class
     */
    Class<? extends OutArchiveImpl> getOutArchiveImplementation() {
        return outArchiveImplementation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return methodName;
    }

    /**
     * Finds the {@link ArchiveFormat} corresponding to the given out-archive interface.
     * 
     * @param outArchiveInterface
     *            out-archive interface
     * @return corresponding out-archive implementation class
     * @throws SevenZipException
     *             if no implementation class could be found.
     */
    static ArchiveFormat findOutArchiveImplementationToInterface(Class<? extends IOutArchive> outArchiveInterface)
            throws SevenZipException {
        if (outArchiveInterface == IOutArchive.class) {
            String ioutArchiveName = IOutArchive.class.getSimpleName();
            throw new SevenZipException("Can't determine corresponding archive format to the interface "
                    + ioutArchiveName + ". Please, provide a one of the concrete " + ioutArchiveName
                    + "XXX interfaces.");
        }
        for (ArchiveFormat archiveFormat : values()) {
            Class<? extends OutArchiveImpl> implementation = archiveFormat.getOutArchiveImplementation();
            if (implementation != null && outArchiveInterface.isAssignableFrom(implementation)) {
                return archiveFormat;
            }
        }
        throw new SevenZipException("Can't determine corresponding archive format to the interface "
                + IOutArchive.class.getSimpleName() + ".");
    }
}