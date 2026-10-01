package com.cafestudio.bingomod.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.cafestudio.bingomod.model.BingoBoard;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class BingoNetworking {
	private BingoNetworking() {
	}

	public static void registerPayloadTypes() {
		PayloadTypeRegistry.playS2C().register(SyncBoardS2CPayload.ID, SyncBoardS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(UpdateSlotS2CPayload.ID, UpdateSlotS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SyncTimerS2CPayload.ID, SyncTimerS2CPayload.CODEC);
	}

	public static void sendBoard(ServerPlayerEntity player, BingoBoard board) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(board, "board");
		ServerPlayNetworking.send(player, new SyncBoardS2CPayload(extractItemIds(board)));
	}

	public static void sendBoardToAll(MinecraftServer server, BingoBoard board) {
		Objects.requireNonNull(server, "server");
		Objects.requireNonNull(board, "board");

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			sendBoard(player, board);
		}
	}

	public static void sendSlotUpdate(ServerPlayerEntity player, int row, int col, boolean completed) {
		Objects.requireNonNull(player, "player");
		ServerPlayNetworking.send(player, new UpdateSlotS2CPayload(row, col, completed));
	}

	public static void sendSlotUpdateToAll(MinecraftServer server, int row, int col, boolean completed) {
		Objects.requireNonNull(server, "server");

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			sendSlotUpdate(player, row, col, completed);
		}
	}

	public static void sendTimer(ServerPlayerEntity player, long remainingTicks) {
		Objects.requireNonNull(player, "player");
		ServerPlayNetworking.send(player, new SyncTimerS2CPayload(remainingTicks));
	}

	public static void sendTimerToAll(MinecraftServer server, long remainingTicks) {
		Objects.requireNonNull(server, "server");

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			sendTimer(player, remainingTicks);
		}
	}

	private static List<Identifier> extractItemIds(BingoBoard board) {
		List<Identifier> itemIds = new ArrayList<>(BingoBoard.SLOT_COUNT);

		for (int index = 0; index < BingoBoard.SLOT_COUNT; index++) {
			int row = index / BingoBoard.SIZE;
			int col = index % BingoBoard.SIZE;
			itemIds.add(Registries.ITEM.getId(board.getSlot(row, col).getItem()));
		}

		return itemIds;
	}
}
