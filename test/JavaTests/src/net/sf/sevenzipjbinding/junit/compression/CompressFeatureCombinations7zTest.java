package net.sf.sevenzipjbinding.junit.compression;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Test;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ICryptoGetTextPassword;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutFeatureSetEncryptHeader;
import net.sf.sevenzipjbinding.IOutFeatureSetLevel;
import net.sf.sevenzipjbinding.IOutFeatureSetMultithreading;
import net.sf.sevenzipjbinding.IOutFeatureSetSolid;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.IOutUpdateArchive7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.TestConfiguration;
import net.sf.sevenzipjbinding.junit.VoidContext;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Multithreaded;
import net.sf.sevenzipjbinding.junit.junittools.annotations.Repeat;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

/**
 * All 7z output features set together - compression level, header encryption, thread count, solid, solid files, solid
 * size, solid extension - with and without a password, on create and on update. 7-Zip resets all output properties
 * on every <code>ISetProperties::SetProperties()</code> call, so each feature must survive the others (up to
 * 26.03-2.3 only the last applied one did, see PR #93).<br>
 * <br>
 * Every feature is read back from the archive, not trusted from the call:
 * <ul>
 * <li>header encryption - the archive can't be opened without the password
 * <li>level 0 - the items are stored ("Copy"), other levels use LZMA2
 * <li>solid, solid files, solid size, solid extension - the files in each block, the block count and the solid flag,
 * predicted with the block packing rules of 7-Zip (<code>7zUpdate.cpp</code>)
 * <li>levels 1-9 and the thread count - the archive size, compared between archives that differ in this feature only
 * </ul>
 * The random tests check {@link TestConfiguration#TEST_PARAM__RANDOM_ARCHIVES} &times; 5 combinations each; raise it
 * for a soak run, e.g. <code>-DTEST_RANDOM_ARCHIVES=100000</code>.
 *
 * @author Boris Brodski
 * @since 26.03-2.3
 */
public class CompressFeatureCombinations7zTest extends JUnitNativeTestBase<VoidContext> {
    private static final long SEED = 0x7A93L;
    private static final int COMBINATIONS_PER_RANDOM_ARCHIVE = 5;
    private static final String[] WORDS = { "alpha ", "beta ", "gamma ", "delta ", "epsilon ", "zeta ", "eta ",
            "theta ", "\n" };

    /**
     * The 7z output features of one archive. <code>null</code> - not set (7-Zip default).
     */
    static class Features {
        Integer level;
        Boolean headerEncryption;
        Integer threadCount;
        Boolean solid;
        Integer solidFiles;
        Long solidSize;
        Boolean solidExtension;
        String password;
        boolean decoys;

        Features level(int level) {
            this.level = level;
            return this;
        }

        Features headerEncryption(boolean headerEncryption) {
            this.headerEncryption = headerEncryption;
            return this;
        }

        Features threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        Features solid(boolean solid) {
            this.solid = solid;
            return this;
        }

        Features solidFiles(int solidFiles) {
            this.solidFiles = solidFiles;
            return this;
        }

        Features solidSize(long solidSize) {
            this.solidSize = solidSize;
            return this;
        }

        Features solidExtension(boolean solidExtension) {
            this.solidExtension = solidExtension;
            return this;
        }

        Features password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Set other values first and the final ones last, to check that only the last call of a setter counts.
         */
        Features decoys() {
            this.decoys = true;
            return this;
        }

        Features copy() {
            Features copy = new Features();
            copy.level = level;
            copy.headerEncryption = headerEncryption;
            copy.threadCount = threadCount;
            copy.solid = solid;
            copy.solidFiles = solidFiles;
            copy.solidSize = solidSize;
            copy.solidExtension = solidExtension;
            copy.password = password;
            copy.decoys = decoys;
            return copy;
        }

        boolean isCopyMethod() {
            return level != null && level.intValue() == 0;
        }

        boolean isSolid() {
            return solid == null || solid.booleanValue();
        }

        boolean isHeaderEncrypted() {
            return password != null && Boolean.TRUE.equals(headerEncryption);
        }

