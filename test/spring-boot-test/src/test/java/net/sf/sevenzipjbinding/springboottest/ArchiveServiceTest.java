package net.sf.sevenzipjbinding.springboottest;

import static net.sf.sevenzipjbinding.springboottest.ArchiveService.MB;
import static net.sf.sevenzipjbinding.springboottest.ArchiveService.data;
import static net.sf.sevenzipjbinding.springboottest.ArchiveService.items;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.sf.sevenzipjbinding.ArchiveFormat;

/**
 * The scenarios of {@link SelfTestRunner} as Spring Boot tests. Here the classes sit on the plain class path of
 * Gradle's test worker; the packaged jar run by run.sh is the one that goes through Spring Boot's class loader.
 */
@SpringBootTest
class ArchiveServiceTest {
    @Autowired
    private ArchiveService service;
    @Autowired
    private CallbackProbe probe;

    @Test
    void createAndExtract7zWithTwoThreads() throws Exception {
        Map<String, byte[]> items = items(MB, MB);
        byte[] archive = service.create(ArchiveFormat.SEVEN_ZIP, 2, -1, items);
        probe.verify("create 7z with 2 threads", true);
        service.extract(ArchiveFormat.SEVEN_ZIP, archive, items);
        probe.verify("extract 7z", false);
    }

    @Test
    void createAndExtractMultiBlock7z() throws Exception {
        Map<String, byte[]> items = items(3 * MB);
        byte[] archive = service.create(ArchiveFormat.SEVEN_ZIP, 2, 1, items);
        probe.verify("create multi-block 7z with 2 threads", true);
        service.extract(ArchiveFormat.SEVEN_ZIP, archive, items);
        probe.verify("extract multi-block 7z", Runtime.getRuntime().availableProcessors() > 1);
    }

    @Test
    void update7zWithTwoThreads() throws Exception {
        Map<String, byte[]> items = items(MB, MB);
        byte[] archive = service.create(ArchiveFormat.SEVEN_ZIP, 2, -1, items);
        probe.verify("create 7z with 2 threads", true);
        Map<String, byte[]> updatedItems = updated(items);
        byte[] updated = service.update(ArchiveFormat.SEVEN_ZIP, archive, 2, "item0.txt",
                updatedItems.get("item0.txt"), "added.txt", updatedItems.get("added.txt"));
        probe.verify("update 7z with 2 threads", true);
        service.extract(ArchiveFormat.SEVEN_ZIP, updated, updatedItems);
        probe.verify("extract updated 7z", false);
    }

    @Test
    void createUpdateAndExtractZip() throws Exception {
        Map<String, byte[]> items = items(MB, MB);
        byte[] archive = service.create(ArchiveFormat.ZIP, 0, -1, items);
        probe.verify("create zip", false);
        service.extract(ArchiveFormat.ZIP, archive, items);
        probe.verify("extract zip", false);
        Map<String, byte[]> updatedItems = updated(items);
        byte[] updated = service.update(ArchiveFormat.ZIP, archive, 0, "item0.txt", updatedItems.get("item0.txt"),
                "added.txt", updatedItems.get("added.txt"));
        probe.verify("update zip", false);
        service.extract(ArchiveFormat.ZIP, updated, updatedItems);
        probe.verify("extract updated zip", false);
    }

    /**
     * The items after the update: item0.txt replaced, added.txt appended.
     */
    private static Map<String, byte[]> updated(Map<String, byte[]> items) {
        Map<String, byte[]> updatedItems = new LinkedHashMap<>(items);
        updatedItems.put("item0.txt", data(MB + 1));
        updatedItems.put("added.txt", data(MB / 2));
        return updatedItems;
    }
}
