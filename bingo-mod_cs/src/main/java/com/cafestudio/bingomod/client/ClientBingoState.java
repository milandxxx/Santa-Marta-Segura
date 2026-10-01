package com.cafestudio.bingomod.client;

import com.cafestudio.bingomod.model.BingoBoard;

public final class ClientBingoState {
	private static BingoBoard board;
	private static long remainingTicks;

	private ClientBingoState() {
	}

	public static void setBoard(BingoBoard board) {
		ClientBingoState.board = board;
	}

	public static BingoBoard getBoard() {
		return board;
	}

	public static boolean hasBoard() {
		return board != null;
	}

	public static void clearBoard() {
		board = null;
		remainingTicks = 0L;
	}

	public static void setRemainingTicks(long remainingTicks) {
		ClientBingoState.remainingTicks = Math.max(0L, remainingTicks);
	}

	public static long getRemainingTicks() {
		return remainingTicks;
	}
}
