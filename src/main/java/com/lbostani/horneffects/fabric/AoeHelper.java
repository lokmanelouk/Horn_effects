package com.lbostani.horneffects.fabric;

import com.lbostani.horneffects.core.ConfigManager;
import com.lbostani.horneffects.core.HornConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AoeHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("horn_effects");

    public static void apply(ServerPlayer player, HornConfig.HornEffect cfg) {
        Holder<MobEffect> effect = resolveEffect(cfg.effect);
        if (effect == null) return;

        HornConfig.AoeSettings aoe = ConfigManager.get().aoe_settings;

        // Buff the horn user
        if (!aoe.enable_aoe || aoe.include_self) {
            give(player, effect, cfg);
        }
        if (!aoe.enable_aoe) return;

        double radius = aoe.radius_blocks;
        double radiusSq = radius * radius;

        List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                e -> e != player && e.isAlive() && e.distanceToSqr(player) <= radiusSq
        );

        for (LivingEntity target : nearby) {
            if (target instanceof ServerPlayer) {
                if (aoe.affect_players) give(target, effect, cfg);
            } else if (target instanceof Mob && !(target instanceof Enemy)) {
                if (aoe.affect_friendly_mobs) give(target, effect, cfg);
            }
        }
    }

    private static void give(LivingEntity target, Holder<MobEffect> effect, HornConfig.HornEffect cfg) {
        // New instance per target, because effect instances can't be shared
        target.addEffect(new MobEffectInstance(effect, cfg.duration_seconds * 20, cfg.amplifier));
    }

    private static Holder<MobEffect> resolveEffect(String name) {
        Identifier id = Identifier.tryParse(name);
        if (id == null) {
            LOGGER.warn("Bad effect id in config: {}", name);
            return null;
        }
        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.get(id).orElse(null);
        if (effect == null) {
            LOGGER.warn("Unknown effect in config: {}", name);
        }
        return effect;
    }
}