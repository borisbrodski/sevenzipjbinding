package net.sf.sevenzipjbinding.maven.test;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

import org.junit.Test;

public class SimpleTest {
	@Test
	public void initTest() throws SevenZipNativeInitializationException {
		SevenZip.initSevenZipFromPlatformJAR();
		System.out.println("Version: " + SevenZip.getSevenZipVersion().version);
	}
}
