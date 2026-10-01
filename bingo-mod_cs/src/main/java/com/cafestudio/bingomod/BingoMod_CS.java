package com.cafestudio.bingomod;

import com.cafestudio.bingomod.command.BingoCommands;
import com.cafestudio.bingomod.game.BingoInventoryScanner;
import com.cafestudio.bingomod.game.BingoRoundManager;
import com.cafestudio.bingomod.network.BingoNetworking;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BingoMod_CS implements ModInitializer {
	public static final String MOD_ID = "bingo-mod_cs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BingoNetworking.registerPayloadTypes();
		BingoInventoryScanner.register();
		BingoRoundManager.register();
		BingoCommands.register();
		LOGGER.info("Bingo networking payloads registered");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
