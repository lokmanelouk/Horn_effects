package com.lbostani.horneffects;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lbostani.horneffects.core.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import com.lbostani.horneffects.fabric.HornListener;
import com.lbostani.horneffects.fabric.HornCommand;



public class HornEffects implements ModInitializer {
	public static final String MOD_ID = "horn_effects";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigManager.load(FabricLoader.getInstance().getConfigDir().resolve("horn_effects.json"));
		HornListener.register();
		LOGGER.info("Hello Fabric world!");
		HornCommand.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
