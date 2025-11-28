package dev.qilletni.pkgutil.manifest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ManifestFinder {

    /**
     * Returns a {@link Path} in `qilletni-src` of the given name that exists. The working directory is either in
     * `qilletni-src` itself, or the parent of the directory.
     *
     * @return A {@link Path} with the given name in the `qilletni-src` directory. This path will always exist
     */
    private static Path getExistingPathInSrc(String filename) {
        var manifestPath = Paths.get(filename);
        var qilletniSrc = Paths.get("qilletni-src");

        if (!Files.exists(manifestPath) && Files.exists(qilletniSrc)) {
            manifestPath = qilletniSrc.resolve(manifestPath);
        }

        return manifestPath;
    }

    public static boolean hasManifest() {
        return Files.exists(getManifest());
    }

    public static Path getLockfile() {
        return getExistingPathInSrc("qilletni.lock");
    }

    public static Path getManifest() {
        return getExistingPathInSrc("qilletni_info.yml");
    }

}
