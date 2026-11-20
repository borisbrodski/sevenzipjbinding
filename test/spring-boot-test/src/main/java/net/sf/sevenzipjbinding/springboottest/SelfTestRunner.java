package net.sf.sevenzipjbinding.springboottest;

import static net.sf.sevenzipjbinding.springboottest.ArchiveService.MB;
import static net.sf.sevenzipjbinding.springboottest.ArchiveService.data;
import static net.sf.sevenzipjbinding.springboottest.ArchiveService.items;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;

/**
 * The scenarios, run when the profile <code>selftest</code> is active (the packaged jar run by run.sh). The last
 * line of the output is {@value #OK} or {@value #FAILED}, the exit code 0 or 1. With the argument
 * <code>--expect-boot-loader</code> the application must be loaded by a class loader other than the system class
 * loader (Spring Boot's launcher), otherwise the run doesn't cover the setup of issue #35.
 * <p>
 * Scenarios with <code>threadCount</code> 2 run callbacks on 7-Zip's own threads whatever the CPU count. 7-Zip's
 * decoder thread count follows the CPU count and can't be set, so extracting the multi-block archive on 7-Zip
 * threads is required only on a machine with several CPUs.
 */
@Component
@Profile("selftest")
public class SelfTestRunner implements CommandLineRunner, ExitCodeGenerator {
    public static final String OK = "SPRING-BOOT-TEST-OK";
    public static final String FAILED = "SPRING-BOOT-TEST-FAILED";

    private static final Logger log = LoggerFactory.getLogger(SelfTestRunner.class);

    private final ArchiveService service;
    private final CallbackProbe probe;
    private int exitCode = 1;

    public SelfTestRunner(ArchiveService service, CallbackProbe probe) {
        this.service = service;
        this.probe = probe;
    }

    @Override
    public void run(String... args) {
        try {
            ClassLoader loader = getClass().getClassLoader();
            log.info("loaded by {}, 7-Zip-JBinding {} ({}), {} CPUs", loader.getClass().getName(),
                    SevenZip.getSevenZipJBindingVersion(), SevenZip.getUsedPlatform(),
                    Runtime.getRuntime().availableProcessors());
            if (Arrays.asList(args).contains("--expect-boot-loader") && loader == ClassLoader.getSystemClassLoader()) {
                throw new IllegalStateException("loaded by the system class loader, not by Spring Boot's launcher");
            }
            runScenarios();
            System.out.println(OK + ": " + String.join("; ", probe.getReports()));
            exitCode = 0;
        } catch (Exception e) {
            log.error("self test failed", e);
            System.out.println(FAILED + ": " + e);
        }
    }

    private void runScenarios() throws Exception {
        boolean severalCpus = Runtime.getRuntime().availableProcessors() > 1;

        Map<String, byte[]> twoItems = items(MB, MB);
        byte[] sevenZip = service.create(ArchiveFormat.SEVEN_ZIP, 2, -1, twoItems);
        probe.verify("create 7z with 2 threads", true);
        service.extract(ArchiveFormat.SEVEN_ZIP, sevenZip, twoItems);
        probe.verify("extract 7z", false);

        Map<String, byte[]> bigItem = items(3 * MB);
        byte[] multiBlock = service.create(ArchiveFormat.SEVEN_ZIP, 2, 1, bigItem);
        probe.verify("create multi-block 7z with 2 threads", true);
        service.extract(ArchiveFormat.SEVEN_ZIP, multiBlock, bigItem);
        probe.verify("extract multi-block 7z", severalCpus);

        Map<String, byte[]> updatedItems = new LinkedHashMap<>(twoItems);
        updatedItems.put("item0.txt", data(MB + 1));
        updatedItems.put("added.txt", data(MB / 2));
        byte[] updated = service.update(ArchiveFormat.SEVEN_ZIP, sevenZip, 2, "item0.txt",
                updatedItems.get("item0.txt"), "added.txt", updatedItems.get("added.txt"));
        probe.verify("update 7z with 2 threads", true);
        service.extract(ArchiveFormat.SEVEN_ZIP, updated, updatedItems);
        probe.verify("extract updated 7z", false);

        byte[] zip = service.create(ArchiveFormat.ZIP, 0, -1, twoItems);
        probe.verify("create zip", false);
        service.extract(ArchiveFormat.ZIP, zip, twoItems);
        probe.verify("extract zip", false);
        byte[] updatedZip = service.update(ArchiveFormat.ZIP, zip, 0, "item0.txt", updatedItems.get("item0.txt"),
                "added.txt", updatedItems.get("added.txt"));
        probe.verify("update zip", false);
        service.extract(ArchiveFormat.ZIP, updatedZip, updatedItems);
        probe.verify("extract updated zip", false);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
