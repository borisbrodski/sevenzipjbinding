package net.sf.sevenzipjbinding.junit;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.sf.sevenzipjbinding.SevenZipException;

public final class TestLogger {
    public static void logWithoutPrefix(String msg, Object... args) {
        if (args.length == 0) {
            logIntern(false, msg);
        } else {
            logIntern(false, String.format(msg, args));
        }
    }

    public static void logWithoutPrefix(Throwable throwable) {
        logIntern(false, throwableToString(throwable));
    }
    public static void log(Throwable throwable) {
        logIntern(true, throwableToString(throwable));
    }

    public static void log(Throwable throwable, String msg, Object... args) {
        log(msg, args);
        log(throwable);
    }

    public static void logWithoutPrefix(Throwable throwable, String msg, Object... args) {
        logWithoutPrefix(msg, args);
        logWithoutPrefix(throwable);
    }
    public static void log(String msg, Object... args) {
        if (args.length == 0) {
            logIntern(true, msg);
        } else {
            logIntern(true, String.format(msg, args));
        }
    }

    private static String throwableToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        if (throwable instanceof SevenZipException) {
            ((SevenZipException) throwable).printStackTraceExtended(new PrintWriter(sw));
        } else {
            throwable.printStackTrace(new PrintWriter(sw));
        }
        String string = sw.toString();
        return string;
    }

    private static void logIntern(boolean withPrefix, String msg) {
        if (!TestConfiguration.getCurrent().isTrace()) {
            return;
        }
        if (withPrefix) {
            doLogWithPrefix(msg);
        } else {
            doLog(msg);
        }
    }

    private synchronized static void doLogWithPrefix(String msg) {
        String prefix = String.format("[%5s] ", Thread.currentThread().getId());
        doLog(prefix + msg.replace("\r", "").replace("\n", "\n" + prefix));
    }

    private static void doLog(String msg) {
        System.out.println(msg);
    }
}
