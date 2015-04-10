package net.sf.sevenzipjbinding.junit.tools;

import static net.sf.sevenzipjbinding.junit.JUnitNativeTestBase.WEEK;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemCallback;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * The VirtualContent provides methods to generate random virtual content, to create an archive this that content and to
 * verify an archive against the virtual content. It also possible to write the virtual content to the disk and to print
 * a list of virtual files (item). Supported operations:
 * <ul>
 * <li>Generate a virtual content based on a set of parameters
 * <li>Create detached archive with the same content (compress virtual content)
 * <li>Verify archive given to match the virtual content
 * <li>Write content to the disk
 * <li>Print file names and sizes to the system out.
 * </ul>
 * VirtualContent uses ByteArrayStream to represents virtual file content (blob). This simplifies passing virtual file
 * content to the callback method.
 *
 * @author Boris Brodski
 * @version 4.65-1
 */
public class VirtualContent {
    public static class VirtualContentConfiguration {
        private boolean forbiddenRootDirectory;
        private boolean allowEmptyFiles;

        public boolean isForbiddenRootDirectory() {
            return forbiddenRootDirectory;
        }

        public void setForbiddenRootDirectory(boolean forbiddenRootDirectory) {
            this.forbiddenRootDirectory = forbiddenRootDirectory;
        }

        public boolean isAllowEmptyFiles() {
            return allowEmptyFiles;
        }

        public void setAllowEmptyFiles(boolean allowEmptyFiles) {
            this.allowEmptyFiles = allowEmptyFiles;
        }
    }

    class Item {
        ByteArrayStream blob;
        String path;

        Date creationTime;
        boolean creationTimeSet;

        protected Date lastAccessTime;
        protected boolean lastAccessTimeSet;

        protected Date modificationTime;
        protected boolean modificationTimeSet;

        String user;
        boolean userSet;

        String group;
        boolean groupSet;

        // TODO Add more parameters, that we can test

        Item(byte[] blobData) {
            blob = new ByteArrayStream(blobData, false);
        }

        public VirtualContent getVirtualContent() {
            return VirtualContent.this;
        }
    }

    private class ArchiveCreateCallback implements IOutCreateCallback<IOutItemCallback> {
        public ISequentialInStream getStream(int index) {
            ByteArrayStream byteArrayStream = itemList.get(index).blob;
            byteArrayStream.rewind();
            return byteArrayStream;
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public IOutItemCallback getOutItemCallback(final int index) throws SevenZipException {
            return new IOutItemCallback() {

                public boolean isAnti() throws SevenZipException {
                    return false;
                }

                public long getSize() throws SevenZipException {
                    return itemList.get(index).blob.getSize();
                }

                public String getPath() throws SevenZipException {
                    return itemList.get(index).path;
                }

                public Integer getPosixAttributes() throws SevenZipException {
                    return null;
                }

                public Integer getAttributes() throws SevenZipException {
                    return null;
                }

                public boolean isDir() throws SevenZipException {
                    return false;
                }

                public Date getModificationTime() throws SevenZipException {
                    itemList.get(index).modificationTimeSet = true;
                    return itemList.get(index).modificationTime;
                }

                public Date getLastAccessTime() throws SevenZipException {
                    itemList.get(index).lastAccessTimeSet = true;
                    return itemList.get(index).lastAccessTime;
                }

                public Date getCreationTime() throws SevenZipException {
                    itemList.get(index).creationTimeSet = true;
                    return itemList.get(index).creationTime;
                }

                public String getUser() throws SevenZipException {
                    itemList.get(index).userSet = true;
                    return itemList.get(index).user;
                }

                public String getGroup() throws SevenZipException {
                    itemList.get(index).groupSet = true;
                    return itemList.get(index).group;
                }
            };
        }
    }

    private class TestSequentailOutStream implements ISequentialOutStream {
        private final ByteArrayStream byteArrayStream;

        public TestSequentailOutStream(Item item) {
            byteArrayStream = item.blob;
            byteArrayStream.rewind();
        }

