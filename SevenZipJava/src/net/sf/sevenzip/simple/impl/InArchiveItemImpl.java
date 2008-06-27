package net.sf.sevenzip.simple.impl;

import java.util.Date;

import net.sf.sevenzip.ISevenZipInArchive;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.simple.IInArchiveItem;

public class InArchiveItemImpl implements IInArchiveItem {

	private final InArchiveImpl inArchiveImpl;
	private final int index;
	private final ISevenZipInArchive sevenZipInArchive = null;

	public InArchiveItemImpl(InArchiveImpl inArchiveImpl, int index) {
		this.inArchiveImpl = inArchiveImpl;
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() throws SevenZipException {
		return sevenZipInArchive.getStringProperty(index, PropID.PATH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAttributes() throws SevenZipException {
		return (Integer) sevenZipInArchive
				.getProperty(index, PropID.ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getCRC() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getComment() throws SevenZipException {
		return sevenZipInArchive.getStringProperty(index, PropID.COMMENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getCreationTime() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGroup() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHostOS() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastAccessTime() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastWriteTime() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethod() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getPackedSize() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPosition() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getSize() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUser() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isCommented() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isEncrypted() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isFolder() throws SevenZipException {
		// TODO Auto-generated method stub
		return null;
	}
}
