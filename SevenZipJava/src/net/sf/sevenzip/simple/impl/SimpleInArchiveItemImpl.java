package net.sf.sevenzip.simple.impl;

import java.util.Date;

import net.sf.sevenzip.ISequentialOutStream;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.simple.ISimpleInArchiveItem;

public class SimpleInArchiveItemImpl implements ISimpleInArchiveItem {

	private final SimpleInArchiveImpl simpleInArchiveImpl;
	private final int index;

	public SimpleInArchiveItemImpl(SimpleInArchiveImpl simpleInArchiveImpl,
			int index) {
		this.simpleInArchiveImpl = simpleInArchiveImpl;
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.PATH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAttributes() throws SevenZipException {
		return (Integer) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getCRC() throws SevenZipException {
		return (Integer) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getComment() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.COMMENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getCreationTime() throws SevenZipException {
		return (Date) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.CREATION_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGroup() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.GROUP);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHostOS() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.HOST_OS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastAccessTime() throws SevenZipException {
		return (Date) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.LAST_ACCESS_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastWriteTime() throws SevenZipException {
		return (Date) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.LAST_WRITE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethod() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.METHOD);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getPackedSize() throws SevenZipException {
		return (Long) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.PACKED_SIZE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPosition() throws SevenZipException {
		return (Integer) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.POSITION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getSize() throws SevenZipException {
		return (Long) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.SIZE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUser() throws SevenZipException {
		return simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.USER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isCommented() throws SevenZipException {
		return (Boolean) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.COMMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isEncrypted() throws SevenZipException {
		return (Boolean) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.ENCRYPTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isFolder() throws SevenZipException {
		return (Boolean) simpleInArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.IS_FOLDER);
	}

	public void extractSlow(ISequentialOutStream outStream)
			throws SevenZipException {
		simpleInArchiveImpl.testAndGetSafeSevenZipInArchive().extractSlow(
				index, outStream);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getItemIndex() {
		return index;
	}
}
