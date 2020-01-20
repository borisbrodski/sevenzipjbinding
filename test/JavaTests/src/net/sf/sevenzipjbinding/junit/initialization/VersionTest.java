package net.sf.sevenzipjbinding.junit.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import org.junit.Test;

import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZip.Version;
import net.sf.sevenzipjbinding.junit.JUnitNativeTestBase;
import net.sf.sevenzipjbinding.junit.VoidContext;

public class VersionTest extends JUnitNativeTestBase<VoidContext> {

    @Test
    public void testSevenZipJBindingVersionSet() throws Exception {
        String version = SevenZip.getSevenZipJBindingVersion();
        assertFalse(version.contains("{"));
        assertFalse(version.contains("}"));
        assertTrue(version.trim().length() > 3);
        String[] parts = version.split("[-.a-z]");
        assertTrue(parts.length > 2);
        for (String part : parts) {
            if (part.length() > 0) {
                assertTrue(part.matches("\\d+"));
            }
        }
        assertEquals(getVersionFromJarMANIFEST(), version);
    }

    @Test
    public void testSevenZipVersion() {
        Version version = SevenZip.getSevenZipVersion();

        log("Testing binding of 7-Zip " + version.version + " (" + version.date + ")");

        assertEquals(0, version.build);
        assertTrue(version.major > 0);
        assertTrue(version.minor > 0);
        assertTrue(version.version.length() > 0);
        assertTrue(version.date.length() > 0);
        assertTrue(version.copyright.length() > 0);
    }

    private String getVersionFromJarMANIFEST() throws Exception {
        Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            Manifest manifest = new Manifest(resources.nextElement().openStream());
            String title = manifest.getMainAttributes().getValue("Implementation-Title");
            if (title != null && title.startsWith("7-Zip-JBinding native lib")) {
                String version = manifest.getMainAttributes().getValue("Implementation-Version");
                if (version != null) {
                    return version;
                }
            }
        }

        fail("No MANIFEST.MF of the 7-Zip-JBinding lib was found");
        return null;
    }
}
