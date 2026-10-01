package com.cafestudio.bingomod.client;

import java.util.ArrayList;
import java.util.List;

import com.cafestudio.bingomod.model.BingoBoard;
import com.cafestudio.bingomod.model.BingoSlot;
import com.cafestudio.bingomod.network.SyncBoardS2CPayload;
import com.cafestudio.bingomod.network.SyncTimerS2CPayload;
import com.cafestudio.bingomod.network.UpdateSlotS2CPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class BingoClientNetworking {
	private BingoClientNetworking() {
	}

	public static void registerReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(SyncBoardS2CPayload.ID, BingoClientNetworking::onSyncBoard);
		ClientPlayNetworking.registerGlobalReceiver(UpdateSlotS2CPayload.ID, BingoClientNetworking::onUpdateSlot);
		ClientPlayNetworking.registerGlobalReceiver(SyncTimerS2CPayload.ID, BingoClientNetworking::onSyncTimer);
	}

	private static void onSyncBoard(SyncBoardS2CPayload payload, ClientPlayNetworking.Context context) {
		List<Identifier> itemIds = List.copyOf(payload.itemIds());

		context.client().execute(() -> {
			ClientBingoState.setBoard(BingoBoard.fromItems(resolveItems(itemIds)));
		});
	}

	private static void onUpdateSlot(UpdateSlotS2CPayload payload, ClientPlayNetworking.Context context) {
		int row = payload.row();
		int col = payload.col();
		boolean completed = payload.completed();

		context.client().execute(() -> {
			applySlotUpdate(row, col, completed);
		});
	}

	private static void onSyncTimer(SyncTimerS2CPayload payload, ClientPlayNetworking.Context context) {
		long remainingTicks = payload.remainingTicks();

		context.client().execute(() -> {
			ClientBingoState.setRemainingTicks(remainingTicks);
		});
	}

	private static void applySlotUpdate(int row, int col, boolean completed) {
		if (!ClientBingoState.hasBoard()) {
			return;
		}

		if (!isValidSlot(row, col)) {
			return;
		}

		BingoSlot slot = ClientBingoState.getBoard().getSlot(row, col);
		slot.setCompleted(completed);
	}

	private static boolean isValidSlot(int row, int col) {
		return row >= 0 && row < BingoBoard.SIZE && col >= 0 && col < BingoBoard.SIZE;
	}

	private static List<Item> resolveItems(List<Identifier> itemIds) {
		List<Item> items = new ArrayList<>(itemIds.size());

		for (Identifier itemId : itemIds) {
			items.add(Registries.ITEM.get(itemId));
		}

		return items;
	}
}
