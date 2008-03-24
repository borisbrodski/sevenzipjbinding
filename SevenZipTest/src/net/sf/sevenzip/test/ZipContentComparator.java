package net.sf.sevenzip.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.sevenzip.IInArchive;
import net.sf.sevenzip.ISequentialOutStream;
import net.sf.sevenzip.PropID;
import net.sf.sevenzip.SevenZipException;

public class ZipContentComparator {
	private class UniversalFileEntryInfo implements
			Comparable<UniversalFileEntryInfo> {
		public int itemId;
		public String itemIdString;
		public String filename;
		public long realSize;
		public Date fileLastModificationTime;

		public int compareTo(UniversalFileEntryInfo o) {
			return filename.compareTo(o.filename);
		}

	}

	private final ZipFile expectedZipFile;
	private Enumeration<? extends ZipEntry> expectedZipEntries;

	private final IInArchive actualSevenZipArchive;
	List<String> errorMessages = null;

	public ZipContentComparator(IInArchive sevenZipArchive, ZipFile zipFile) {
		this.actualSevenZipArchive = sevenZipArchive;
		this.expectedZipFile = zipFile;
		expectedZipEntries = zipFile.entries();
	}

	private void compare() {
		try {
		errorMessages = new ArrayList<String>();
		List<UniversalFileEntryInfo> expectedFileNames;
		List<UniversalFileEntryInfo> actualFileNames;

		expectedFileNames = getExpectedFileNameList();
		actualFileNames = getActualFileNameList();

		Collections.sort(expectedFileNames);
		Collections.sort(actualFileNames);

		if (expectedFileNames.size() != actualFileNames.size()) {
			errorMessages
					.add("Difference in count of files in archive: expected "
							+ expectedFileNames.size() + ", found "
							+ actualFileNames.size());
			return;
		}

		for (int i = 0; i < actualFileNames.size(); i++) {
			UniversalFileEntryInfo actualInfo = actualFileNames.get(i);
			UniversalFileEntryInfo expectedInfo = expectedFileNames.get(i);

			if (!actualInfo.filename.equalsIgnoreCase(expectedInfo.filename)) {
				error("Filename missmatch: expected '" + expectedInfo.filename
						+ "', actual '" + actualInfo.filename + "'");
			}

			if (actualInfo.realSize != expectedInfo.realSize) {
				error("Real file size missmatch for file '"
						+ expectedInfo.filename + "': expected "
						+ expectedInfo.realSize + ", actual "
						+ actualInfo.realSize);
			}

			if (!actualInfo.fileLastModificationTime
					.equals(expectedInfo.fileLastModificationTime)) {
				error("Last modification time missmatch for file '"
						+ expectedInfo.filename + "': expected "
						+ expectedInfo.fileLastModificationTime + ", actual "
						+ actualInfo.fileLastModificationTime);
			}
		}

		for (int i = 0; i < actualFileNames.size(); i++) {
			UniversalFileEntryInfo actualInfo = actualFileNames.get(i);
			UniversalFileEntryInfo expectedInfo = expectedFileNames.get(i);

			InputStream inputStream = expectedZipFile
					.getInputStream(expectedZipFile
							.getEntry(expectedInfo.itemIdString));
			ISequentialOutStream outStream = new TestSequentialOutStream(
					expectedInfo.filename, inputStream);
			long start = System.currentTimeMillis();
			actualSevenZipArchive.extract(actualInfo.itemId, outStream);
			System.out.println("Extraction in " + (System.currentTimeMillis() - start) / 1000.0 + " sec (size: " + expectedInfo.realSize + ")");
		}
		} catch (Exception e) {
			e.printStackTrace();
			error("Exception occurs: " + e);
		}
	}

	private void error(String message) {
		if (errorMessages.size() > 10) {
			return;
		}
		if (errorMessages.size() == 10) {
			errorMessages.add("...");
			return;
		}
		errorMessages.add(message);
	}

	private String getStringProperty(IInArchive sevenZip, int index,
			PropID propID) throws SevenZipException {
		String string = (String) sevenZip.getProperty(index, propID);
		if (string == null) {
			return "";
		}
		return string;
	}

