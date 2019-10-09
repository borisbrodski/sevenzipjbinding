package net.sf.sevenzipjbinding.junit;

import java.util.ArrayList;
import java.util.List;

import net.sf.sevenzipjbinding.junit.junittools.annotations.LongRunning;

/**
 * Defines test run configuration. Use -D[PARAM]=[VALUE].
 *
 * @author Boris Brodski
 * @since 9.20-2.00
 */
public class TestConfiguration {
    /**
     * Name of the "minimal" profile.
     */
    public static String PR_MINUMAL = "MINIMAL";

    /**
     * Name of the default profile.
     */
    public static String PR_DEFAULT = "DEFAULT";

    /**
     * Name of the "stress" profile.
     */
    public static String PR_STRESS = "STRESS";

    /**
     * Use named test profile.
     */
    public static String TEST_PARAM__PROFILE = "TEST_PROFILE";

    /**
     * Count of threads to the used for multithreaded tests.<br>
     * 0 - disables multithreaded tests.
     */
    public static String TEST_PARAM__THREADS_MULTITHREADED_TEST = "TEST_MULTITHREADED_THREADS";

    /**
     * The number of time a single threaded (normal) test should be repeated.<br>
     * 0 - disables single threaded tests.
     */
    public static String TEST_PARAM__REPEAT_SINGLETHREADED_TEST = "TEST_REPEAT_SINGLETHREADED";

    /**
     * The number of time a multithreaded test should be repeated. 0 - disables multithreaded tests.
     */
    public static String TEST_PARAM__REPEAT_MULTITHREADED_TEST = "TEST_REPEAT_MULTITHREADED";

    /**
     * The timeout in seconds for a <i>single</i> run of a test.
     */
    public static String TEST_PARAM__TIMEOUT = "TEST_TIMEOUT";

    /**
     * If <code>true</code> run tests marked as 'long running'.
     *
     * @see LongRunning
     */
    public static String TEST_PARAM__LONG_RUNNING = "TEST_LONG_RUNNING";

    /**
     * If set, trace log output will be written to the corresponding file.
     */
    public static String TEST_PARAM__TRACE_FILE = "TEST_TRACE_FILE";

    /**
     * If enable, print trace output. Default: ON.
     */
    public static String TEST_PARAM__TRACE = "TEST_TRACE";

    /**
     * If enable, configure test suite to run on low memory machine. Default: OFF.
     */
    public static String TEST_PARAM__ON_LOW_MEMORY = "TEST_ON_LOW_MEMORY";

    // @formatter:off
    private static final TestConfiguration[] PROFILES = new TestConfiguration[] { //
        /*                    name       | thread# | repeatSingle | repeatMultiple | timeout | longRun | trace | */
        new TestConfiguration(PR_MINUMAL,         2,             1,               0,      300,   false,   true), //
        new TestConfiguration(PR_DEFAULT,         2,             2,               2,      600,   false,   true), //
        new TestConfiguration(PR_STRESS ,        15,            10,              10,     1200,   true,   false)  //
    };
    // @formatter:off
    private static TestConfiguration currentProfile;

    private String name;
    private int multiThreadedThreads;
    private int repeatSingleThreadedTest;
    private int repeatMultiThreadedTest;
    private int singleTestTimeout;
    private boolean longRunning;
    private boolean trace;
    private String traceFile;
    private boolean onLowMemory;

    TestConfiguration(String name, int multiThreadedThreads, int repeatSingleThreadedTest, int repeatMultiThreadedTest,
            int singleTestTimeout,
            boolean longRunning, boolean trace) {
        this.name = name;
        this.multiThreadedThreads = multiThreadedThreads;
        this.repeatSingleThreadedTest = repeatSingleThreadedTest;
        this.repeatMultiThreadedTest = repeatMultiThreadedTest;
        this.singleTestTimeout = singleTestTimeout;
        this.longRunning = longRunning;
        this.trace = trace;
    }

    void overwriteFromSystemProperties() {
        multiThreadedThreads = getInt(TEST_PARAM__THREADS_MULTITHREADED_TEST, multiThreadedThreads);
        repeatSingleThreadedTest = getInt(TEST_PARAM__REPEAT_SINGLETHREADED_TEST, repeatSingleThreadedTest);
        repeatMultiThreadedTest = getInt(TEST_PARAM__REPEAT_MULTITHREADED_TEST, repeatMultiThreadedTest);
        singleTestTimeout = getInt(TEST_PARAM__TIMEOUT, singleTestTimeout);
        longRunning = getBoolean(TEST_PARAM__LONG_RUNNING, longRunning);
        trace = getBoolean(TEST_PARAM__TRACE, trace);
        traceFile = getString(TEST_PARAM__TRACE, traceFile);
        onLowMemory = getBoolean(TEST_PARAM__ON_LOW_MEMORY, false);
    }

