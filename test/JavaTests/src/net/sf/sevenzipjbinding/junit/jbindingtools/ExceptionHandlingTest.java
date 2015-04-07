package net.sf.sevenzipjbinding.junit.jbindingtools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.junit.DebugModeOnly;
import net.sf.sevenzipjbinding.junit.DebugModeOnlyTestRule;

import org.junit.Rule;
import org.junit.Test;

public abstract class ExceptionHandlingTest extends JBindingToolsTestBase {
    private static final String NO_CUSTOM_MESSSAGE_TEXT = "One or multiple exceptions without specific error message were thrown. See multiple 'caused by' exceptions for more information.";

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

    public static abstract class Width1Depth0 extends Width1 {
        @Override
        protected int getDepth() {
            return 0;
        }
    }

    public static abstract class Width1Depth1 extends Width1 {
        @Override
        protected int getDepth() {
            return 1;
        }
    }

    public static abstract class Width1Depth2 extends Width1 {
        @Override
        protected int getDepth() {
            return 2;
        }
    }

    //    public static abstract class Width1Depth3 extends Width1 {
    //        @Override
    //        protected int getDepth() {
    //            return 3;
    //        }
    //    }

    public static abstract class Width2Depth0 extends Width2 {
        @Override
        protected int getDepth() {
            return 0;
        }
    }

    public static abstract class Width2Depth1 extends Width2 {
        @Override
        protected int getDepth() {
            return 1;
        }
    }

    public static abstract class Width2Depth2 extends Width2 {
        @Override
        protected int getDepth() {
            return 2;
        }
    }

    //    public static abstract class Width2Depth3 extends Width2 {
    //        @Override
    //        protected int getDepth() {
    //            return 3;
    //        }
    //    }

    public static abstract class Width3Depth0 extends Width3 {
        @Override
        protected int getDepth() {
            return 0;
        }
    }

    public static abstract class Width3Depth1 extends Width3 {
        @Override
        protected int getDepth() {
            return 1;
        }
    }

    public static abstract class Width3Depth2 extends Width3 {
        @Override
        protected int getDepth() {
            return 2;
        }
    }

    //    public static abstract class Width3Depth3 extends Width3 {
    //        @Override
    //        protected int getDepth() {
    //            return 3;
    //        }
    //    }

    public static class Width1Depth0MtWidth0 extends Width1Depth0 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    public static class Width1Depth1MtWidth0 extends Width1Depth1 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    public static class Width1Depth2MtWidth0 extends Width1Depth2 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    //    public static class Width1Depth3MtWidth0 extends Width1Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 0;
    //        }
    //    }

    public static class Width2Depth0MtWidth0 extends Width2Depth0 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    public static class Width2Depth1MtWidth0 extends Width2Depth1 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    public static class Width2Depth2MtWidth0 extends Width2Depth2 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    //    public static class Width2Depth3MtWidth0 extends Width2Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 0;
    //        }
    //    }

    public static class Width3Depth0MtWidth0 extends Width3Depth0 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    public static class Width3Depth1MtWidth0 extends Width3Depth1 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    public static class Width3Depth2MtWidth0 extends Width3Depth2 {
        @Override
        protected int getMtWidth() {
            return 0;
        }
    }

    //    public static class Width3Depth3MtWidth0 extends Width3Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 0;
    //        }
    //    }

    public static class Width1Depth0MtWidth1 extends Width1Depth0 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    public static class Width1Depth1MtWidth1 extends Width1Depth1 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    public static class Width1Depth2MtWidth1 extends Width1Depth2 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    //    public static class Width1Depth3MtWidth1 extends Width1Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 1;
    //        }
    //    }

    public static class Width2Depth0MtWidth1 extends Width2Depth0 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    public static class Width2Depth1MtWidth1 extends Width2Depth1 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    public static class Width2Depth2MtWidth1 extends Width2Depth2 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    //    public static class Width2Depth3MtWidth1 extends Width2Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 1;
    //        }
    //    }

