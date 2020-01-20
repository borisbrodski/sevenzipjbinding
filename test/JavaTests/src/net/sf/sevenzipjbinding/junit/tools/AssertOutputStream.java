package net.sf.sevenzipjbinding.junit.tools;

import org.junit.Assert;

import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class AssertOutputStream implements ISequentialOutStream {

    private final IInStream inStream;

    public AssertOutputStream(IInStream inStream) {
        this.inStream = inStream;
    }

    public int write(byte[] data) throws SevenZipException {
        byte[] expected = new byte[data.length];
        Assert.assertEquals("Extracted data exceeds expected.", data.length, inStream.read(expected));
        Assert.assertArrayEquals("Extracted data doesn't match expected", expected, data);
        return data.length;
    }

    public boolean readEnitireStream() throws SevenZipException {
        byte[] data = new byte[1];
        int size = inStream.read(data);
        return size == 0;
    }
}