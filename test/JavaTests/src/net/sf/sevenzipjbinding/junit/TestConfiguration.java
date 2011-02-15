package net.sf.sevenzipjbinding.junit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestConfiguration {

    private static String _defaultParameters[][] = { { "STRESS_TEST", "" }, //
            { "TIMEOUT", "" + (2 * 60 * 1000) }, // 
            { "THREADS", "3", "10" }, //
            { "REPEAT_SINGLE_TEST", "1", "2" }, //
            { "REPEAT_MULTITHREADED_TEST", "3", "8" }, //
    };
    private static Field[] _fields = TestConfiguration.class.getDeclaredFields();

    private static boolean stressTest;
    private static int timeout;
    private static int threads;
    private static int repeatSingleTest;
    private static int repeatMultithreadedTest;

    static {
        try {
            for (String[] defaultParameter : _defaultParameters) {
                String value = System.getProperty(defaultParameter[0]);
                if (value == null) {
                    value = defaultParameter[isStressTest() ? 2 : 1];
                }
                value = value.trim();
                Field field = getField(defaultParameter[0]);
                field.setAccessible(true);
                if (field.getType() == boolean.class) {
                    boolean typedValue = value.length() > 0 && !value.equalsIgnoreCase("f")
                            && !value.equalsIgnoreCase("false") && !value.equalsIgnoreCase("no")
                            && !value.equalsIgnoreCase("off") && !value.equalsIgnoreCase("disable");
                    field.setBoolean(null, typedValue);
                    continue;
                }
                if (field.getType() == int.class) {
                    field.setInt(null, Integer.parseInt(value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    private static Field getField(String parameterName) {
        String name = parameterName.replaceAll("_", "");
        for (Field field : _fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().equalsIgnoreCase(name)) {
                return field;
            }
        }
        throw new RuntimeException("Can't find corresponding static attribute for parameter: '" + parameterName + "'");
    }

    static void printParameter() throws Exception {
        int maxLength = 0;
        for (String[] defaultParameter : _defaultParameters) {
            if (maxLength < defaultParameter[0].length()) {
                maxLength = defaultParameter[0].length();
            }
        }

        System.out.println("== TEST CONFIGURATION ==================================");
        for (String[] defaultParameter : _defaultParameters) {
            Field field = getField(defaultParameter[0]);
            Object value = field.get(null);
            System.out.print(defaultParameter[0]);
            System.out.print(new String(new byte[maxLength - defaultParameter[0].length()]).replace('\0', ' '));
            System.out.print(" = ");
            if (value == null) {
                System.out.println("null");
            } else {
                if (value instanceof String) {
                    System.out.println("\"" + value + '"');
                } else {
                    System.out.println(value);
                }
            }
        }
        System.out.println("========================================================");
    }

    public static boolean isStressTest() {
        return stressTest;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static int getThreads() {
        return threads;
    }

    public static int getRepeatSingleTest() {
        return repeatSingleTest;
    }

    public static int getRepeatMultithreadedTest() {
        return repeatMultithreadedTest;
    }
}
