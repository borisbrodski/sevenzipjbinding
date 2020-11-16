package net.sf.sevenzipjbinding.junit.tools;

import static net.sf.sevenzipjbinding.junit.tools.DateTools.WEEK;
import static net.sf.sevenzipjbinding.junit.tools.RandomTools.getRandom;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.IOutItemGZip;
import net.sf.sevenzipjbinding.IOutItemTar;
import net.sf.sevenzipjbinding.IOutItemZip;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.PropID.AttributesBitMask;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
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
 * @since 4.65-1
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

        Integer attributes;
        boolean attributesSet;

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

        String symLink;
        boolean symLinkSet;

        String hardLink;
        boolean hardLinkSet;

        // TODO Add more parameters, that we can test

        Item(byte[] blobData) {
            if (blobData != null) {
                blob = new ByteArrayStream(blobData, false);
            }
        }

        public VirtualContent getVirtualContent() {
            return VirtualContent.this;
        }
    }

    private class ArchiveCreateCallback implements IOutCreateCallback<IOutItemAllFormats> {
        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public void setTotal(long total) throws SevenZipException {

        }

        public void setCompleted(long complete) throws SevenZipException {

        }

        public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory)
                throws SevenZipException {

            IOutItemAllFormats outItem = outItemFactory.createOutItem();

            Item item = itemList.get(index);
            ByteArrayStream byteArrayStream = item.blob;
            if (byteArrayStream != null) {
                byteArrayStream.rewind();
            }

            switch (outItem.getArchiveFormat()) {
            case SEVEN_ZIP:
                IOutItem7z outItem7z = outItem;

                outItem7z.setPropertyLastModificationTime(item.modificationTime);
                item.modificationTimeSet = (item.modificationTime != null);

                outItem7z.setPropertyAttributes(item.attributes);
                item.attributesSet = (item.attributes != null);
                break;

            case ZIP:
                IOutItemZip outItemZip = outItem;

                outItemZip.setPropertyAttributes(item.attributes);
                item.attributesSet = (item.attributes != null);

                outItemZip.setPropertyLastAccessTime(item.lastAccessTime);
                item.lastAccessTimeSet = (item.lastAccessTime != null);

                outItemZip.setPropertyLastModificationTime(item.modificationTime);
                item.modificationTimeSet = (item.modificationTime != null);

                outItemZip.setPropertyCreationTime(item.creationTime);
                item.creationTimeSet = (item.creationTime != null);
                break;

            case GZIP:
                IOutItemGZip outItemGZip = outItem;

                outItemGZip.setPropertyLastModificationTime(item.modificationTime);
                item.modificationTimeSet = (item.modificationTime != null);
                break;

            case BZIP2:
                break;

            case TAR:
                IOutItemTar outItemTar = outItem;

                outItemTar.setPropertyLastModificationTime(item.modificationTime);
                item.modificationTimeSet = (item.modificationTime != null);

                outItemTar.setPropertyUser(item.user);
                item.userSet = (item.user != null);

                outItemTar.setPropertyGroup(item.group);
                item.groupSet = (item.group != null);

                outItemTar.setPropertySymLink(item.symLink);
                item.symLinkSet = (item.symLink != null);

                outItemTar.setPropertyHardLink(item.hardLink);
                item.hardLinkSet = (item.hardLink != null);
                break;
            default:
                throw new RuntimeException("Unknown ArchiveFormat: " + outItem.getArchiveFormat());
            }

            if (item.blob != null) {
                outItem.setDataSize((long) item.blob.getSize());
            }
            outItem.setPropertyPath(item.path);

            return outItem;
        }

        public ISequentialInStream getStream(int index) throws SevenZipException {
            return itemList.get(index).blob;
        }
    }

    private class ArchiveCreateCallbackWithPassword extends ArchiveCreateCallback implements ICryptoGetTextPassword {
        private String password;

        ArchiveCreateCallbackWithPassword(String password) {
            this.password = password;
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
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
                System.out.println("Length: " + data.length);
                for (int i = 0; i < data.length; i++) {
                    if (expectedData[i] != data[i]) {
                        System.out.println("Different at pos " + i);
                        System.out.println("Expect byte: " + expectedData[i]);
                        System.out.println("Actual byte: " + data[i]);
                        break;
                    }
                }
                e.printStackTrace();
                throw e;
            }

            return data.length;
        }

        void finish() throws SevenZipException {
            assertTrue(byteArrayStream.isEOF());
        }
    }

    private class VerifyExtractCallback implements IArchiveExtractCallback {
        private TestSequentailOutStream testSequentailOutStream;
        private final boolean[] extracted;
        private final IInArchive inArchive;

        VerifyExtractCallback(IInArchive inArchive) {
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
                // GZip/BZip2
                myIndex = 0;
            }
            Item item = itemList.get(myIndex);
            if (item.userSet) {
                Object expectedUser = inArchive.getProperty(index, PropID.USER);
                assertEquals(item.user, expectedUser);
            }
            if (item.groupSet) {
                assertEquals(item.group, inArchive.getProperty(index, PropID.GROUP));
            }
            if (item.attributesSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                assertEquals(item.attributes, inArchive.getProperty(index, PropID.ATTRIBUTES));
            }
            if (item.lastAccessTimeSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                Date isDate = (Date) inArchive.getProperty(index, PropID.LAST_ACCESS_TIME);
                assertNotNull(isDate);
                assertTrue(Math.abs(item.lastAccessTime.getTime() - isDate.getTime()) <= 2000);
            }
            if (item.modificationTimeSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                Date isDate = (Date) inArchive.getProperty(index, PropID.LAST_MODIFICATION_TIME);
                assertNotNull(isDate);
                assertTrue(Math.abs(item.modificationTime.getTime() - isDate.getTime()) <= 2000);
            }
            if (item.creationTimeSet && archiveFormat != ArchiveFormat.ZIP) { // TODO Fix this for ZIP
                Date isDate = (Date) inArchive.getProperty(index, PropID.CREATION_TIME);
                assertNotNull(isDate);
                assertTrue(Math.abs(item.creationTime.getTime() - isDate.getTime()) <= 2000);
            }
            if (item.symLinkSet) {
                assertEquals(item.symLink, inArchive.getProperty(index, PropID.SYM_LINK));
            }
            if (item.hardLinkSet) {
                assertEquals(item.hardLink, inArchive.getProperty(index, PropID.HARD_LINK));
            }
            extracted[myIndex] = true;
            if (item.blob == null) {
                return null;
            }
            testSequentailOutStream = new TestSequentailOutStream(item);
            return testSequentailOutStream;
        }

        //        private void assertEqualsArchiveDates(Date actual, Date expected) {
        //            if (actual == null && expected == null) {
        //                return;
        //            }
        //            assertNotNull("Actual date/time is null. Expected: " + expected, actual);
        //            assertNotNull("Actual date/time is " + actual + ". Expected null", expected);
        //
        //            assertTrue("Dates differ. Actual: " + actual + ", expcted: " + expected,
        //                    Math.abs(actual.getTime() - expected.getTime()) <= 2000);
        //        }

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

    private class VerifyExtractCallbackWithPassword extends VerifyExtractCallback implements ICryptoGetTextPassword {
        private String password;

        VerifyExtractCallbackWithPassword(IInArchive inArchive, String password) {
            super(inArchive);
            this.password = password;
        }

        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    // TODO Use it or remove it
    public interface FilenameGenerator {
        String nextFilename();
    }

    /**
     * Constant seed used here. Tests should be deterministic in order to ensure easy debugging.
     */
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

    private void addSymLink(Item item) {
        item.symLink = RandomTools.getRandomName();
        item.symLinkSet = true;
    }

    private void addHardLink(Item item) {
        item.hardLink = RandomTools.getRandomName();
        item.hardLinkSet = true;
    }
    private void fillWithRandomData(Item item) {
        item.creationTime = DateTools.getDate(3 * WEEK);
        item.modificationTime = DateTools.getDate(2 * WEEK);
        item.lastAccessTime = DateTools.getDate(WEEK);
        item.user = RandomTools.getRandomName();
        item.group = RandomTools.getRandomName();
        item.attributes = getRandom().nextInt(17) & ~(AttributesBitMask.FILE_ATTRIBUTE_DIRECTORY);
    }

    private void reindexUsedNames() {
        usedNames.clear();
        for (int i = 0; i < itemList.size(); i++) {
            if (usedNames.put(itemList.get(i).path.toUpperCase(), i) != null) {
                fail("Name used twice: '" + itemList.get(i).path.toUpperCase() + "'");
            }
        }
    }

    public void createOutArchive(IOutCreateArchive<IOutItemAllFormats> outArchive, ISequentialOutStream outputStream)
            throws SevenZipException {
        createOutArchive(outArchive, outputStream, false, null);
    }

    public void createOutArchive(IOutCreateArchive<IOutItemAllFormats> outArchive, ISequentialOutStream outputStream,
            boolean implememntICryptoGetTextPassword, String password) throws SevenZipException {
        ArchiveCreateCallback outCreateCallback;
        if (implememntICryptoGetTextPassword) {
            outCreateCallback = new ArchiveCreateCallbackWithPassword(password);
        } else {
            outCreateCallback = new ArchiveCreateCallback();
        }
        outArchive.createArchive(outputStream, itemList.size(), outCreateCallback);
    }

    public void verifyInArchive(IInArchive inArchive) throws SevenZipException {
        verifyInArchive(inArchive, null);
    }
    public void verifyInArchive(IInArchive inArchive, String password) throws SevenZipException {
        VerifyExtractCallback verifyExtractCallback;
        if (password == null) {
            verifyExtractCallback = new VerifyExtractCallback(inArchive);
        } else {
            verifyExtractCallback = new VerifyExtractCallbackWithPassword(inArchive, password);
        }
        inArchive.extract(null, false, verifyExtractCallback);
        verifyExtractCallback.finish();

    }

    public void fillRandomly(int countOfFiles, int directoriesDepth, int maxSubdirectories, int averageFileLength,
            int deltaFileLength, FilenameGenerator filenameGenerator, boolean addLinks) {
        itemList.clear();
        List<String> directoryList = getRandomDirectory(directoriesDepth, maxSubdirectories, countOfFiles);
        for (int i = 0; i < countOfFiles; i++) {
            int fileLength = averageFileLength + getRandom().nextInt(deltaFileLength + 1);
            if (configuration.isAllowEmptyFiles() && getRandom().nextInt(3) == 0) {
                fileLength = 0;
            }
            byte[] fileContent = getRandomFileContent(fileLength);

            String directory = directoryList.get(getRandom().nextInt(directoryList.size()));

            Item item;
            if (addLinks && getRandom().nextInt(5) == 3) {
                item = new Item(null);
                if (getRandom().nextInt(2) == 0) {
                    addSymLink(item);
                } else {
                    addHardLink(item);
                }
            } else {
                item = new Item(fileContent);
            }
            fillWithRandomData(item);
            for (int j = 0; j < 50; j++) {
                String filename = filenameGenerator == null ? RandomTools.getRandomFilename()
                        : filenameGenerator.nextFilename();
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
        Random random = getRandom();
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
                name = RandomTools.getRandomFilename();
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
            String fullDirectoryName = root + name + File.separator;
            result.add(fullDirectoryName);
            if (depth < maxDepth) {
                createFullDirectoryStructure(result, fullDirectoryName, depth + 1, maxDepth, maxSubdirectories);
            }
        }
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

    public String getItemSymLink(int index) {
        return itemList.get(index).symLink;
    }

    public String getItemHardLink(int index) {
        return itemList.get(index).hardLink;
    }

}
