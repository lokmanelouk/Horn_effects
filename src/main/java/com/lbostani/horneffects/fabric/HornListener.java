package com.lbostani.horneffects.fabric;

import com.lbostani.horneffects.core.ConfigManager;
import com.lbostani.horneffects.core.HornConfig;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.InstrumentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import java.util.ArrayList;
import java.util.List;

public class HornListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("horn_effects");
    private static final List<Runnable> PENDING = new ArrayList<>();


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (PENDING.isEmpty()) return;
            List<Runnable> tasks = new ArrayList<>(PENDING);
            PENDING.clear();
            tasks.forEach(Runnable::run);
        });
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!stack.is(Items.GOAT_HORN)) {
                return InteractionResult.PASS;
            }

            if (player.getCooldowns().isOnCooldown(stack)) {
                return InteractionResult.PASS;
            }

            String hornName = getHornName(stack);
            if (hornName == null) {
                return InteractionResult.PASS;
            }

            HornConfig.HornEffect cfg = ConfigManager.get().horn_effects.get(hornName);
            if (cfg == null) {
                return InteractionResult.PASS;
            }

            AoeHelper.apply(serverPlayer, cfg);
            int cooldownTicks = ConfigManager.get().global_cooldown_seconds * 20;
            PENDING.add(() -> serverPlayer.getCooldowns().addCooldown(stack, cooldownTicks));
            return InteractionResult.PASS;
        });
    }

    private static String getHornName(ItemStack stack) {
        InstrumentComponent component = stack.get(DataComponents.INSTRUMENT);
        if (component == null) return null;

        Holder<Instrument> holder = component.instrument();
        if (holder == null) return null;

        return holder.unwrapKey()
                .map(key -> key.identifier().getPath().replace("_goat_horn", ""))
                .orElse(null);
    }

//    private static void applyEffect(ServerPlayer player, HornConfig.HornEffect cfg) {
//        Identifier id = Identifier.tryParse(cfg.effect);
//        if (id == null) {
//            LOGGER.warn("Bad effect id in config: {}", cfg.effect);
//            return;
//        }
//
//        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.get(id).orElse(null);
//        if (effect == null) {
//            LOGGER.warn("Unknown effect in config: {}", cfg.effect);
//            return;
//        }
//
//        player.addEffect(new MobEffectInstance(effect, cfg.duration_seconds * 20, cfg.amplifier));
//    }
}