    private static int getInt(String name, int defaultValue) {
        String stringValue = System.getProperty(name.trim());
        if (stringValue != null) {
            try {
                return Integer.valueOf(stringValue.trim());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid integer value for property '" + name + "': '" + stringValue + "'");
            }
        }
        return defaultValue;
    }

    private static boolean getBoolean(String name, boolean defaultValue) {
        String stringValue = System.getProperty(name.trim());
        if (stringValue != null) {
            stringValue = stringValue.trim();
            return !stringValue.equalsIgnoreCase("f") && !stringValue.equalsIgnoreCase("false")
                    && !stringValue.equalsIgnoreCase("no") && !stringValue.equalsIgnoreCase("n")
                    && !stringValue.equalsIgnoreCase("off") && !stringValue.equalsIgnoreCase("disable");
        }
        return defaultValue;
    }

    private static String getString(String name, String defaultValue) {
        String stringValue = System.getProperty(name.trim());
        if (stringValue != null) {
            return stringValue.trim();
        }
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public int getMultiThreadedThreads() {
        return multiThreadedThreads;
    }

    public int getRepeatSingleThreadedTest() {
        return repeatSingleThreadedTest;
    }

    public int getRepeatMultiThreadedTest() {
        return repeatMultiThreadedTest;
    }

    public boolean isMultithreadedEnabled() {
        return repeatMultiThreadedTest > 0 && multiThreadedThreads > 0;
    }

    /**
     * Default timeout for a single threaded test
     *
     * @return timeout in seconds.
     */
    public int getSingleTestTimeout() {
        return singleTestTimeout;
    }

    public boolean isLongRunning() {
        return longRunning;
    }

    public boolean isTrace() {
        return trace;
    }

    public boolean isOnLowMemory() {
        return onLowMemory;
    }

    public static TestConfiguration getCurrent() {
        return currentProfile;
    }

    public void printParameter() {
        class ParamInfo {
            String name;
            String value;

            ParamInfo(String name, int value) {
                this(name, Integer.toString(value));
            }

            ParamInfo(String name, boolean value) {
                this(name, Boolean.toString(value));
            }

            ParamInfo(String name, String value) {
                this.name = name;
                this.value = value;
            }
        }
        List<ParamInfo> params = new ArrayList<ParamInfo>();
        params.add(new ParamInfo(TEST_PARAM__PROFILE, name));
        params.add(new ParamInfo(TEST_PARAM__REPEAT_SINGLETHREADED_TEST, repeatSingleThreadedTest));
        params.add(new ParamInfo(TEST_PARAM__REPEAT_MULTITHREADED_TEST, repeatMultiThreadedTest));
        params.add(new ParamInfo(TEST_PARAM__THREADS_MULTITHREADED_TEST, multiThreadedThreads));
        params.add(new ParamInfo(TEST_PARAM__TIMEOUT, singleTestTimeout + " seconds"));
        params.add(new ParamInfo(TEST_PARAM__LONG_RUNNING, longRunning));
        params.add(new ParamInfo(TEST_PARAM__TRACE, trace));
        params.add(new ParamInfo(TEST_PARAM__ON_LOW_MEMORY, onLowMemory));
        int padding = 0;
        for (ParamInfo paramInfo : params) {
            if (padding < paramInfo.name.length()) {
                padding = paramInfo.name.length();
            }
        }
        System.out.println("== TEST CONFIGURATION ==================================");
        for (ParamInfo paramInfo : params) {
            String paramWithPadding = paramInfo.name;
            while (padding > paramWithPadding.length()) {
                paramWithPadding += new String(new char[padding - paramWithPadding.length()]).replace('\0', ' ');
            }
            System.out.println(paramWithPadding + " : " + paramInfo.value);
        }
        System.out.println("========================================================");
    }

    public synchronized static void init() {
        if (currentProfile == null) {
            String profileName = System.getProperty(TEST_PARAM__PROFILE);
            if (profileName != null) {
                loadProfile(profileName);
            } else {
                loadProfile(PR_DEFAULT);
            }
            currentProfile.overwriteFromSystemProperties();
            currentProfile.printParameter();
        }
    }

    private static void loadProfile(String profileName) {
        for (TestConfiguration profile : PROFILES) {
            if (profile.name.equalsIgnoreCase(profileName)) {
                currentProfile = profile;
                return;
            }
        }
        StringBuilder profileNames = new StringBuilder();
        for (TestConfiguration profile : PROFILES) {
            if (profileNames.length() > 0) {
                profileNames.append(", ");
            }
            profileNames.append(profile.name);
        }
        throw new RuntimeException("Unknown profile '" + profileName + "'. Available profiles: " + profileNames);
    }

}
