package net.sf.sevenzipjbinding.simple.impl;

import java.util.Date;

import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 * Standard implementation of {@link ISimpleInArchiveItem}.
 * 
 * @author Boris Brodski
 * @version 1.0
 */
public class SimpleInArchiveItemImpl implements ISimpleInArchiveItem {

	private final SimpleInArchiveImpl simpleInArchiveImpl;
	private final int index;

	public SimpleInArchiveItemImpl(SimpleInArchiveImpl simpleInArchiveImpl, int index) {
		this.simpleInArchiveImpl = simpleInArchiveImpl;
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */

	public String getPath() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getStringProperty(index, PropID.PATH);
	}

	/**
	 * {@inheritDoc}
	 */

	public Integer getAttributes() throws SevenZipException {
		return (Integer) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */

	public Integer getCRC() throws SevenZipException {
		return (Integer) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */

	public String getComment() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getStringProperty(index, PropID.COMMENT);
	}

	/**
	 * {@inheritDoc}
	 */

	public Date getCreationTime() throws SevenZipException {
		return (Date) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.CREATION_TIME);
	}

	/**
	 * {@inheritDoc}
	 */

	public String getGroup() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getStringProperty(index, PropID.GROUP);
	}

	/**
	 * {@inheritDoc}
	 */

	public String getHostOS() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getStringProperty(index, PropID.HOST_OS);
	}

	/**
	 * {@inheritDoc}
	 */

	public Date getLastAccessTime() throws SevenZipException {
		return (Date) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.LAST_ACCESS_TIME);
	}

	/**
	 * {@inheritDoc}
	 */

	public Date getLastWriteTime() throws SevenZipException {
		return (Date) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.LAST_WRITE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */

	public String getMethod() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getStringProperty(index, PropID.METHOD);
	}

	/**
	 * {@inheritDoc}
	 */

	public Long getPackedSize() throws SevenZipException {
		return (Long) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.PACKED_SIZE);
	}

	/**
	 * {@inheritDoc}
	 */

	public Integer getPosition() throws SevenZipException {
		return (Integer) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.POSITION);
	}

	/**
	 * {@inheritDoc}
	 */

	public Long getSize() throws SevenZipException {
		return (Long) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.SIZE);
	}

	/**
	 * {@inheritDoc}
	 */

	public String getUser() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getStringProperty(index, PropID.USER);
	}

	/**
	 * {@inheritDoc}
	 */

	public Boolean isCommented() throws SevenZipException {
		return (Boolean) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.COMMENTED);
	}

	/**
	 * {@inheritDoc}
	 */

	public Boolean isEncrypted() throws SevenZipException {
		return (Boolean) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.ENCRYPTED);
	}

	/**
	 * {@inheritDoc}
	 */

	public Boolean isFolder() throws SevenZipException {
		return (Boolean) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().getProperty(index, PropID.IS_FOLDER);
	}

	public void extractSlow(ISequentialOutStream outStream) throws SevenZipException {
		simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().extractSlow(index, outStream);
	}

	@Override
	public void extractSlow(ISequentialOutStream outStream, String password) throws SevenZipException {
		simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().extractSlow(index, outStream, password);
	}

	/**
	 * {@inheritDoc}
	 */

	public int getItemIndex() {
		return index;
	}
}