    public static class Width3Depth0MtWidth1 extends Width3Depth0 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    public static class Width3Depth1MtWidth1 extends Width3Depth1 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    public static class Width3Depth2MtWidth1 extends Width3Depth2 {
        @Override
        protected int getMtWidth() {
            return 1;
        }
    }

    //    public static class Width3Depth3MtWidth1 extends Width3Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 1;
    //        }
    //    }

    public static class Width1Depth0MtWidth2 extends Width1Depth0 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    public static class Width1Depth1MtWidth2 extends Width1Depth1 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    public static class Width1Depth2MtWidth2 extends Width1Depth2 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    //    public static class Width1Depth3MtWidth2 extends Width1Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 2;
    //        }
    //    }

    public static class Width2Depth0MtWidth2 extends Width2Depth0 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    public static class Width2Depth1MtWidth2 extends Width2Depth1 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    public static class Width2Depth2MtWidth2 extends Width2Depth2 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    //    public static class Width2Depth3MtWidth2 extends Width2Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 2;
    //        }
    //    }

    public static class Width3Depth0MtWidth2 extends Width3Depth0 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    public static class Width3Depth1MtWidth2 extends Width3Depth1 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    public static class Width3Depth2MtWidth2 extends Width3Depth2 {
        @Override
        protected int getMtWidth() {
            return 2;
        }
    }

    //    public static class Width3Depth3MtWidth2 extends Width3Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 2;
    //        }
    //    }

    public static class Width1Depth0MtWidth3 extends Width1Depth0 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    public static class Width1Depth1MtWidth3 extends Width1Depth1 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    public static class Width1Depth2MtWidth3 extends Width1Depth2 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    //    public static class Width1Depth3MtWidth3 extends Width1Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 3;
    //        }
    //    }

    public static class Width2Depth0MtWidth3 extends Width2Depth0 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    public static class Width2Depth1MtWidth3 extends Width2Depth1 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    public static class Width2Depth2MtWidth3 extends Width2Depth2 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    //    public static class Width2Depth3MtWidth3 extends Width2Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 3;
    //        }
    //    }

    public static class Width3Depth0MtWidth3 extends Width3Depth0 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    public static class Width3Depth1MtWidth3 extends Width3Depth1 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    public static class Width3Depth2MtWidth3 extends Width3Depth2 {
        @Override
        protected int getMtWidth() {
            return 3;
        }
    }

    //    public static class Width3Depth3MtWidth3 extends Width3Depth3 {
    //        @Override
    //        protected int getMtWidth() {
    //            return 2;
    //        }
    //    }

    @Rule
    public DebugModeOnlyTestRule debugModeOnlyTestRule = new DebugModeOnlyTestRule();

    private static native String callRecursiveCallbackMethod(String path, int depth, int width, int mtwidth,
            boolean useException, boolean customErrorMessage) throws SevenZipException;

    protected abstract int getWidth();

    protected abstract int getDepth();

    protected abstract int getMtWidth();

    static String recursiveCallbackMethod(String path, int depth, int width, int mtWidth, boolean useException,
            boolean customErrorMessage, int widthIndex, int mtWidthIndex) throws SevenZipException {
        String pathElement = "[" + depth + ":" + widthIndex + ":" + mtWidthIndex + "]";
        String newPath = path + pathElement;
        if (depth <= 0) {
            if (useException) {
                throw new RuntimeException("EXCEPTION: path=" + newPath);
            }
            return pathElement;
        }
        return pathElement
                + callRecursiveCallbackMethod(newPath, depth - 1, width, mtWidth, useException, customErrorMessage);
    }

    private static String testRecCPP(int depth, int width, int mtwidth) {
        StringBuilder stringBuilder = new StringBuilder();
        if (width > 1) {
            stringBuilder.append("(");
        }
        for (int i = 0; i < width; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(testRecJava(depth, width, mtwidth, i, -1));
        }
        if (width > 1) {
            stringBuilder.append(")");
        }
        for (int i = 0; i < mtwidth; i++) {
            stringBuilder.append(testRecJava(depth, width, mtwidth, -1, i));
        }
        return stringBuilder.toString();
    }

