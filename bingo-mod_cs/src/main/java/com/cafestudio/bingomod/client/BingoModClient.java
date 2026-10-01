package com.cafestudio.bingomod.client;

import net.fabricmc.api.ClientModInitializer;

public class BingoModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BingoHudOverlay.register();
		BingoClientNetworking.registerReceivers();
	}
}
