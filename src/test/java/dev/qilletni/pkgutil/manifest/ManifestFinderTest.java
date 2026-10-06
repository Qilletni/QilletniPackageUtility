package dev.qilletni.pkgutil.manifest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ManifestFinder} resolves its paths relative to the JVM's working directory (optionally under
 * a "qilletni-src" subdirectory of it), so these tests create/remove real files there rather than mocking.
 */
class ManifestFinderTest {

    private static final Path MANIFEST = Paths.get("qilletni_info.yml");
    private static final Path LOCKFILE = Paths.get("qilletni.lock");
    private static final Path QILLETNI_SRC = Paths.get("qilletni-src");

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(MANIFEST);
        Files.deleteIfExists(LOCKFILE);
        Files.deleteIfExists(QILLETNI_SRC.resolve("qilletni_info.yml"));
        Files.deleteIfExists(QILLETNI_SRC);
    }

    @Test
    void hasManifest_falseWhenNoManifestPresent() {
        assertFalse(ManifestFinder.hasManifest());
    }

    @Test
    void hasManifest_trueWhenManifestInWorkingDirectory() throws IOException {
        Files.writeString(MANIFEST, "name: test-package\n");

        assertTrue(ManifestFinder.hasManifest());
        assertEquals(MANIFEST, ManifestFinder.getManifest());
    }

    @Test
    void getManifest_prefersWorkingDirectoryOverQilletniSrc() throws IOException {
        Files.createDirectories(QILLETNI_SRC);
        Files.writeString(QILLETNI_SRC.resolve("qilletni_info.yml"), "name: nested-package\n");
        Files.writeString(MANIFEST, "name: top-level-package\n");

        assertEquals(MANIFEST, ManifestFinder.getManifest());
    }

    @Test
    void getManifest_fallsBackToQilletniSrcWhenNotInWorkingDirectory() throws IOException {
        Files.createDirectories(QILLETNI_SRC);
        var nestedManifest = QILLETNI_SRC.resolve("qilletni_info.yml");
        Files.writeString(nestedManifest, "name: nested-package\n");

        assertEquals(nestedManifest, ManifestFinder.getManifest());
    }

    @Test
    void getLockfile_returnsWorkingDirectoryPathWhenAbsentEverywhere() {
        assertEquals(LOCKFILE, ManifestFinder.getLockfile());
    }
}
