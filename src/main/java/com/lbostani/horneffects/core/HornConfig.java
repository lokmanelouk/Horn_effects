package com.lbostani.horneffects.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class HornConfig {
    public int global_cooldown_seconds = 30; // Raised to 30s
    public int permission_level_required = 2;
    public AoeSettings aoe_settings = new AoeSettings();
    public Map<String, HornEffect> horn_effects = new LinkedHashMap<>();

    public static class AoeSettings {
        public boolean enable_aoe = true;
        public double radius_blocks = 10.0;
        public boolean include_self = true;
        public boolean affect_players = true;
        public boolean affect_friendly_mobs = true;
    }

    public static class HornEffect {
        public String effect;
        public int duration_seconds;
        public int amplifier;
    }

    private static HornEffect of(String effect, int seconds, int amp) {
        HornEffect e = new HornEffect();
        e.effect = effect;
        e.duration_seconds = seconds;
        e.amplifier = amp;
        return e;
    }

    public static HornConfig defaults() {
        HornConfig c = new HornConfig();
        c.horn_effects.put("seek",   of("minecraft:fire_resistance", 12, 0));
        c.horn_effects.put("feel",   of("minecraft:water_breathing", 20, 0));
        c.horn_effects.put("sing",   of("minecraft:speed", 10, 0));
        c.horn_effects.put("ponder", of("minecraft:dolphins_grace", 15, 0));
        c.horn_effects.put("call",   of("minecraft:strength", 6, 0)); // Amp 0 = Strength I
        c.horn_effects.put("admire", of("minecraft:haste", 8, 1));     // Amp 1 = Haste II
        c.horn_effects.put("yearn",  of("minecraft:resistance", 6, 0));
        c.horn_effects.put("dream",  of("minecraft:slow_falling", 10, 0));
        return c;
    }
}