    private static String testRecJava(int depth, int width, int mtWidth, int widthIndex, int mtWidthIndex) {

        String pathElement = "[" + depth + ":" + widthIndex + ":" + mtWidthIndex + "]";

        if (depth <= 0) {
            return pathElement;
        }

        return pathElement + testRecCPP(depth - 1, width, mtWidth);
    }

    @Test
    @DebugModeOnly
    public void testCallRecursive() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            testCallRecursiveCallbackMethod("", getDepth(), getWidth(), getMtWidth(), false, false);

        }
    }

    @Test
    @DebugModeOnly
    public void testCallRecursiveMultithreaded() throws Exception {
        int threadCount = THREAD_COUNT;
        // TODO verify/optimize
        if (getWidth() + 10 * getMtWidth() + getDepth() >= 32) {
            threadCount = 5;
        }
        if (getWidth() + 10 * getMtWidth() + getDepth() >= 34) {
            threadCount = 2;
        }
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursive();
            }
        }, null, threadCount, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testCallRecursiveWithException() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            testCallRecursiveCallbackMethod("", getDepth(), getWidth(), getMtWidth(), true, false);
        }
    }

    @Test
    @DebugModeOnly
    public void testCallRecursiveWithExceptionMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWithException();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    @Test
    @DebugModeOnly
    public void testCallRecursiveWithExceptionWithCustomMessage() throws Exception {
        for (int i = 0; i < TEST_REPEAT_COUNT; i++) {
            testCallRecursiveCallbackMethod("", getDepth(), getWidth(), getMtWidth(), true, true);
        }
    }

    @Test
    @DebugModeOnly
    public void testCallRecursiveWithExceptionWithCustomMessageMultithreaded() throws Exception {
        runMultithreaded(new RunnableThrowsException() {
            public void run() throws Exception {
                testCallRecursiveWithExceptionWithCustomMessage();
            }
        }, null, THREAD_COUNT, THREAD_TIMEOUT);
    }

    private void testCallRecursiveCallbackMethod(String string, int depth, int width, int mtWidth,
            boolean useException, boolean customErrorMessage) {
        try {
            String expected = null;
            if (!useException) {
                expected = testRecCPP(getDepth(), getWidth(), getMtWidth());
            }

            String result = callRecursiveCallbackMethod("", getDepth(), getWidth(), getMtWidth(), useException,
                    customErrorMessage);
            if (useException) {
                fail("No exception occurred");
            } else {
                assertEquals(expected, result);
            }
        } catch (Throwable throwable) {
            assertTrue(useException);
            checkException(throwable);
            // throwable.printStackTrace();
            if (getWidth() == 1 && getMtWidth() == 0 && !customErrorMessage) {
                assertFalse(throwable instanceof SevenZipException);
                assertNull(throwable.getCause());
                checkPathInSingleException(throwable);
            } else {
                assertTrue(throwable instanceof SevenZipException);
                checkPathInException(throwable, 0, customErrorMessage);
            }
        }
    }

    private void checkPathInSingleException(Throwable throwable) {
        assertNull(throwable.getCause());
        assertEquals(Integer.valueOf(1), Integer.valueOf(getWidth()));
        assertEquals(Integer.valueOf(0), Integer.valueOf(getMtWidth()));
        List<int[]> path = parsePath(throwable.getMessage());
        assertEquals(Integer.valueOf(getDepth() + 1), Integer.valueOf(path.size()));
        for (int i = 0; i < path.size(); i++) {
            int[] pathElement = path.get(i);
            assertEquals(Integer.valueOf(getDepth() - i), Integer.valueOf(pathElement[0]));
            assertEquals(Integer.valueOf(0), Integer.valueOf(pathElement[1]));
            assertEquals(Integer.valueOf(-1), Integer.valueOf(pathElement[2]));
        }
    }

    private List<int[]> checkPathInException(Throwable throwable, int level, boolean customErrorMessage) {
        List<int[]> path = null;
        Throwable firstCause = throwable.getCause();
        Throwable lastCause = null;
        Throwable firstPotentialCause = null;
        Throwable lastPotentialCause = null;
        if (throwable instanceof SevenZipException) {
            SevenZipException sevenZipException = (SevenZipException) throwable;
            lastCause = sevenZipException.getCauseLastThrown();
            firstPotentialCause = sevenZipException.getCauseFirstPotentialThrown();
            lastPotentialCause = sevenZipException.getCauseLastPotentialThrown();

            if (customErrorMessage) {
                assertEquals("Error: depth=" + (getDepth() - level) + ", width=" + getWidth(), //
                        sevenZipException.getSevenZipExceptionMessage());
            } else {
                assertEquals(NO_CUSTOM_MESSSAGE_TEXT, sevenZipException.getSevenZipExceptionMessage());
            }
        }

        if (firstCause != null) {
            int[] firstCausePathElement = null;
            int[] lastCausePathElement = null;
            int[] firstPotentialCausePathElement = null;
            int[] lastPotentialCausePathElement = null;

            List<int[]> firstCausePath = checkPathInException(firstCause, level + 1, customErrorMessage);
            firstCausePathElement = firstCausePath.get(level);
            path = checkPathAndPutToResult(path, firstCausePath);

            if (lastCause != null) {
                List<int[]> lastCausePath = checkPathInException(lastCause, level + 1, customErrorMessage);
                lastCausePathElement = lastCausePath.get(level);
                path = checkPathAndPutToResult(path, lastCausePath);
            }
            if (firstPotentialCause != null) {
                List<int[]> firstPotentialCausePath = checkPathInException(firstPotentialCause, level + 1,
                        customErrorMessage);
                firstPotentialCausePathElement = firstPotentialCausePath.get(level);
                path = checkPathAndPutToResult(path, firstPotentialCausePath);
            }
            if (lastPotentialCause != null) {
                List<int[]> lastPotentialCausePath = checkPathInException(lastPotentialCause, level + 1,
                        customErrorMessage);
                lastPotentialCausePathElement = lastPotentialCausePath.get(level);
                path = checkPathAndPutToResult(path, lastPotentialCausePath);
            }
            if (getWidth() == 0) {
                assertNull(firstCausePathElement);
                assertNull(lastCausePathElement);
            } else if (getWidth() == 1) {
                assertNotNull(firstCausePathElement);
                assertNull(lastCausePathElement);
                assertEquals(Integer.valueOf(0), Integer.valueOf(firstCausePathElement[1]));
                assertEquals(Integer.valueOf(-1), Integer.valueOf(firstCausePathElement[2]));
            } else if (getWidth() > 1) {
                assertNotNull(firstCausePathElement);
                assertNotNull(lastCausePathElement);
                assertEquals(Integer.valueOf(0), Integer.valueOf(firstCausePathElement[1]));
                assertEquals(Integer.valueOf(-1), Integer.valueOf(firstCausePathElement[2]));
                assertEquals(Integer.valueOf(getWidth() - 1), Integer.valueOf(lastCausePathElement[1]));
                assertEquals(Integer.valueOf(-1), Integer.valueOf(lastCausePathElement[2]));
            }

            if (getMtWidth() == 0) {
                assertNull(firstPotentialCausePathElement);
                assertNull(lastPotentialCausePathElement);
            } else if (getMtWidth() == 1) {
                assertNotNull(firstPotentialCausePathElement);
                assertNull(lastPotentialCausePathElement);
                assertEquals(Integer.valueOf(-1), Integer.valueOf(firstPotentialCausePathElement[1]));
                assertEquals(Integer.valueOf(0), Integer.valueOf(firstPotentialCausePathElement[2]));
            } else if (getMtWidth() > 1) {
                assertNotNull(firstPotentialCausePathElement);
                assertNotNull(lastPotentialCausePathElement);
                assertEquals(Integer.valueOf(-1), Integer.valueOf(firstPotentialCausePathElement[1]));
                assertTrue(firstPotentialCausePathElement[2] >= 0);
                assertTrue(firstPotentialCausePathElement[2] < getMtWidth());
                assertEquals(Integer.valueOf(-1), Integer.valueOf(lastPotentialCausePathElement[1]));
                assertTrue(lastPotentialCausePathElement[2] >= 0);
                assertTrue(lastPotentialCausePathElement[2] < getMtWidth());
                assertTrue(firstPotentialCausePathElement[2] != lastPotentialCausePathElement[2]);
            }
        } else {
            path = parsePath(throwable.getMessage());
        }

        assertNotNull(path);

        assertEquals(Integer.valueOf(level), Integer.valueOf(path.size()));
        for (int i = 0; i < path.size(); i++) {
            assertEquals(Integer.valueOf(getDepth() - i), Integer.valueOf(path.get(i)[0]));
        }
        return path;
    }

    private List<int[]> checkPathAndPutToResult(List<int[]> path, List<int[]> causePath) {
        assertTrue(causePath.size() > 0);
        causePath.remove(causePath.size() - 1);
        if (path == null) {
            return causePath;
        }

        assertEquals(Integer.valueOf(path.size()), Integer.valueOf(causePath.size()));
        for (int i = 0; i < path.size(); i++) {
            assertArrayEquals(path.get(i), causePath.get(i));
        }

        return path;
    }

    private List<int[]> parsePath(String path) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[([-0-9]+)\\:([-0-9]+)\\:([-0-9]+)\\]");
        Matcher matcher = pattern.matcher(path);
        List<int[]> result = new ArrayList<int[]>();
        while (matcher.find()) {
            int depth = Integer.parseInt(matcher.group(1));
            int width = Integer.parseInt(matcher.group(2));
            int widthMt = Integer.parseInt(matcher.group(3));
            result.add(new int[] { depth, width, widthMt });
        }

        return result;
    }

    static void checkSevenZipException(SevenZipException sevenZipException) {
        if (sevenZipException.getCauseLastThrown() != null) {
            assertNotNull(sevenZipException.getCause());
        }
        if (sevenZipException.getCauseLastPotentialThrown() != null) {
            assertNotNull(sevenZipException.getCauseFirstPotentialThrown());
        }

        sevenZipException.getMessage();
        sevenZipException.printStackTraceExtended(new PrintWriter(new StringWriter()));

        if (sevenZipException.getCause() != null) {
            if (sevenZipException.getCauseLastThrown() != null) {
                assertTrue(sevenZipException.getCause() != sevenZipException.getCauseLastThrown());
            }
            if (sevenZipException.getCauseFirstPotentialThrown() != null) {
                assertTrue(sevenZipException.getCause() != sevenZipException.getCauseFirstPotentialThrown());
            }
            if (sevenZipException.getCauseLastPotentialThrown() != null) {
                assertTrue(sevenZipException.getCause() != sevenZipException.getCauseLastPotentialThrown());
            }
        }
        if (sevenZipException.getCauseLastThrown() != null) {
            if (sevenZipException.getCauseFirstPotentialThrown() != null) {
                assertTrue(sevenZipException.getCauseLastThrown() != sevenZipException.getCauseFirstPotentialThrown());
            }
            if (sevenZipException.getCauseLastPotentialThrown() != null) {
                assertTrue(sevenZipException.getCauseLastThrown() != sevenZipException.getCauseLastPotentialThrown());
            }
        }
        if (sevenZipException.getCauseFirstPotentialThrown() != null) {
            assertTrue(sevenZipException.getCauseFirstPotentialThrown() != sevenZipException
                    .getCauseLastPotentialThrown());
        }

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
