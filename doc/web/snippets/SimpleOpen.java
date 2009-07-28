public void openArchive(String archiveFilename) 
        throws SevenZipException, FileNotFoundException {

    /*f*/randomAccessFile/* */= new RandomAccessFile(archiveFilename, "r");

    /*f*/inArchive/* */= SevenZip.openInArchive(null, // Choose format automatically
            new RandomAccessFileInStream(randomAccessFile));
}
