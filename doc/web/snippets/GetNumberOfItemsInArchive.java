private int getNumberOfItemsInArchive(String archiveFile) throws Exception {
    ISevenZipInArchive archive;
    RandomAccessFile randomAccessFile;

    randomAccessFile = new RandomAccessFile(archiveFile, "r");

    archive = SevenZip.openInArchive(ArchiveFormat.ZIP, // null - autodetect
            new RandomAccessFileInStream(
                    randomAccessFile));

    int numberOfItems = archive.getNumberOfItems();

    archive.close();
    randomAccessFile.close();

    return numberOfItems;
}