        public int write(byte[] data) throws SevenZipException {
            byte[] expectedData = new byte[data.length];
            assertEquals(Integer.valueOf(data.length), Integer.valueOf(byteArrayStream.read(expectedData)));

            try {
                assertArrayEquals(expectedData, data);
            } catch (org.junit.internal.ArrayComparisonFailure e) {
                for (int i = 0; i < data.length; i++) {
                    if (expectedData[i] != data[i]) {
                        System.out.println("Different at pos " + i);
                        break;
                    }
                }
                System.out.println("Length: " + data.length);
                System.out.println("exp: " + expectedData[128]);
                System.out.println("data: " + data[128]);
                e.printStackTrace();
                throw e;
            }

            return data.length;
        }

        void finish() throws SevenZipException {
            assertTrue(byteArrayStream.isEOF());
        }
    }

    private class TextExtractCallback implements IArchiveExtractCallback {
        private TestSequentailOutStream testSequentailOutStream;
        private final boolean[] extracted;
        private final IInArchive inArchive;

        TextExtractCallback(IInArchive inArchive) {
            this.inArchive = inArchive;
            extracted = new boolean[itemList.size()];
        }

        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
            ArchiveFormat archiveFormat = inArchive.getArchiveFormat();
            String path = (String) inArchive.getProperty(index, PropID.PATH);

