private int getNumberOfItemsInArchive(String archiveFile) throws Exception {
    ISevenZipInArchive archive;

    archive = SevenZip.openInArchive(ArchiveFormat.ZIP, // null - autodetect
            new RandomAccessFileInStream(
                    new RandomAccessFile(archiveFile, "r")));

    return archive.getNumberOfItems();
}
