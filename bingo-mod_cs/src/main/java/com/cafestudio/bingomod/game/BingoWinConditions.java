package com.cafestudio.bingomod.game;

import java.util.Objects;

import com.cafestudio.bingomod.model.BingoBoard;

public final class BingoWinConditions {
	private BingoWinConditions() {
	}

	public static boolean checkLines(boolean[][] completedSlots) {
		validateBoard(completedSlots);

		for (int index = 0; index < BingoBoard.SIZE; index++) {
			if (isRowCompleted(completedSlots, index) || isColumnCompleted(completedSlots, index)) {
				return true;
			}
		}

		return false;
	}

	public static boolean checkDiagonals(boolean[][] completedSlots) {
		validateBoard(completedSlots);
		return isDiagonalCompleted(completedSlots, false) || isDiagonalCompleted(completedSlots, true);
	}

	public static boolean checkFullCard(boolean[][] completedSlots) {
		validateBoard(completedSlots);

		for (boolean[] row : completedSlots) {
			for (boolean completed : row) {
				if (!completed) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean isRowCompleted(boolean[][] completedSlots, int row) {
		for (boolean completed : completedSlots[row]) {
			if (!completed) {
				return false;
			}
		}
		return true;
	}

	private static boolean isColumnCompleted(boolean[][] completedSlots, int col) {
		for (int row = 0; row < BingoBoard.SIZE; row++) {
			if (!completedSlots[row][col]) {
				return false;
			}
		}
		return true;
	}

	private static boolean isDiagonalCompleted(boolean[][] completedSlots, boolean reverse) {
		for (int index = 0; index < BingoBoard.SIZE; index++) {
			int col = reverse ? BingoBoard.SIZE - 1 - index : index;
			if (!completedSlots[index][col]) {
				return false;
			}
		}
		return true;
	}

	private static void validateBoard(boolean[][] completedSlots) {
		Objects.requireNonNull(completedSlots, "completedSlots");
		if (completedSlots.length != BingoBoard.SIZE) {
			throw new IllegalArgumentException("Board must have exactly " + BingoBoard.SIZE + " rows");
		}
		for (boolean[] row : completedSlots) {
			if (row == null || row.length != BingoBoard.SIZE) {
				throw new IllegalArgumentException("Each board row must have exactly " + BingoBoard.SIZE + " slots");
			}
		}
	}
}