	private List<UniversalFileEntryInfo> getActualFileNameList()
			throws SevenZipException {
		List<UniversalFileEntryInfo> fileNames = new ArrayList<UniversalFileEntryInfo>();

		for (int i = 0; i < actualSevenZipArchive.getNumberOfItems(); i++) {
			if (((Boolean) actualSevenZipArchive.getProperty(i,
					PropID.IS_FOLDER)).booleanValue()) {
				continue;
			}
			UniversalFileEntryInfo info = new UniversalFileEntryInfo();
			info.filename = getStringProperty(actualSevenZipArchive, i,
					PropID.PATH)
					+ getStringProperty(actualSevenZipArchive, i, PropID.NAME)
					+ getStringProperty(actualSevenZipArchive, i,
							PropID.EXTENSION);
			info.filename = info.filename.replace('\\', '/');
			info.itemId = i;
			info.realSize = ((Long) actualSevenZipArchive.getProperty(i,
					PropID.SIZE)).longValue();
			info.fileLastModificationTime = (Date) actualSevenZipArchive
					.getProperty(i, PropID.LAST_WRITE_TIME);
			fileNames.add(info);
		}

		return fileNames;
	}

	private List<UniversalFileEntryInfo> getExpectedFileNameList() {
		List<UniversalFileEntryInfo> fileNames = new ArrayList<UniversalFileEntryInfo>();

		while (expectedZipEntries.hasMoreElements()) {
			ZipEntry zipEntry = expectedZipEntries.nextElement();

			if (zipEntry.isDirectory()) {
				continue;
			}
			UniversalFileEntryInfo info = new UniversalFileEntryInfo();

			info.itemIdString = zipEntry.getName();
			info.filename = info.itemIdString.replace('\\', '/');
			info.filename = info.filename
					.substring(info.filename.indexOf('/') + 1);
			info.realSize = zipEntry.getSize();
			if (zipEntry.getTime() != -1) {
				info.fileLastModificationTime = new Date(zipEntry.getTime());
			}
			fileNames.add(info);
		}

		return fileNames;
	}

	public boolean isEqual() {
		if (errorMessages == null) {
			compare();
		}
		return errorMessages.size() == 0;
	}

	public String getErrorMessage() {
		if (errorMessages == null) {
			compare();
		}

		StringBuffer stringBuffer = new StringBuffer();

		for (String errrorMessage : errorMessages) {
			if (stringBuffer.length() > 0) {
				stringBuffer.append('\n');
			}
			stringBuffer.append(errrorMessage);
		}

		return stringBuffer.toString();
	}

	public void print() throws SevenZipException {
		List<UniversalFileEntryInfo> expectedFileNames;
		List<UniversalFileEntryInfo> actualFileNames;

		expectedFileNames = getExpectedFileNameList();
		actualFileNames = getActualFileNameList();

		Collections.sort(expectedFileNames);
		Collections.sort(actualFileNames);

		for (int i = 0; i < Math.max(expectedFileNames.size(), actualFileNames
				.size()); i++) {
			if (i < expectedFileNames.size()) {
				UniversalFileEntryInfo universalFileEntryInfo1 = expectedFileNames
						.get(i);
				System.out.println("EX: " + universalFileEntryInfo1.filename);
			} else {
				System.out.println("EX: <none>");
			}

			if (i < actualFileNames.size()) {
				UniversalFileEntryInfo universalFileEntryInfo2 = actualFileNames
						.get(i);
				System.out.println("AC: " + universalFileEntryInfo2.filename);
			} else {
				System.out.println("AC: <none>");
			}
		}
	}

	public class TestSequentialOutStream implements ISequentialOutStream {
		private final InputStream inputStream;
		private final String filename;

		public TestSequentialOutStream(String filename, InputStream inputStream) {
			this.filename = filename;
			this.inputStream = inputStream;
		}

		@Override
		public int write(byte[] data) {
			try {
				if (inputStream.available() < data.length) {
					error("More data as expected for file '" + filename + "'");
					throw new RuntimeException("More data as expected for file '" + filename + "'");
				} else {
					byte[] expectedData = new byte[data.length];
					inputStream.read(expectedData);

					if (!Arrays.equals(expectedData, data)) {
						error("Extracted data missmatched for file '"
								+ filename + "'");
						throw new RuntimeException("Extracted data missmatched for file '"
								+ filename + "'");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				error("Unexpected exception: " + e);
				throw new RuntimeException("Unexpected exception: " + e,e);
			}
			return data.length;
		}

	}
}
