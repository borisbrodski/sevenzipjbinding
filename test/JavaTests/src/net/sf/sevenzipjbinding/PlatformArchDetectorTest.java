package net.sf.sevenzipjbinding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Pure-Java unit tests for {@link PlatformArchDetector} (the arch autodetection engine behind
 * {@link SevenZip#getPlatformBestMatch()}).
 * <p>
 * This class lives in package {@code net.sf.sevenzipjbinding} on purpose: it exercises the
 * package-private {@link PlatformArchDetector#getArchCandidates(String, List)} directly, without
 * loading the native library. Full end-to-end behaviour on real hardware is covered by the QEMU/VM
 * matrix; the point of these tests is to lock down the <b>deterministic</b>, host-independent logic
 * (the {@code os.arch} normalization and the ARM candidate ordering) so future refactoring can't
 * silently break platform selection.
 * <p>
 * The 32-bit-ARM detection that reads {@code /proc} / {@code uname} is environment dependent, so the
 * ARM assertions below only check invariants that hold on <i>any</i> host (candidate set membership
 * and monotonic level ordering), never a single exact list.
 */
public class PlatformArchDetectorTest {

    private static final List<String> ARM_32 = Arrays.asList("armv7", "armv6", "armv5");

    /**
     * Candidates with the libc suffix normalized away. On a musl host (e.g. the Alpine QEMU VMs) the
     * detector appends "-musl" to every candidate; the arch-identity assertions below only care about
     * the architecture, so strip it here to stay host-independent. The musl behaviour itself is
     * covered by the dedicated musl tests.
     */
    private static List<String> candidates(String osArch) {
        List<String> result = new ArrayList<String>();
        for (String c : rawCandidates(osArch)) {
            result.add(c.endsWith("-musl") ? c.substring(0, c.length() - "-musl".length()) : c);
        }
        return result;
    }

    /** Raw candidates exactly as returned (keeps any "-musl" suffix). */
    private static List<String> rawCandidates(String osArch) {
        List<String> result = PlatformArchDetector.getArchCandidates(osArch, null);
        assertNotNull("getArchCandidates must never return null (os.arch=" + osArch + ")", result);
        return result;
    }

    // ---- os.arch normalization: fully deterministic on every host ------------------------------

    @Test
    public void amd64AliasesNormalizeToAmd64() {
        List<String> expected = Arrays.asList("amd64", "x86_64");
        assertEquals(expected, candidates("amd64"));
        assertEquals(expected, candidates("x86_64"));
        assertEquals(expected, candidates("x64"));
        assertEquals("normalization must be case-insensitive", expected, candidates("X86_64"));
    }

    @Test
    public void aarch64AliasesNormalizeToArm64() {
        List<String> expected = Arrays.asList("arm64", "aarch64");
        assertEquals(expected, candidates("aarch64"));
        assertEquals(expected, candidates("arm64"));
        assertEquals(expected, candidates("AArch64"));
    }

    @Test
    public void x86AliasesNormalizeToI386() {
        List<String> expected = Arrays.asList("i386", "x86");
        assertEquals(expected, candidates("i386"));
        assertEquals(expected, candidates("i486"));
        assertEquals(expected, candidates("i586"));
        assertEquals(expected, candidates("i686"));
        assertEquals(expected, candidates("x86"));
        assertEquals(expected, candidates("ia32"));
    }

    @Test
    public void unknownArchFallsBackToRawValue() {
        assertEquals(Arrays.asList("sparc"), candidates("sparc"));
        assertEquals(Arrays.asList("riscv64"), candidates("riscv64"));
        // leading/trailing whitespace is trimmed and lower-cased
        assertEquals(Arrays.asList("ppc64le"), candidates("  PPC64LE  "));
    }

    @Test
    public void nullOrBlankOsArchIsSafeAndEmpty() {
        assertTrue(candidates(null).isEmpty());
        assertTrue(candidates("").isEmpty());
        assertTrue(candidates("   ").isEmpty());
    }

    // ---- diagnostics ---------------------------------------------------------------------------

    @Test
    public void diagnosticsTrailIsCollected() {
        List<String> diag = new ArrayList<String>();
        PlatformArchDetector.getArchCandidates("amd64", diag);
        assertFalse("a detection trail must be recorded", diag.isEmpty());
        assertTrue("the trail should start by echoing os.arch", diag.get(0).contains("os.arch="));
    }

    // ---- 32-bit ARM: host-independent invariants only ------------------------------------------

    @Test
    public void armLevelEncodedInOsArchYieldsOnlyArmBuildsInDescendingOrder() {
        assertArmCandidates(candidates("armv7l"));
        assertArmCandidates(candidates("armv6l"));
        assertArmCandidates(candidates("armv5tejl"));
    }

    @Test
    public void bareArmNeverReturnsNonArmOrNull() {
        // os.arch=="arm" triggers /proc-based detection; result varies by host but must always be a
        // (possibly empty) list of ARM builds only.
        assertArmSubset(candidates("arm"));
    }

    @Test
    public void higherOsArchLevelOffersAtLeastAsMuchAsLower() {
        // A v7 userspace can run v6 code, so its candidate list must include every build a v6
        // userspace would be offered (same float-ABI detection on this host for both calls).
        List<String> v7 = candidates("armv7l");
        List<String> v6 = candidates("armv6l");
        assertTrue("v7 candidates " + v7 + " must contain all v6 candidates " + v6,
                v7.containsAll(v6));
    }

    // ---- musl / libc detection -----------------------------------------------------------------

    @Test
    public void muslVariantsAppendsMuslSuffix() {
        assertEquals(Arrays.asList("arm64-musl", "aarch64-musl"),
                PlatformArchDetector.muslVariants(Arrays.asList("arm64", "aarch64")));
        assertEquals(Arrays.asList("armv7-musl", "armv6-musl"),
                PlatformArchDetector.muslVariants(Arrays.asList("armv7", "armv6")));
        assertTrue(PlatformArchDetector.muslVariants(new ArrayList<String>()).isEmpty());
    }

    @Test
    public void candidatesAreConsistentlyMuslOrGlibc() {
        // Host-independent invariant: on any host every candidate is either the musl variant or the
        // glibc one, never a mix (a musl userspace can't load a glibc lib or vice-versa). Holds on
        // glibc hosts (0 musl) and musl hosts (all musl).
        for (String osArch : new String[] { "amd64", "aarch64", "arm", "armv7l", "armv6l", "i686" }) {
            List<String> raw = rawCandidates(osArch);
            if (raw.isEmpty()) {
                continue;
            }
            int musl = 0;
            for (String c : raw) {
                if (c.endsWith("-musl")) {
                    musl++;
                }
            }
            assertTrue("candidates must be all-musl or all-glibc, got " + raw, musl == 0 || musl == raw.size());
        }
    }

    // ---- helpers -------------------------------------------------------------------------------

    /** Assert: only ARM builds, no duplicates, and strictly descending architecture level. */
    private static void assertArmCandidates(List<String> result) {
        assertArmSubset(result);
        assertFalse("expected at least one ARM build", result.isEmpty());
        int prevLevel = Integer.MAX_VALUE;
        for (String c : result) {
            int level = c.charAt(4) - '0'; // "armvN"
            assertTrue("candidates must be ordered by descending level: " + result, level < prevLevel);
            prevLevel = level;
        }
    }

    /** Assert every element is one of the known 32-bit ARM builds (never arm64/amd64/i386/...). */
    private static void assertArmSubset(List<String> result) {
        assertNotNull(result);
        for (String c : result) {
            assertTrue("unexpected non-ARM candidate '" + c + "' in " + result, ARM_32.contains(c));
        }
    }
}
