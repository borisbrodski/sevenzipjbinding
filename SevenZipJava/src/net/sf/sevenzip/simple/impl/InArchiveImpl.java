package net.sf.sevenzip.simple.impl;

import net.sf.sevenzip.SevenZipException;
import net.sf.sevenzip.simple.IInArchive;
import net.sf.sevenzip.simple.IInArchiveItem;

public class InArchiveImpl implements IInArchive {

	@Override
	public void close() throws SevenZipException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return 0;
	}

}
