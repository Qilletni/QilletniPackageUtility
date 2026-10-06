package dev.qilletni.pkgutil.manifest.models;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResolvedPackageTest {

    @Test
    void getFullIdentifier_combinesNameAndVersion() {
        var pkg = new ResolvedPackage("@alice/postgres", "1.0.2", "https://registry.example/packages/@alice/postgres/1.0.2",
                "sha256-abc123", Map.of());

        assertEquals("@alice/postgres@1.0.2", pkg.getFullIdentifier());
    }

    @Test
    void constructor_rejectsNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResolvedPackage(null, "1.0.2", "https://registry.example", "sha256-abc123", Map.of()));
    }

    @Test
    void constructor_rejectsEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResolvedPackage("", "1.0.2", "https://registry.example", "sha256-abc123", Map.of()));
    }

    @Test
    void constructor_rejectsEmptyVersion() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResolvedPackage("@alice/postgres", "", "https://registry.example", "sha256-abc123", Map.of()));
    }

    @Test
    void constructor_rejectsEmptyResolvedUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResolvedPackage("@alice/postgres", "1.0.2", "", "sha256-abc123", Map.of()));
    }

    @Test
    void constructor_rejectsEmptyIntegrity() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResolvedPackage("@alice/postgres", "1.0.2", "https://registry.example", "", Map.of()));
    }
}
