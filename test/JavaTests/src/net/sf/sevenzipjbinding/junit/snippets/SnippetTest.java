package net.sf.sevenzipjbinding.junit.snippets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class SnippetTest {

	private PrintStream oldSystemOut;
	private TestOutputStream testOutputStream;
	private PrintStream printStream;

	protected void beginSnippetTest() {
		oldSystemOut = System.out;
		testOutputStream = new TestOutputStream();
		printStream = new PrintStream(testOutputStream);
		System.setOut(printStream);
	}

	protected String endSnippetTest() {
		System.setOut(oldSystemOut);
		String result = testOutputStream.getOutputString();
		printStream.close();
		return result;
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
