package com.lbostani.horneffects.fabric;

import com.lbostani.horneffects.core.ConfigManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HornCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("goathornconfig")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)) // OP level 2+
                            .then(Commands.literal("reload")
                                    .executes(context -> {
                                        ConfigManager.load(
                                                FabricLoader.getInstance().getConfigDir().resolve("horn_effects.json")
                                        );
                                        context.getSource().sendSuccess(
                                                () -> Component.literal("🎺 Horn Effects config reloaded!"),
                                                true
                                        );
                                        return 1;
                                    })
                            )
            );
        });
    }
}