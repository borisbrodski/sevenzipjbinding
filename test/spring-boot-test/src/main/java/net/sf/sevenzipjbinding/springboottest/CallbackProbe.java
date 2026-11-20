package net.sf.sevenzipjbinding.springboottest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Called from every 7-Zip-JBinding callback of {@link ArchiveService}. It does what application code typically does
 * there and records every problem instead of throwing, so one run reports them all:
 * <ul>
 * <li>the context class loader of the thread is the application's class loader,</li>
 * <li>a Spring Boot class and an application class resolve through the context class loader,</li>
 * <li>{@link ServiceLoader} finds {@link CallbackSpi},</li>
 * <li>a Spring bean can be fetched from the application context and called,</li>
 * <li>logging works.</li>
 * </ul>
 * It also counts the callbacks that ran on 7-Zip's own threads: a scenario that is supposed to have them proves
 * nothing without them.
 */
@Component
public class CallbackProbe {
    private static final Logger log = LoggerFactory.getLogger(CallbackProbe.class);
    private static final int MAX_PROBLEMS = 10;

    private final ApplicationContext applicationContext;
    private final ClassLoader expectedLoader = CallbackProbe.class.getClassLoader();
    private final Set<String> problems = new LinkedHashSet<>();
    private final List<String> reports = new ArrayList<>();
    private Thread callingThread;
    private int callbacks;
    private int offThreadCallbacks;

    public CallbackProbe(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Called by {@link ArchiveService} on the thread that calls 7-Zip-JBinding, before the call.
     */
    synchronized void begin() {
        callingThread = Thread.currentThread();
    }

    synchronized void callback(String what) {
        Thread thread = Thread.currentThread();
        boolean offThread = thread != callingThread;
        callbacks++;
        if (offThread) {
            offThreadCallbacks++;
        }
        String where = what + " on " + (offThread ? "7-Zip thread " : "calling thread ") + thread.getName();
        ClassLoader contextClassLoader = thread.getContextClassLoader();
        if (contextClassLoader != expectedLoader) {
            problem(where + ": context class loader " + contextClassLoader + ", expected " + expectedLoader);
            return;
        }
        try {
            Class.forName("org.springframework.boot.SpringApplication", false, contextClassLoader);
            Class.forName(ArchiveService.class.getName(), false, contextClassLoader);
        } catch (ClassNotFoundException e) {
            problem(where + ": " + e);
        }
        Iterator<CallbackSpi> services = ServiceLoader.load(CallbackSpi.class).iterator();
        if (!services.hasNext() || !CallbackSpiImpl.NAME.equals(services.next().name())) {
            problem(where + ": ServiceLoader doesn't find " + CallbackSpiImpl.class.getName());
        }
        String greeting = applicationContext.getBean(CallbackGreeter.class).greet(what);
        log.debug("{}: {}", where, greeting);
    }

    private void problem(String problem) {
        if (problems.size() < MAX_PROBLEMS) {
            problems.add(problem);
        }
    }

    /**
     * Close a scenario: verify it and reset the counters.
     *
     * @param offThreadCallbacksRequired
     *            the scenario is meaningless unless some callbacks ran on 7-Zip's own threads
     * @return a one-line report of the scenario
     * @throws IllegalStateException
     *             with every problem found
     */
    public synchronized String verify(String scenario, boolean offThreadCallbacksRequired) {
        if (offThreadCallbacksRequired && offThreadCallbacks == 0) {
            problem(scenario + ": no callback ran on a 7-Zip thread, the scenario proves nothing");
        }
        String report = scenario + ": " + callbacks + " callbacks, " + offThreadCallbacks + " on 7-Zip threads";
        reports.add(report);
        log.info(report);
        callbacks = 0;
        offThreadCallbacks = 0;
        callingThread = null;
        if (!problems.isEmpty()) {
            String message = scenario + " failed:\n  " + String.join("\n  ", problems);
            problems.clear();
            throw new IllegalStateException(message);
        }
        return report;
    }

    public synchronized List<String> getReports() {
        return new ArrayList<>(reports);
    }
}
