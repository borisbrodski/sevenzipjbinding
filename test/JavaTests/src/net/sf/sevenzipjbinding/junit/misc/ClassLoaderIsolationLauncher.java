package net.sf.sevenzipjbinding.junit.misc;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * The only class on the child JVM's system class path of {@link ClassLoaderIsolationTest}. Must reference nothing but
 * the JDK: the test classes aren't loadable there. A top-level class on purpose: the macOS Java launcher calls
 * <code>getCanonicalName()</code> on the main class, which resolves the enclosing class of a nested one and fails with
 * <code>NoClassDefFoundError</code> when that class isn't on the class path.
 * <p>
 * <code>ClassLoaderIsolationLauncher &lt;isolated|delegating&gt; &lt;class path&gt; &lt;main class&gt; &lt;args...&gt;</code>:
 * builds a <code>URLClassLoader</code> over the class path with no parent (isolated) or the system class loader as
 * parent (delegating), makes it the context class loader like a container would, and runs the main class through it.
 *
 * @author Boris Brodski
 * @since 26.06-2.4
 */
public class ClassLoaderIsolationLauncher {
    public static final String OK = "LAUNCHER-OK";
    public static final String ISOLATED = "isolated";
    public static final String DELEGATING = "delegating";

    public static void main(String[] args) throws Exception {
        String shape = args[0];
        String[] classPath = args[1].split(File.pathSeparator);
        String mainClass = args[2];

        try {
            Class.forName("net.sf.sevenzipjbinding.SevenZip");
            System.out.println("FAILED: the system class loader sees the binding, the test proves nothing");
            System.exit(2);
        } catch (ClassNotFoundException expected) {
            // The whole point
        }

        URL[] urls = new URL[classPath.length];
        for (int i = 0; i < classPath.length; i++) {
            urls[i] = new File(classPath[i]).toURI().toURL();
        }
        ClassLoader parent = shape.equals(DELEGATING) ? ClassLoader.getSystemClassLoader() : null;
        URLClassLoader loader = new URLClassLoader(urls, parent);
        Thread.currentThread().setContextClassLoader(loader);

        Class<?> clazz = Class.forName(mainClass, true, loader);
        if (clazz.getClassLoader() != loader) {
            System.out.println("FAILED: " + mainClass + " was defined by " + clazz.getClassLoader());
            System.exit(2);
        }
        System.out.println(OK + " " + shape + " loader, parent " + parent);

        Method main = clazz.getMethod("main", String[].class);
        String[] rest = Arrays.copyOfRange(args, 3, args.length);
        main.invoke(null, (Object) rest);
    }
}