            int myIndex;
            if (path != null) {
                Integer myIndexObjekt = usedNames.get(path.toUpperCase());
                assertNotNull("Directory passed to extraction (or index for path not found: '" + path + "')",
                        myIndexObjekt);
                myIndex = myIndexObjekt.intValue();
            } else {
                // Gzip/BZip2
                myIndex = 0;
            }
            Item item = itemList.get(myIndex);
            if (item.userSet) {
                assertEquals(item.user, inArchive.getProperty(index, PropID.USER));
            }
            if (item.groupSet) {
                assertEquals(item.group, inArchive.getProperty(index, PropID.GROUP));
            }
            if (item.creationTimeSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                Date isDate = (Date) inArchive.getProperty(index, PropID.CREATION_TIME);
                assertTrue(Math.abs(item.creationTime.getTime() - isDate.getTime()) <= 2000);
            }
            if (item.lastAccessTimeSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                Date isDate = (Date) inArchive.getProperty(index, PropID.LAST_ACCESS_TIME);
                assertTrue(Math.abs(item.lastAccessTime.getTime() - isDate.getTime()) <= 2000);
            }
            if (item.modificationTimeSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                Date isDate = (Date) inArchive.getProperty(index, PropID.LAST_MODIFICATION_TIME);
                assertTrue(Math.abs(item.modificationTime.getTime() - isDate.getTime()) <= 2000);
            }
            extracted[myIndex] = true;
            testSequentailOutStream = new TestSequentailOutStream(item);
            return testSequentailOutStream;
        }

        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
            assertEquals(ExtractAskMode.EXTRACT, extractAskMode);
        }

        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            assertEquals(ExtractOperationResult.OK, extractOperationResult);
            testSequentailOutStream.finish();
            testSequentailOutStream = null;
        }

        public void setCompleted(long completeValue) throws SevenZipException {
            // TODO Check this value or remove todo
        }

        public void setTotal(long total) throws SevenZipException {
            // TODO Check this value or remove todo
        }

        void finish() throws SevenZipException {
            for (int i = 0; i < extracted.length; i++) {
                assertTrue("Item '" + itemList.get(i).path + "' wasn't extracted", extracted[i]);
            }
        }
    }

    // TODO Use it or remove it
    public interface FilenameGenerator {
        String nextFilename();
    }

    /**
     * Constant seed used here. Tests should be deterministic in order to ensure easy debugging.
     */
    private static final Random random = new Random(0);
    private static final char[] SYMBOLS = new char[] { ' ', '_', '-', '+', '=' };
    private static final char[] SYMBOLS_FIRST_CHAR = new char[] { '_', '-', '+', '=' };

    private List<Item> itemList = new ArrayList<Item>();
    private Map<String, Integer> usedNames = new HashMap<String, Integer>();
    private VirtualContentConfiguration configuration;

    public VirtualContent(VirtualContentConfiguration virtualContentConfiguration) {
        this.configuration = virtualContentConfiguration;
    }

    public void writeToDirectory(File directory) throws Exception {
        directory.mkdirs();
        for (Item item : itemList) {
            File itemFile = new File(directory.getName() + File.separator + item.path).getAbsoluteFile();
            itemFile.getParentFile().mkdirs();
            item.blob.writeToOutputStream(new FileOutputStream(itemFile), true);
        }
    }

    public void print() {
        print(true);
    }

    public void print(boolean sort) {
        List<Item> values;
        if (sort) {
            values = new ArrayList<Item>(itemList);
            Collections.sort(values, new Comparator<Item>() {
                public int compare(Item item1, Item item2) {
                    return item1.path.compareTo(item2.path);
                }
            });
        } else {
            values = itemList;
        }
        for (Item item : values) {
            System.out.println(item.path + "   (" + item.blob.getSize() + " bytes)");
        }
    }

    public void deleteItem(int index) {
        itemList.remove(index);
        reindexUsedNames();
    }

    public void deleteItemByPath(String path) {
        int index = getIndexByPath(path);
        deleteItem(index);
    }

    private int getIndexByPath(String path) {
        int index = -1;
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).path.equals(path)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new RuntimeException("Can't find item: item with path '" + path + "' not found");
        }
        return index;
    }

    public void addItem(int index, String path, byte[] blob) {
        Item item = new Item(blob);
        item.path = path;
        fillWithRandomData(item);
        itemList.add(index, item);
        reindexUsedNames();
    }

    private void fillWithRandomData(Item item) {
        item.creationTime = JUnitNativeTestBase.getDate(3 * WEEK);
        item.modificationTime = JUnitNativeTestBase.getDate(2 * WEEK);
        item.lastAccessTime = JUnitNativeTestBase.getDate(WEEK);
        item.user = getRandomName();
        item.group = getRandomName();
    }


    private void reindexUsedNames() {
        usedNames.clear();
        for (int i = 0; i < itemList.size(); i++) {
            usedNames.put(itemList.get(i).path.toUpperCase(), i);
        }
    }

    public void createOutArchive(IOutCreateArchive<IOutItemCallback> outArchive, ISequentialOutStream outputStream)
            throws SevenZipException {
        outArchive.createArchive(outputStream, itemList.size(), new ArchiveCreateCallback());
    }

    public void verifyInArchive(IInArchive inArchive) throws SevenZipException {
        TextExtractCallback testExtractCallback = new TextExtractCallback(inArchive);
        inArchive.extract(null, false, testExtractCallback);
        testExtractCallback.finish();

    }

    public void fillRandomly(int countOfFiles, int directoriesDepth, int maxSubdirectories, int averageFileLength,
            int deltaFileLength, FilenameGenerator filenameGenerator) {
        itemList.clear();
        List<String> directoryList = getRandomDirectory(directoriesDepth, maxSubdirectories, countOfFiles);
        for (int i = 0; i < countOfFiles; i++) {
            int fileLength = averageFileLength + random.nextInt(deltaFileLength + 1);
            if (configuration.isAllowEmptyFiles() && random.nextInt(3) == 0) {
                fileLength = 0;
            }
            byte[] fileContent = getRandomFileContent(fileLength);

            String directory = directoryList.get(random.nextInt(directoryList.size()));

            Item item = new Item(fileContent);
            fillWithRandomData(item);
            for (int j = 0; j < 50; j++) {
                String filename = filenameGenerator == null ? getRandomFilename() : filenameGenerator.nextFilename();
                if (!usedNames.containsKey((directory + filename).toUpperCase())) {
                    item.path = directory + filename;
                    break;
                }
            }
            if (item.path == null) {
                throw new RuntimeException("It wasn't possible to generate a random file name after 50 iterations.");
            }

            usedNames.put(item.path.toUpperCase(), Integer.valueOf(itemList.size()));
            itemList.add(item);

        }
    }

    private byte[] getRandomFileContent(int length) {
        byte[] content = new byte[length];
        switch (random.nextInt(3)) {
        case 0:
            // Random content
            random.nextBytes(content);
            break;

        case 1:
            // Good compressible content
            for (int i = random.nextInt(256); i < length; i++) {
                content[i] = (byte) i;
            }
            break;

        case 2:
            // Very good compressible content
            byte c = (byte) random.nextInt(256);
            Arrays.fill(content, c);
        }
        return content;
    }

    private List<String> getRandomDirectory(int directoriesDepth, int maxSubdirectories, int maxDirectories) {
        List<String> result = new ArrayList<String>();
        if (!configuration.isForbiddenRootDirectory()) {
            result.add(""); // Root directory
        }
        BigDecimal countOfDirectories;
        if (maxSubdirectories > 1 && directoriesDepth > 1) {
            // count of directories = \[ \frac{maxSubdirectories^{directoriesDepth + 1} - 1}{maxSubdirectories - 1} \]$ (maxSubdirectories > 1)
            countOfDirectories = BigDecimal.valueOf(maxSubdirectories).pow(directoriesDepth + 1)
                    .subtract(BigDecimal.ONE).divide(BigDecimal.valueOf(maxSubdirectories - 1));
        } else {
            countOfDirectories = BigDecimal.valueOf(Math.max(directoriesDepth, maxSubdirectories));
        }

        if (countOfDirectories.compareTo(BigDecimal.valueOf(maxDirectories)) < 0) {
            createFullDirectoryStructure(result, "", 1, directoriesDepth, maxSubdirectories);
        } else {
            throw new IllegalStateException("Not implemented yet: " + countOfDirectories); // TODO
        }

        return result;
    }

    private void createFullDirectoryStructure(List<String> result, String root, int depth, int maxDepth,
            int maxSubdirectories) {
        for (int i = 0; i < maxSubdirectories; i++) {
            String name = null;
            for (int j = 0; j < 50; j++) {
                name = getRandomFilename();
                if (usedNames.containsKey(name)) {
                    name = null;
                } else {
                    break;
                }
            }
            if (name == null) {
                throw new RuntimeException(
                        "It wasn't possible to generate a random directory name after 50 iterations.");
            }
            usedNames.put((root + name).toUpperCase(), null);
            String fullDirectoryName = root + name + "/";
            result.add(fullDirectoryName);
            if (depth < maxDepth) {
                createFullDirectoryStructure(result, fullDirectoryName, depth + 1, maxDepth, maxSubdirectories);
            }
        }
    }

    private static String getRandomFilename() {
        int length;
        switch (random.nextInt(3)) {
        case 0:
            length = 1;
            break;
        case 1:
            // Length: 2-10
            length = 2 + random.nextInt(9);
            break;
        default:
            // Length: 20-30
            length = 20 + random.nextInt(11);
        }
        char[] filenameArray = new char[length];
        for (int j = 0; j < length; j++) {
            filenameArray[j] = getRandomFilenameChar(j == 0);
        }
        return new String(filenameArray);
    }

    private static String getRandomName() {
        int length = 3 + random.nextInt(9);
        char[] filenameArray = new char[length];
        for (int j = 0; j < length; j++) {
            filenameArray[j] = getRandomLetter();
        }
        return new String(filenameArray);
    }

    private static char getRandomFilenameChar(boolean firstChar) {
        switch (random.nextInt()) {
        case 0:
            // Symbols
            if (firstChar) {
                return SYMBOLS_FIRST_CHAR[random.nextInt(SYMBOLS_FIRST_CHAR.length)];
            }
            return SYMBOLS[random.nextInt(SYMBOLS.length)];
        case 1:
            // Digits
            return (char) ('0' + random.nextInt(10));

        default:
            return getRandomLetter();
        }
    }

    private static char getRandomLetter() {
        if (random.nextBoolean()) {
            // Upper case letters
            return (char) ('A' + random.nextInt(26));
        }
        // Lower case letters
        return (char) ('a' + random.nextInt(26));
    }

    public int getItemCount() {
        return itemList.size();
    }

    public ByteArrayStream getItemStream(int index) {
        return itemList.get(index).blob;
    }

    public String getItemPath(int index) {
        return itemList.get(index).path;
    }

    public void updateItemContentByPath(String itemToUpdatePath, byte[] newContent) {
        int index = getIndexByPath(itemToUpdatePath);
        itemList.get(index).blob = new ByteArrayStream(newContent, true);
    }

    public void updateItemPathByPath(String itemToUpdatePath, String newPath) {
        int index = getIndexByPath(itemToUpdatePath);
        itemList.get(index).path = newPath;
        reindexUsedNames();
    }

    public void updateItemLastModificationTimeByPath(String itemToUpdatePath, Date time) {
        int index = getIndexByPath(itemToUpdatePath);
        itemList.get(index).modificationTime = time;
    }

    public void updateUserByPath(String itemToUpdatePath, String newValue) {
        int index = getIndexByPath(itemToUpdatePath);
        itemList.get(index).user = newValue;
    }

    public void updateGroupByPath(String itemToUpdatePath, String newValue) {
        int index = getIndexByPath(itemToUpdatePath);
        itemList.get(index).group = newValue;
    }

}
