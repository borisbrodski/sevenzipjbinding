public void openArchive(String archiveFilename) 
        throws SevenZipException, FileNotFoundException {

    randomAccessFile = new RandomAccessFile(archiveFilename, "r");

    inArchive = SevenZip.openInArchive(null, // Choose format automatically
            new RandomAccessFileInStream(randomAccessFile));
}
