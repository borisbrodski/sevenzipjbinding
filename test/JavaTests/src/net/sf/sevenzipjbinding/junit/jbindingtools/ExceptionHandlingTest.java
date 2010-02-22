package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.sevenzipjbinding.SevenZipException;

import org.junit.Test;

public abstract class ExceptionHandlingTest extends JBindingToolsTestBase {

    public static abstract class Width1 extends ExceptionHandlingTest {
        @Override
        protected int getWidth() {
            return 1;
        }
    }

    public static abstract class Width2 extends ExceptionHandlingTest {
        @Override
        protected int getWidth() {
            return 2;
        }
    }

    public static abstract class Width3 extends ExceptionHandlingTest {
        @Override
        protected int getWidth() {
            return 3;
        }
    }

    public static class Width1Depth0 extends Width1 {
        @Override
        protected int getDepth() {
            return 0;
        }
    }

    public static class Width1Depth1 extends Width1 {
        @Override
        protected int getDepth() {
            return 1;
        }
    }

    public static class Width1Depth2 extends Width1 {
        @Override
        protected int getDepth() {
            return 2;
        }
    }

    public static class Width1Depth3 extends Width1 {
        @Override
        protected int getDepth() {
            return 3;
        }
    }

    public static class Width2Depth0 extends Width2 {
        @Override
        protected int getDepth() {
            return 0;
        }
    }

    public static class Width2Depth1 extends Width2 {
        @Override
        protected int getDepth() {
            return 1;
        }
    }

    public static class Width2Depth2 extends Width2 {
        @Override
        protected int getDepth() {
            return 2;
        }
    }

    public static class Width2Depth3 extends Width2 {
        @Override
        protected int getDepth() {
            return 3;
        }
    }

    public static class Width3Depth0 extends Width3 {
        @Override
        protected int getDepth() {
            return 0;
        }
    }

    public static class Width3Depth1 extends Width3 {
        @Override
        protected int getDepth() {
            return 1;
        }
    }

    public static class Width3Depth2 extends Width3 {
        @Override
        protected int getDepth() {
            return 2;
        }
    }

    public static class Width3Depth3 extends Width3 {
        @Override
        protected int getDepth() {
            return 3;
        }
    }

    private static native String callRecursiveCallbackMethod(int depth, int width, boolean useException)
            throws SevenZipException;

    protected abstract int getWidth();

    protected abstract int getDepth();

    static String recursiveCallbackMethod(int depth, int width, boolean useException, int widthIndex)
            throws SevenZipException {
        if (depth < 0) {
            if (useException) {
                throw new RuntimeException("EXCEPTION: i=" + depth + ":" + widthIndex);
            }
            return "i=" + depth + ":" + widthIndex;
        }
        return callRecursiveCallbackMethod(depth - 1, width, useException) + "[i=" + depth + ":" + widthIndex + "]";
    }

    private static void testRec(StringBuilder stringBuilder, int depth, int width) {
        if (width > 1) {
            stringBuilder.append("(");
        }
        for (int i = 0; i < width; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            testRec(stringBuilder, depth, width, i);
        }
        if (width > 1) {
            stringBuilder.append(")");
        }
    }

    private static void testRec(StringBuilder stringBuilder, int depth, int width, int widthIndex) {
        if (depth < 0) {
            stringBuilder.append("i=" + depth + ":" + widthIndex);
            return;
        }

        if (width > 1) {
            stringBuilder.append("(");
        }
        for (int i = 0; i < width; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            testRec(stringBuilder, depth - 1, width, i);
        }
        if (width > 1) {
            stringBuilder.append(")");
        }
        stringBuilder.append("[i=" + depth + ":" + widthIndex + "]");
    }

    @Test
    public void testCallRecursive() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            testRec(stringBuilder, getDepth(), getWidth());
            //            System.out.println("Expected: " + stringBuilder);
            //            System.out.println("Is:       " + callRecursiveCallbackMethod(getDepth(), getWidth(), false));
            assertEquals(stringBuilder.toString(), callRecursiveCallbackMethod(getDepth(), getWidth(), false));
        }
    }

    @Test
    public void testCallRecursiveMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursive();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    public void testCallRecursiveWithException() throws Exception {
        //        for (int i = 0; i < TEST_REPEAT_COUNT; i++) 
        {
            try {
                callRecursiveCallbackMethod(getDepth(), getWidth(), true);
                fail("No exception occurred");
            } catch (RuntimeException runtimeException) {
                checkException(runtimeException);
                assertTrue(getWidth() == 1);
                //assertEquals("EXCEPTION: i=-1:0", runtimeException.getMessage());
                System.out.println(runtimeException.getLocalizedMessage());
            } catch (SevenZipException sevenZipException) {
                checkException(sevenZipException);
                assertTrue(getWidth() > 1);
                System.out.println(">" + sevenZipException.getLocalizedMessage() + "<");
            }
        }
    }

    @Test
    public void testCallRecursiveWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    static void checkSevenZipException(SevenZipException sevenZipException) {
        if (sevenZipException.getCauseLastThrown() != null) {
            assertNotNull(sevenZipException.getCause());
        }
        if (sevenZipException.getCauseLastPotentialThrown() != null) {
            assertNotNull(sevenZipException.getCauseFirstPotentialThrown());
        }

        sevenZipException.getMessage();

        checkException(sevenZipException.getCause());
        checkException(sevenZipException.getCauseLastThrown());
        checkException(sevenZipException.getCauseFirstPotentialThrown());
        checkException(sevenZipException.getCauseLastPotentialThrown());
    }

    static void checkException(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SevenZipException) {
                checkSevenZipException((SevenZipException) cause);
                return;
            }
            cause = cause.getCause();
        }
    }

}
