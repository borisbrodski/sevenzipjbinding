package net.sf.sevenzip.simple.impl;

import java.util.Date;

import net.sf.sevenzip.ISequentialOutStream;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.simple.IInArchiveItem;

public class InArchiveItemImpl implements IInArchiveItem {

	private final InArchiveImpl inArchiveImpl;
	private final int index;

	public InArchiveItemImpl(InArchiveImpl inArchiveImpl, int index) {
		this.inArchiveImpl = inArchiveImpl;
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() throws SevenZipException {
		return inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.PATH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAttributes() throws SevenZipException {
		return (Integer) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getCRC() throws SevenZipException {
		return (Integer) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getComment() throws SevenZipException {
		return inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.COMMENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getCreationTime() throws SevenZipException {
		return (Date) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.CREATION_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGroup() throws SevenZipException {
		return inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.GROUP);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHostOS() throws SevenZipException {
		return inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.HOST_OS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastAccessTime() throws SevenZipException {
		return (Date) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.LAST_ACCESS_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastWriteTime() throws SevenZipException {
		return (Date) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.LAST_WRITE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethod() throws SevenZipException {
		return inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.METHOD);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getPackedSize() throws SevenZipException {
		return (Long) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.PACKED_SIZE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPosition() throws SevenZipException {
		return (Integer) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.POSITION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getSize() throws SevenZipException {
		return (Long) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.SIZE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUser() throws SevenZipException {
		return inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getStringProperty(index, PropID.USER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isCommented() throws SevenZipException {
		return (Boolean) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.COMMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isEncrypted() throws SevenZipException {
		return (Boolean) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.ENCRYPTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isFolder() throws SevenZipException {
		return (Boolean) inArchiveImpl.testAndGetSafeSevenZipInArchive()
				.getProperty(index, PropID.IS_FOLDER);
	}

	public void extractSlow(ISequentialOutStream outStream)
			throws SevenZipException {
		inArchiveImpl.testAndGetSafeSevenZipInArchive().extract(index,
				outStream);

	}
}
