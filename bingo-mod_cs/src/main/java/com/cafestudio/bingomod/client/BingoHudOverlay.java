package com.cafestudio.bingomod.client;

import com.cafestudio.bingomod.model.BingoBoard;
import com.cafestudio.bingomod.model.BingoSlot;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

public final class BingoHudOverlay {
	public static final int CELL_SIZE = 18;

	private static final int BOARD_MARGIN_X = 4;
	private static final int BOARD_MARGIN_Y = 16;
	private static final int ITEM_OFFSET = 1;
	private static final int TIMER_GAP_ABOVE_BOARD = 12;
	private static final int CELL_BACKGROUND_COLOR = 0x90000000;
	private static final int COMPLETED_OVERLAY_COLOR = 0x884CAF50;
	private static final int TIMER_TEXT_COLOR = 0xFFFFFFFF;
	private static final int TICKS_PER_SECOND = 20;
	private static final int SECONDS_PER_MINUTE = 60;

	private BingoHudOverlay() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register(BingoHudOverlay::onHudRender);
	}

	private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		if (!ClientBingoState.hasBoard()) {
			return;
		}

		renderBoard(drawContext, ClientBingoState.getBoard());
		renderTimer(drawContext, ClientBingoState.getRemainingTicks());
	}

	private static void renderBoard(DrawContext drawContext, BingoBoard board) {
		for (int row = 0; row < BingoBoard.SIZE; row++) {
			for (int col = 0; col < BingoBoard.SIZE; col++) {
				renderCell(drawContext, board.getSlot(row, col), row, col);
			}
		}
	}

	private static void renderCell(DrawContext drawContext, BingoSlot slot, int row, int col) {
		int cellX = calculateCellX(col);
		int cellY = calculateCellY(row);

		drawCellBackground(drawContext, cellX, cellY);
		drawCellItem(drawContext, slot, cellX, cellY);
		drawCompletedOverlay(drawContext, slot, cellX, cellY);
	}

	private static void drawCellBackground(DrawContext drawContext, int cellX, int cellY) {
		drawContext.fill(
				cellX,
				cellY,
				cellX + CELL_SIZE,
				cellY + CELL_SIZE,
				CELL_BACKGROUND_COLOR);
	}

	private static void drawCellItem(DrawContext drawContext, BingoSlot slot, int cellX, int cellY) {
		int itemX = calculateItemX(cellX);
		int itemY = calculateItemY(cellY);
		ItemStack stack = new ItemStack(slot.getItem());
		drawContext.drawItem(stack, itemX, itemY);
	}

	private static void drawCompletedOverlay(DrawContext drawContext, BingoSlot slot, int cellX, int cellY) {
		if (!slot.isCompleted()) {
			return;
		}

		drawContext.fill(
				cellX,
				cellY,
				cellX + CELL_SIZE,
				cellY + CELL_SIZE,
				COMPLETED_OVERLAY_COLOR);
	}

	private static void renderTimer(DrawContext drawContext, long remainingTicks) {
		String timerText = formatRemainingTime(remainingTicks);
		int timerX = calculateTimerX();
		int timerY = calculateTimerY();

		drawContext.drawText(
				MinecraftClient.getInstance().textRenderer,
				timerText,
				timerX,
				timerY,
				TIMER_TEXT_COLOR,
				true);
	}

	private static String formatRemainingTime(long remainingTicks) {
		long totalSeconds = ticksToSeconds(remainingTicks);
		long minutes = totalSeconds / SECONDS_PER_MINUTE;
		long seconds = totalSeconds % SECONDS_PER_MINUTE;
		return String.format("%02d:%02d", minutes, seconds);
	}

	private static long ticksToSeconds(long ticks) {
		return ticks / TICKS_PER_SECOND;
	}

	private static int calculateTimerX() {
		return calculateBoardOriginX();
	}

	private static int calculateTimerY() {
		return calculateBoardOriginY() - TIMER_GAP_ABOVE_BOARD;
	}

	private static int calculateItemX(int cellX) {
		return cellX + ITEM_OFFSET;
	}

	private static int calculateItemY(int cellY) {
		return cellY + ITEM_OFFSET;
	}

	private static int calculateBoardOriginX() {
		return BOARD_MARGIN_X;
	}

	private static int calculateBoardOriginY() {
		return BOARD_MARGIN_Y;
	}

	private static int calculateCellX(int col) {
		return calculateBoardOriginX() + (col * CELL_SIZE);
	}

	private static int calculateCellY(int row) {
		return calculateBoardOriginY() + (row * CELL_SIZE);
	}

	private static int calculateBoardWidth() {
		return BingoBoard.SIZE * CELL_SIZE;
	}

	private static int calculateBoardHeight() {
		return BingoBoard.SIZE * CELL_SIZE;
	}
}
