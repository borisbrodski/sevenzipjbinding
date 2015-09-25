package net.sf.sevenzipjbinding.junit.snippets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class SnippetTest {
    public static final String NEW_LINE = System.getProperty("line.separator");

    private PrintStream oldSystemOut;
    private PrintStream oldSystemErr;
    private TestOutputStream testOutputStream;
    private TestOutputStream testOutputStreamErr;
    private PrintStream printStream;
    private PrintStream printStreamErr;
    private String snippetErrOutput;

    protected void beginSnippetTest() {
        oldSystemOut = System.out;
        testOutputStream = new TestOutputStream();
        printStream = new PrintStream(testOutputStream);
        System.setOut(printStream);
    }

    protected void beginSnippetFailingTest() {
        oldSystemOut = System.out;
        oldSystemErr = System.err;
        testOutputStream = new TestOutputStream();
        testOutputStreamErr = new TestOutputStream();
        printStream = new PrintStream(testOutputStream);
        printStreamErr = new PrintStream(testOutputStreamErr);
        System.setOut(printStream);
        System.setErr(printStreamErr);
    }

    protected String endSnippetTest() {
        System.setOut(oldSystemOut);
        if (oldSystemErr != null) {
            System.setErr(oldSystemErr);
        }
        String result = testOutputStream.getOutputString();
        printStream.close();
        if (printStreamErr != null) {
            snippetErrOutput = testOutputStreamErr.getOutputString();
            printStreamErr.close();
        }
        return result;
    }

    public String getSnippetErrOutput() {
        return snippetErrOutput;
    }

    private class TestOutputStream extends OutputStream {
        private final StringBuffer stringBuffer;

        TestOutputStream() {
            this.stringBuffer = new StringBuffer();
        }

        public String getOutputString() {
            return stringBuffer.toString();
        }

        @Override
        public void write(int b) throws IOException {
            stringBuffer.append((char) b);
        }
    }
}
