package net.sf.sevenzipjbinding.junit.snippets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GetNumberOfItemInArchive {
	@Test
	public void snippetRunner() {
		assertEquals(2, getNumberOfItemsInArchive());
	}

	/* BEGIN_SNIPPET(getNumberOfItemsInArchive) */
	private int getNumberOfItemsInArchive() {
		String s = "Test String";
		int i = 1 + 1;
		return i;
	}
	/* END_SNIPPET */
}