        static Features random(Random random) {
            Features features = new Features();
            if (random.nextInt(4) != 0) {
                features.level = pick(random, 0, 1, 3, 5, 7, 9);
            }
            if (random.nextBoolean()) {
                features.headerEncryption = random.nextBoolean();
            }
            if (random.nextBoolean()) {
                features.threadCount = pick(random, 0, 1, 2, 3, 7, 64);
            }
            if (random.nextBoolean()) {
                features.solid = random.nextInt(3) != 0;
            }
            if (random.nextBoolean()) {
                features.solidFiles = pick(random, 0, 1, 2, 3, 5, 1000);
            }
            if (random.nextBoolean()) {
                features.solidSize = Long.valueOf(pick(random, 0, 1, 1000, 2500, 6000, 100000));
            }
            if (random.nextBoolean()) {
                features.solidExtension = random.nextBoolean();
            }
            if (random.nextBoolean()) {
                features.password = random.nextBoolean() ? "secret" : "Päß-密码 !";
            }
            features.decoys = random.nextBoolean();
            return features;
        }

        @Override
        public String toString() {
            return "level=" + level + ", headerEncryption=" + headerEncryption + ", threadCount=" + threadCount
                    + ", solid=" + solid + ", solidFiles=" + solidFiles + ", solidSize=" + solidSize
                    + ", solidExtension=" + solidExtension + ", password=" + password + ", decoys=" + decoys;
        }
    }

    /**
     * Calls the setters of the features in a random order. With {@link Features#decoys()} each setter is called with
     * other values first; a feature, that isn't set, then gets reset to its default value.
     *
     * @return the calls made, for the failure messages
     */
    static <A extends IOutFeatureSetLevel & IOutFeatureSetEncryptHeader & IOutFeatureSetMultithreading //
    & IOutFeatureSetSolid> String applyFeatures(final A archive, final Features features, Random random)
            throws SevenZipException {
        abstract class Setter {
            final List<Object> values = new ArrayList<Object>();

            abstract void set(Object value) throws SevenZipException;

            abstract String name();

            void plan(Object finalValue, Object decoy, Object reset) {
                if (features.decoys) {
                    values.add(decoy);
                }
                if (finalValue != null) {
                    values.add(finalValue);
                } else if (features.decoys && reset != null) {
                    values.add(reset);
                }
            }
        }
        List<Setter> setters = new ArrayList<Setter>();
        setters.add(new Setter() {
            {
                plan(features.level, Integer.valueOf(features.isCopyMethod() ? 9 : 0), Integer.valueOf(-1));
            }

            void set(Object value) throws SevenZipException {
                archive.setLevel(((Integer) value).intValue());
            }

            String name() {
                return "setLevel";
            }
        });
        setters.add(new Setter() {
            {
                // Header encryption can't be unset: 'false' is the default
                plan(features.headerEncryption, Boolean.valueOf(!Boolean.TRUE.equals(features.headerEncryption)),
                        Boolean.FALSE);
            }

            void set(Object value) throws SevenZipException {
                archive.setHeaderEncryption(((Boolean) value).booleanValue());
            }

            String name() {
                return "setHeaderEncryption";
            }
        });
        setters.add(new Setter() {
            {
                plan(features.threadCount, Integer.valueOf(5), Integer.valueOf(-1));
            }

            void set(Object value) throws SevenZipException {
                archive.setThreadCount(((Integer) value).intValue());
            }

            String name() {
                return "setThreadCount";
            }
        });
        setters.add(new Setter() {
            {
                // 'true' is the default
                plan(features.solid, Boolean.valueOf(!features.isSolid()), Boolean.TRUE);
            }

            void set(Object value) throws SevenZipException {
                archive.setSolid(((Boolean) value).booleanValue());
            }

            String name() {
                return "setSolid";
            }
        });
        setters.add(new Setter() {
            {
                plan(features.solidFiles, Integer.valueOf(4), Integer.valueOf(-1));
            }

            void set(Object value) throws SevenZipException {
                archive.setSolidFiles(((Integer) value).intValue());
            }

            String name() {
                return "setSolidFiles";
            }
        });
        setters.add(new Setter() {
            {
                plan(features.solidSize, Long.valueOf(3), Long.valueOf(-1));
            }

            void set(Object value) throws SevenZipException {
                archive.setSolidSize(((Long) value).longValue());
            }

            String name() {
                return "setSolidSize";
            }
        });
        setters.add(new Setter() {
            {
                // 'false' is the default
                plan(features.solidExtension, Boolean.valueOf(!Boolean.TRUE.equals(features.solidExtension)),
                        Boolean.FALSE);
            }

            void set(Object value) throws SevenZipException {
                archive.setSolidExtension(((Boolean) value).booleanValue());
            }

            String name() {
                return "setSolidExtension";
            }
        });

        // Interleave the setters randomly, keeping the order of the calls of each setter
        StringBuilder calls = new StringBuilder();
        List<Setter> pending = new ArrayList<Setter>();
        for (Setter setter : setters) {
            if (!setter.values.isEmpty()) {
                pending.add(setter);
            }
        }
        while (!pending.isEmpty()) {
            Setter setter = pending.get(random.nextInt(pending.size()));
            Object value = setter.values.remove(0);
            setter.set(value);
            calls.append(setter.name()).append('(').append(value).append(") ");
            if (setter.values.isEmpty()) {
                pending.remove(setter);
            }
        }
        return calls.toString().trim();
    }

