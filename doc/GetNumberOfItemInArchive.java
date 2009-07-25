package net.sf.sevenzipjbinding.junit.snipplets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GetNumberOfItemInArchive {
	@Test
	public void snippletRunner() {
		assertEquals(2, getNumberOfItemsInArchive());
	}

	/* BEGIN_SNIPPLET(getNumberOfItemsInArchive) */
	private int getNumberOfItemsInArchive() {
		int i = 1 + 1;
		return i;
	}
	/* END_SNIPPLET */
}
