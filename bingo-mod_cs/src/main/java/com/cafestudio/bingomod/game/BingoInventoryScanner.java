package com.cafestudio.bingomod.game;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.cafestudio.bingomod.model.BingoBoard;
import com.cafestudio.bingomod.network.BingoNetworking;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class BingoInventoryScanner {
	private static final BingoInventoryScanner INSTANCE = new BingoInventoryScanner();

	private final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();
	private boolean roundActive;

	private BingoInventoryScanner() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(INSTANCE::scanPlayers);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> INSTANCE.clearBoard());
	}

	public static void setBoard(UUID playerId, BingoBoard board) {
		Objects.requireNonNull(playerId, "playerId");
		Objects.requireNonNull(board, "board");
		INSTANCE.playerProgress.put(playerId, new PlayerProgress(extractBoardItems(board)));
	}

	public static void setRoundActive(boolean roundActive) {
		INSTANCE.roundActive = roundActive;
	}

	public static boolean hasBoard(UUID playerId) {
		Objects.requireNonNull(playerId, "playerId");
		return INSTANCE.playerProgress.containsKey(playerId);
	}

	public static boolean isSlotCompleted(UUID playerId, int row, int col) {
		Objects.requireNonNull(playerId, "playerId");
		validateSlot(row, col);
		PlayerProgress progress = INSTANCE.playerProgress.get(playerId);
		return progress != null && progress.completedSlots().get(toSlotIndex(row, col));
	}

	private void scanPlayers(MinecraftServer server) {
		if (!roundActive) {
			return;
		}

		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			scanPlayerInventory(player);
		}
	}

	private void clearBoard() {
		playerProgress.clear();
		roundActive = false;
	}

	private void scanPlayerInventory(ServerPlayerEntity player) {
		PlayerProgress progress = playerProgress.get(player.getUuid());
		if (progress == null) {
			return;
		}

		Set<Item> inventoryItems = collectInventoryItems(player);

		for (int index : findNewlyCompletedSlots(
				progress.boardItems(),
				inventoryItems,
				progress.completedSlots())) {
			int row = index / BingoBoard.SIZE;
			int col = index % BingoBoard.SIZE;
			BingoNetworking.sendSlotUpdate(player, row, col, true);
		}
	}

	private static List<Item> extractBoardItems(BingoBoard board) {
		List<Item> boardItems = new ArrayList<>(BingoBoard.SLOT_COUNT);
		for (int index = 0; index < BingoBoard.SLOT_COUNT; index++) {
			int row = index / BingoBoard.SIZE;
			int col = index % BingoBoard.SIZE;
			boardItems.add(board.getSlot(row, col).getItem());
		}
		return List.copyOf(boardItems);
	}

	static <T> List<Integer> findNewlyCompletedSlots(
			List<T> boardItems,
			Set<T> inventoryItems,
			BitSet completedSlots) {
		List<Integer> newlyCompletedSlots = new ArrayList<>();
		for (int index = 0; index < boardItems.size(); index++) {
			if (!completedSlots.get(index) && inventoryItems.contains(boardItems.get(index))) {
				completedSlots.set(index);
				newlyCompletedSlots.add(index);
			}
		}
		return newlyCompletedSlots;
	}

	private static Set<Item> collectInventoryItems(ServerPlayerEntity player) {
		Set<Item> inventoryItems = new HashSet<>();
		for (int slot = 0; slot < player.getInventory().size(); slot++) {
			ItemStack stack = player.getInventory().getStack(slot);
			if (!stack.isEmpty()) {
				inventoryItems.add(stack.getItem());
			}
		}
		return inventoryItems;
	}

	private static int toSlotIndex(int row, int col) {
		return row * BingoBoard.SIZE + col;
	}

	private static void validateSlot(int row, int col) {
		if (row < 0 || row >= BingoBoard.SIZE || col < 0 || col >= BingoBoard.SIZE) {
			throw new IndexOutOfBoundsException("Slot out of range: (" + row + ", " + col + ")");
		}
	}

	private record PlayerProgress(List<Item> boardItems, BitSet completedSlots) {
		private PlayerProgress(List<Item> boardItems) {
			this(boardItems, new BitSet(BingoBoard.SLOT_COUNT));
		}
	}
}