    static class Item {
        final String path;
        final byte[] content;

        Item(String path, byte[] content) {
            this.path = path;
            this.content = content;
        }

        String getExtension() {
            int slash = path.lastIndexOf('/');
            int dot = path.lastIndexOf('.');
            return dot <= slash ? "" : path.substring(dot + 1);
        }
    }

    static byte[] text(Random random, int length) {
        StringBuilder stringBuilder = new StringBuilder(length + 10);
        while (stringBuilder.length() < length) {
            stringBuilder.append(WORDS[random.nextInt(WORDS.length)]);
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) stringBuilder.charAt(i);
        }
        return bytes;
    }

    /**
     * 1-10 text files with tricky extensions for the 'solid extension' feature: different case, none, a dot in the
     * directory name only, a double extension. Some files are empty (they don't go into blocks). 7-Zip packs the files
     * sorted by name: the names start with the index, to sort the same with and without case sensitivity.
     */
    static List<Item> randomItems(Random random, String prefix) {
        String[] paths = { "a.txt", "b.TXT", "c.xml", "d.csv", "noext", "v1.2/readme", "e.tar.gz", "dir/f.txt" };
        int count = 1 + random.nextInt(10);
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < count; i++) {
            int length = random.nextInt(8) == 0 ? 0 : 1 + random.nextInt(4000);
            items.add(new Item(prefix + "/" + i + "-" + paths[random.nextInt(paths.length)], text(random, length)));
        }
        return items;
    }

    static int pick(Random random, int... values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Predicts the 7z blocks (folders) for the new items: the packing loop of 7-Zip in <code>7zUpdate.cpp</code>, over
     * the non-empty files sorted by name.
     *
     * @return the paths of the files in each block
     */
    static List<List<String>> expectedBlocks(List<Item> items, Features features) {
        long maxFiles = Long.MAX_VALUE;
        long maxBytes = features.isCopyMethod() ? 0 : Long.MAX_VALUE; // Copy method: not solid by default
        boolean byExtension = false;
        if (!features.isSolid()) {
            maxFiles = 1; // setSolid(false) overrides the other solid features
        } else {
            if (features.solidFiles != null) {
                maxFiles = Math.max(1, features.solidFiles.intValue());
            }
            if (features.solidSize != null) {
                maxBytes = features.solidSize.longValue();
            }
            byExtension = Boolean.TRUE.equals(features.solidExtension);
        }
        List<Item> files = new ArrayList<Item>();
        for (Item item : items) {
            if (item.content.length > 0) {
                files.add(item);
            }
        }
        Collections.sort(files, new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return item1.path.compareTo(item2.path);
            }
        });
        List<List<String>> blocks = new ArrayList<List<String>>();
        for (int i = 0; i < files.size();) {
            long totalSize = 0;
            int numSubFiles;
            String extension = null;
            for (numSubFiles = 0; i + numSubFiles < files.size() && numSubFiles < maxFiles; numSubFiles++) {
                Item item = files.get(i + numSubFiles);
                totalSize += item.content.length;
                if (totalSize > maxBytes) {
                    break;
                }
                if (byExtension) {
                    if (numSubFiles == 0) {
                        extension = item.getExtension();
                    } else if (!item.getExtension().equalsIgnoreCase(extension)) {
                        break;
                    }
                }
            }
            numSubFiles = Math.max(1, numSubFiles);
            List<String> block = new ArrayList<String>();
            for (int j = i; j < i + numSubFiles; j++) {
                block.add(files.get(j).path);
            }
            blocks.add(block);
            i += numSubFiles;
        }
        return blocks;
    }

    private static class CreateCallback implements IOutCreateCallback<IOutItem7z> {
        private final List<Item> items;

        CreateCallback(List<Item> items) {
            this.items = items;
        }

        public void setTotal(long total) {
        }

        public void setCompleted(long complete) {
        }

        public void setOperationResult(boolean operationResultOk) {
            assertTrue(operationResultOk);
        }

        public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
                throws SevenZipException {
            IOutItem7z outItem = outItemFactory.createOutItem();
            outItem.setPropertyPath(items.get(index).path);
            outItem.setDataSize(Long.valueOf(items.get(index).content.length));
            return outItem;
        }

        public ISequentialInStream getStream(int index) {
            return new ByteArrayStream(items.get(index).content, false);
        }
    }

    private static class CreateCallbackWithPassword extends CreateCallback implements ICryptoGetTextPassword {
        private final String password;

        CreateCallbackWithPassword(List<Item> items, String password) {
            super(items);
            this.password = password;
        }

        public String cryptoGetTextPassword() {
            return password;
        }
    }

    /**
     * Update: keeps the old items with the given indices unchanged and adds new items. The old items must sit in the
     * blocks of their own (old archive not solid), so that the update copies their blocks unchanged.
     */
    private static class UpdateCallback extends CreateCallback {
        private final List<Integer> oldIndices; // null - new item

        UpdateCallback(List<Item> items, List<Integer> oldIndices) {
            super(items);
            this.oldIndices = oldIndices;
        }

        @Override
        public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
                throws SevenZipException {
            if (oldIndices.get(index) != null) {
                return outItemFactory.createOutItem(oldIndices.get(index).intValue());
            }
            return super.getItemInformation(index, outItemFactory);
        }

        @Override
        public ISequentialInStream getStream(int index) {
            assertEquals(null, oldIndices.get(index));
            return super.getStream(index);
        }
    }

    private static class UpdateCallbackWithPassword extends UpdateCallback implements ICryptoGetTextPassword {
        private final String password;

        UpdateCallbackWithPassword(List<Item> items, List<Integer> oldIndices, String password) {
            super(items, oldIndices);
            this.password = password;
        }

        public String cryptoGetTextPassword() {
            return password;
        }
    }

    private byte[] create(List<Item> items, Features features, Random random, StringBuilder calls)
            throws SevenZipException {
        ByteArrayStream outputStream = new ByteArrayStream(1024 * 1024);
        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        calls.append(applyFeatures(outArchive, features, random));
        outArchive.createArchive(outputStream, items.size(), features.password == null ? new CreateCallback(items)
                : new CreateCallbackWithPassword(items, features.password));
        return toBytes(outputStream);
    }

    private static List<String> sorted(List<List<String>> blocks) {
        List<String> result = new ArrayList<String>();
        for (List<String> block : blocks) {
            List<String> sortedBlock = new ArrayList<String>(block);
            Collections.sort(sortedBlock);
            result.add(sortedBlock.toString());
        }
        Collections.sort(result);
        return result;
    }

    private static byte[] toBytes(ByteArrayStream byteArrayStream) {
        return Arrays.copyOf(byteArrayStream.getBytes(), byteArrayStream.getSize());
    }

    /**
     * The result of {@link #update(List, List, Features, Random, StringBuilder)}.
     */
    private static class Update {
        byte[] archive;
        List<Item> items = new ArrayList<Item>();
        List<Item> newItems = new ArrayList<Item>();
        List<Boolean> isNew = new ArrayList<Boolean>();
        int keptBlocks;
    }

    /**
     * Creates a non-solid archive with the old items (no features, no password), then updates it with the features:
     * keeps a random part of the old items, drops the rest and adds the new items in between.
     */
    private Update update(List<Item> oldItems, List<Item> newItems, Features features, Random random,
            StringBuilder calls) throws SevenZipException {
        ByteArrayStream oldArchiveStream = new ByteArrayStream(1024 * 1024);
        IOutCreateArchive7z oldOutArchive = closeLater(SevenZip.openOutArchive7z());
        oldOutArchive.setSolid(false);
        oldOutArchive.createArchive(oldArchiveStream, oldItems.size(), new CreateCallback(oldItems));
        oldArchiveStream.rewind();
        IInArchive oldInArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, oldArchiveStream));
        assertEquals(oldItems.size(), oldInArchive.getNumberOfItems());
        Map<String, Integer> oldArchiveIndexByPath = new HashMap<String, Integer>();
        for (int i = 0; i < oldItems.size(); i++) {
            String path = ((String) oldInArchive.getProperty(i, PropID.PATH)).replace('\\', '/');
            oldArchiveIndexByPath.put(path, Integer.valueOf(i));
        }

        Update update = new Update();
        List<Integer> oldIndices = new ArrayList<Integer>();
        int nextNewItem = 0;
        for (int oldIndex = 0; oldIndex < oldItems.size() || nextNewItem < newItems.size();) {
            boolean takeNew = oldIndex >= oldItems.size()
                    || (nextNewItem < newItems.size() && random.nextBoolean());
            if (takeNew) {
                Item item = newItems.get(nextNewItem++);
                update.items.add(item);
                update.newItems.add(item);
                update.isNew.add(Boolean.TRUE);
                oldIndices.add(null);
            } else {
                if (random.nextBoolean()) {
                    Item item = oldItems.get(oldIndex);
                    update.items.add(item);
                    update.isNew.add(Boolean.FALSE);
                    oldIndices.add(oldArchiveIndexByPath.get(item.path));
                    if (item.content.length > 0) {
                        update.keptBlocks++;
                    }
                }
                oldIndex++;
            }
        }

        IOutUpdateArchive7z outArchive = oldInArchive.getConnectedOutArchive7z();
        calls.append(applyFeatures(outArchive, features, random));
        ByteArrayStream outputStream = new ByteArrayStream(1024 * 1024);
        outArchive.updateItems(outputStream, update.items.size(), features.password == null
                ? new UpdateCallback(update.items, oldIndices)
                : new UpdateCallbackWithPassword(update.items, oldIndices, features.password));
        update.archive = toBytes(outputStream);
        return update;
    }

    /**
     * Checks every feature, that can be read back from the archive, and the content.
     *
     * @param isNew <code>null</code> - all items are new, else: <code>false</code> - item kept unchanged from the old
     *            archive (old block, no password)
     */
    private void verify(String where, byte[] archive, List<Item> items, List<Boolean> isNew,
            List<List<String>> blocks, int keptBlocks, Features features) throws SevenZipException {
        IInArchive inArchive;
        try {
            inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new ByteArrayStream(archive, false));
        } catch (SevenZipException e) {
            assertTrue("The archive (header not encrypted) can't be opened without password: " + e.getMessage()
                    + ". " + where, features.isHeaderEncrypted());
            inArchive = null;
        }
        if (inArchive != null) {
            inArchive.close();
            assertFalse("The archive header isn't encrypted. " + where, features.isHeaderEncrypted());
        }

        ByteArrayStream archiveStream = new ByteArrayStream(archive, false);
        if (features.password == null) {
            inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, archiveStream));
        } else {
            inArchive = closeLater(SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, archiveStream, features.password));
        }
        assertEquals(where, items.size(), inArchive.getNumberOfItems());

        // 7-Zip stores the items in its own order: find them by path
        Map<String, Integer> itemByPath = new HashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            itemByPath.put(items.get(i).path, Integer.valueOf(i));
        }
        Map<Integer, List<String>> actualBlockMap = new TreeMap<Integer, List<String>>();
        int[] archiveIndices = new int[items.size()];
        for (int archiveIndex = 0; archiveIndex < items.size(); archiveIndex++) {
            String path = ((String) inArchive.getProperty(archiveIndex, PropID.PATH)).replace('\\', '/');
            Integer index = itemByPath.remove(path);
            assertTrue("Unexpected item '" + path + "'. " + where, index != null);
            archiveIndices[index.intValue()] = archiveIndex;
            Integer block = (Integer) inArchive.getProperty(archiveIndex, PropID.BLOCK);
            if (block != null && (isNew == null || isNew.get(index.intValue()).booleanValue())) {
                if (!actualBlockMap.containsKey(block)) {
                    actualBlockMap.put(block, new ArrayList<String>());
                }
                actualBlockMap.get(block).add(path);
            }
        }
        List<List<String>> actualBlocks = new ArrayList<List<String>>(actualBlockMap.values());
        assertEquals("Files in the new blocks. " + where, sorted(blocks), sorted(actualBlocks));

        boolean expectedSolid = false;
        for (List<String> block : blocks) {
            expectedSolid |= block.size() > 1;
        }
        assertEquals("Block count; kept blocks: " + keptBlocks + ". " + where,
                Integer.valueOf(keptBlocks + blocks.size()), inArchive.getArchiveProperty(PropID.NUM_BLOCKS));
        assertEquals("Solid. " + where, Boolean.valueOf(expectedSolid), inArchive.getArchiveProperty(PropID.SOLID));

        for (int index = 0; index < items.size(); index++) {
            Item item = items.get(index);
            int i = archiveIndices[index];
            String whereItem = "Item " + index + " '" + item.path + "'. " + where;
            if (item.content.length == 0) {
                continue;
            }
            boolean newItem = isNew == null || isNew.get(index).booleanValue();
            String method = (String) inArchive.getProperty(i, PropID.METHOD);
            if (newItem && features.isCopyMethod()) {
                assertTrue("Level 0: stored, method: " + method + ". " + whereItem, method.startsWith("Copy"));
            } else {
                assertTrue("Compressed, method: " + method + ". " + whereItem, method.startsWith("LZMA2"));
            }
            boolean encrypted = newItem && features.password != null;
            assertEquals("Method: " + method + ". " + whereItem, Boolean.valueOf(encrypted),
                    Boolean.valueOf(method.contains("7zAES")));
            assertEquals(whereItem, Boolean.valueOf(encrypted), inArchive.getProperty(i, PropID.ENCRYPTED));

            ByteArrayStream content = new ByteArrayStream(item.content.length + 1);
            ExtractOperationResult result = features.password == null ? inArchive.extractSlow(i, content)
                    : inArchive.extractSlow(i, content, features.password);
            assertEquals(whereItem, ExtractOperationResult.OK, result);
            assertArrayEquals(whereItem, item.content, toBytes(content));
        }
    }

    private void checkCreate(String name, List<Item> items, Features features, Random random) throws Exception {
        StringBuilder calls = new StringBuilder();
        byte[] archive = create(items, features, random, calls);
        String where = name + " (create): " + features + "; calls: " + calls;
        verify(where, archive, items, null, expectedBlocks(items, features), 0, features);
    }

    private void checkUpdate(String name, List<Item> oldItems, List<Item> newItems, Features features, Random random)
            throws Exception {
        StringBuilder calls = new StringBuilder();
        Update update = update(oldItems, newItems, features, random, calls);
        String where = name + " (update): " + features + "; calls: " + calls;
        verify(where, update.archive, update.items, update.isNew, expectedBlocks(update.newItems, features),
                update.keptBlocks, features);
    }

    private static int getCombinationCount() {
        TestConfiguration.init();
        return TestConfiguration.getCurrent().getRandomArchives() * COMBINATIONS_PER_RANDOM_ARCHIVE;
    }

    @Test
    public void randomCombinationsOnCreate() throws Exception {
        int combinations = getCombinationCount();
        for (int i = 1; i <= combinations; i++) {
            Random random = new Random(SEED + i);
            checkCreate("Random combination " + i + " of " + combinations, randomItems(random, "f"),
                    Features.random(random), random);
        }
    }

    @Test
    public void randomCombinationsOnUpdate() throws Exception {
        int combinations = getCombinationCount();
        for (int i = 1; i <= combinations; i++) {
            Random random = new Random(-SEED - i);
            checkUpdate("Random combination " + i + " of " + combinations, randomItems(random, "old"),
                    randomItems(random, "new"), Features.random(random), random);
        }
    }

    /**
     * Hand-picked combinations: every feature together, features overriding each other, extreme values.
     */
    static Features[] weirdCombinations() {
        return new Features[] { //
                // Everything at once
                new Features().level(9).headerEncryption(true).threadCount(2).solid(true).solidFiles(3)
                        .solidSize(5000).solidExtension(true).password("secret"), //
                new Features().level(0).headerEncryption(true).threadCount(0).solidFiles(2).solidSize(100000)
                        .solidExtension(true).password("secret").decoys(), //
                // setSolid(false) overrides the other solid features
                new Features().level(1).headerEncryption(true).threadCount(64).solid(false).solidFiles(1000)
                        .solidSize(100000).solidExtension(true).password("secret"), //
                // Level 0 (Copy) is not solid by default, but an explicit solid size makes it solid
                new Features().level(0).solidSize(100000).threadCount(1), //
                new Features().level(0).solidFiles(3).solidExtension(true).password("secret"), //
                // Header encryption without a password does nothing
                new Features().level(7).headerEncryption(true).threadCount(3).solidFiles(2), //
                // Explicit 'off' values
                new Features().level(5).headerEncryption(false).threadCount(1).solid(true).solidExtension(false)
                        .password("secret").decoys(), //
                // Single feature next to the password only
                new Features().solidExtension(true).password("secret"), //
                new Features().solidSize(0).password("secret"), //
                new Features().solidFiles(0).headerEncryption(true).password("secret"), //
                new Features().threadCount(2).headerEncryption(true).password("secret"), //
                new Features().level(0).headerEncryption(true).password("secret"), //
                // Nothing set, only decoys reset to the defaults
                new Features().decoys(), //
                new Features().password("secret").decoys(), //
        };
    }

    @Test
    @Multithreaded
    @Repeat
    public void weirdCombinationsOnCreate() throws Exception {
        Features[] combinations = weirdCombinations();
        for (int i = 0; i < combinations.length; i++) {
            Random random = new Random(SEED * 31 + i);
            checkCreate("Weird combination " + i, weirdItems(random, "f"), combinations[i], random);
        }
    }

    @Test
    @Multithreaded
    @Repeat
    public void weirdCombinationsOnUpdate() throws Exception {
        Features[] combinations = weirdCombinations();
        for (int i = 0; i < combinations.length; i++) {
            Random random = new Random(SEED * 37 + i);
            checkUpdate("Weird combination " + i, weirdItems(random, "old"), weirdItems(random, "new"),
                    combinations[i], random);
        }
    }

    /**
     * 9 files with 1500-2500 bytes, the extensions changing in runs (txt, TXT, xml, none), one empty file. In name
     * order, like 7-Zip packs them.
     */
    static List<Item> weirdItems(Random random, String prefix) {
        String[] paths = { "a.txt", "b.TXT", "c.txt", "d.xml", "empty.txt", "e.xml", "noext", "v1.2/noext", "f.txt",
                "g.xml" };
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < paths.length; i++) {
            int length = paths[i].startsWith("empty") ? 0 : 1500 + random.nextInt(1000);
            items.add(new Item(prefix + "/" + i + "-" + paths[i], text(random, length)));
        }
        return items;
    }

    /**
     * A lost level would leave the default level 5. With all the other features set, level 0 must store the files
     * (checked by {@link #verify}) and the fast levels 1-4 must give archives of other size, than level 5. The same
     * level must give the same size.
     */
    @Test
    public void levelTogetherWithAllOtherFeatures() throws Exception {
        List<Item> items = weirdItems(new Random(SEED), "f");
        for (boolean update : new boolean[] { false, true }) {
            int[] sizes = new int[10];
            for (int level = 0; level <= 9; level++) {
                Features features = new Features().level(level).headerEncryption(true).threadCount(2)
                        .solidFiles(1000).solidSize(1000000).solidExtension(false).password("secret");
                sizes[level] = buildAndVerify(items, features, update, new Random(level));
            }
            String where = (update ? "update" : "create") + ", sizes by level: " + Arrays.toString(sizes);
            for (int level = 1; level <= 4; level++) {
                assertNotEquals("Level " + level + ", " + where, sizes[level], sizes[5]);
            }
            Features level5 = new Features().level(5).headerEncryption(true).threadCount(2).solidFiles(1000)
                    .solidSize(1000000).solidExtension(false).password("secret").decoys();
            assertEquals(where, sizes[5], buildAndVerify(items, level5, update, new Random(SEED)));
        }
    }

    /**
     * LZMA2 (levels 1-2) splits a big solid block into 1 MB chunks if it has more than one thread, which changes the
     * archive size: 1 thread vs. 2 threads must give different archives, 2 vs. 3 threads - the same, with all the other
     * features set. Thread count 0 (count of processors) must match 1 thread on a single processor, 2 threads
     * otherwise. A lost thread count would leave the default (count of processors) for all of them.
     */
    @Test
    public void threadCountTogetherWithAllOtherFeatures() throws Exception {
        Random random = new Random(SEED);
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 3; i++) {
            items.add(new Item("big" + i + ".txt", text(random, 1000 * 1000)));
        }
        for (boolean update : new boolean[] { false, true }) {
            int[] sizes = new int[4];
            for (int threadCount = 0; threadCount <= 3; threadCount++) {
                Features features = new Features().level(1).headerEncryption(true).threadCount(threadCount)
                        .solid(true).solidFiles(10).solidSize(100 * 1000 * 1000).solidExtension(true)
                        .password("secret");
                sizes[threadCount] = buildAndVerify(items, features, update, new Random(threadCount));
            }
            String where = (update ? "update" : "create") + ", sizes by thread count: " + Arrays.toString(sizes);
            assertNotEquals(where, sizes[1], sizes[2]);
            assertEquals(where, sizes[2], sizes[3]);
            int processors = Runtime.getRuntime().availableProcessors();
            assertEquals("Thread count 0, processors: " + processors + ", " + where, sizes[processors == 1 ? 1 : 2],
                    sizes[0]);
        }
    }

    private int buildAndVerify(List<Item> items, Features features, boolean update, Random random)
            throws Exception {
        StringBuilder calls = new StringBuilder();
        if (update) {
            Update result = update(new ArrayList<Item>(), items, features, random, calls);
            verify("update: " + features + "; calls: " + calls, result.archive, result.items, null,
                    expectedBlocks(items, features), 0, features);
            return result.archive.length;
        }
        byte[] archive = create(items, features, random, calls);
        verify("create: " + features + "; calls: " + calls, archive, items, null, expectedBlocks(items, features), 0,
                features);
        return archive.length;
    }

    /**
     * 7-Zip rejects an invalid solid spec: the error names all properties sent together.
     */
    @Test
    public void invalidSettingIsReportedWithAllProperties() throws Exception {
        List<Item> items = weirdItems(new Random(SEED), "f");
        IOutCreateArchive7z outArchive = closeLater(SevenZip.openOutArchive7z());
        outArchive.setLevel(3);
        outArchive.setHeaderEncryption(true);
        outArchive.setThreadCount(2);
        outArchive.setSolidFiles(-5); // "-5F": not a number for 7-Zip
        try {
            outArchive.createArchive(new ByteArrayStream(1024 * 1024), items.size(),
                    new CreateCallbackWithPassword(items, "secret"));
            fail("Invalid solid spec accepted");
        } catch (SevenZipException e) {
            String message = e.getMessage();
            assertTrue(message, message.contains("Error setting archive properties: X=3 HE=on MT=2 S=-5F"));
        }
    }
}
