package com.lbostani.horneffects.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("horn_effects");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HornConfig config = HornConfig.defaults();

    public static HornConfig get() {
        return config;
    }

    public static void load(Path file) {
        try {
            if (Files.notExists(file)) {
                config = HornConfig.defaults();
                save(file);
                return;
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                HornConfig loaded = GSON.fromJson(reader, HornConfig.class);
                if (loaded != null) {
                    fillMissing(loaded);
                    config = loaded;
                    save(file); // re-save so new options appear in the file
                }
            }
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Could not read config, keeping previous settings", e);
        }
    }

    private static void save(Path file) throws IOException {
        Files.createDirectories(file.getParent());
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(config, writer);
        }
    }

    // Safety: add any horn that is missing and fix bad numbers
    private static void fillMissing(HornConfig c) {
        if (c.aoe_settings == null) c.aoe_settings = new HornConfig.AoeSettings();
        if (c.horn_effects == null) c.horn_effects = HornConfig.defaults().horn_effects;
        HornConfig.defaults().horn_effects.forEach(c.horn_effects::putIfAbsent);
        c.global_cooldown_seconds = Math.max(0, c.global_cooldown_seconds);
        c.permission_level_required = Math.max(0, Math.min(4, c.permission_level_required));
        c.aoe_settings.radius_blocks = Math.max(0, c.aoe_settings.radius_blocks);
    }
}