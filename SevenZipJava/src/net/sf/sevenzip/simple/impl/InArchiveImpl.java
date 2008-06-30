package net.sf.sevenzip.simple.impl;

import net.sf.sevenzip.ISevenZipInArchive;
import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.simple.IInArchive;
import net.sf.sevenzip.simple.IInArchiveItem;

public class InArchiveImpl implements IInArchive {
	private final ISevenZipInArchive sevenZipInArchive;
	private boolean wasClosed = false;

	public InArchiveImpl(ISevenZipInArchive sevenZipInArchive) {
		this.sevenZipInArchive = sevenZipInArchive;
	}

	@Override
	public void close() throws SevenZipException {
		sevenZipInArchive.close();
		wasClosed = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IInArchiveItem[] getArchiveItems() throws SevenZipException {
		IInArchiveItem[] result = new IInArchiveItem[getNumberOfItems()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new InArchiveItemImpl(this, i);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfItems() throws SevenZipException {
		return testAndGetSafeSevenZipInArchive().getNumberOfItems();
	}

	/**
	 * Tests, if 7-Zip In archive interface can be accessed safely.
	 * 
	 * @return 7-Zip In archive interface
	 * @throws SevenZipException
	 *             archive can't be accessed any more
	 */
	public ISevenZipInArchive testAndGetSafeSevenZipInArchive()
			throws SevenZipException {
		if (wasClosed) {
			throw new SevenZipException("Archive was closed");
		}
		return sevenZipInArchive;
	}
}
