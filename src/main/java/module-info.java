module qilletni.pkgutil {
    requires qilletni.api;
    requires com.google.gson;
    requires org.yaml.snakeyaml;

    exports dev.qilletni.pkgutil;
    exports dev.qilletni.pkgutil.adapters;
    exports dev.qilletni.pkgutil.manifest;
    exports dev.qilletni.pkgutil.manifest.models;
}
