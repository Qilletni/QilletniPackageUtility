package dev.qilletni.pkgutil.manifest;

import dev.qilletni.pkgutil.manifest.models.ResolvedPackage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LockFileTest {

    @Test
    void newLockFile_startsEmptyAtVersion1() {
        var lockFile = new LockFile();

        assertEquals(1, lockFile.getVersion());
        assertTrue(lockFile.getPackages().isEmpty());
    }

    @Test
    void addPackage_isKeyedByFullIdentifier() {
        var lockFile = new LockFile();
        var pkg = new ResolvedPackage("@alice/postgres", "1.0.2", "https://registry.example/@alice/postgres/1.0.2",
                "sha256-abc123", Map.of());

        lockFile.addPackage(pkg);

        assertEquals(pkg, lockFile.getPackages().get("@alice/postgres@1.0.2"));
    }

    @Test
    void parse_missingFile_throwsIOException(@TempDir Path tempDir) {
        var missing = tempDir.resolve("qilletni.lock");

        assertThrows(IOException.class, () -> LockFile.parse(missing));
    }

    @Test
    void parse_emptyFile_throwsIOException(@TempDir Path tempDir) throws IOException {
        var lockFilePath = tempDir.resolve("qilletni.lock");
        Files.writeString(lockFilePath, "");

        assertThrows(IOException.class, () -> LockFile.parse(lockFilePath));
    }

    @Test
    void writeThenParse_roundTripsPackageData(@TempDir Path tempDir) throws IOException {
        var lockFile = new LockFile();
        var pkg = new ResolvedPackage("@alice/postgres", "1.0.2", "https://registry.example/@alice/postgres/1.0.2",
                "sha256-abc123", Map.of("@bob/json", "^2.0.0"));
        lockFile.addPackage(pkg);

        var lockFilePath = tempDir.resolve("qilletni.lock");
        lockFile.write(lockFilePath);

        var parsed = LockFile.parse(lockFilePath);

        assertEquals(1, parsed.getVersion());
        var parsedPkg = parsed.getPackages().get("@alice/postgres@1.0.2");
        assertEquals("@alice/postgres", parsedPkg.name());
        assertEquals("1.0.2", parsedPkg.version());
        assertEquals("https://registry.example/@alice/postgres/1.0.2", parsedPkg.resolved());
        assertEquals("sha256-abc123", parsedPkg.integrity());
        assertEquals(Map.of("@bob/json", "^2.0.0"), parsedPkg.dependencies());
    }

    @Test
    void parse_packageWithoutDependencies_defaultsToEmptyMap(@TempDir Path tempDir) throws IOException {
        var lockFilePath = tempDir.resolve("qilletni.lock");
        Files.writeString(lockFilePath, """
                version: 1
                packages:
                  "@alice/postgres@1.0.2":
                    version: 1.0.2
                    resolved: https://registry.example/@alice/postgres/1.0.2
                    integrity: sha256-abc123
                """);

        var parsed = LockFile.parse(lockFilePath);

        var parsedPkg = parsed.getPackages().get("@alice/postgres@1.0.2");
        assertTrue(parsedPkg.dependencies().isEmpty());
    }
}
