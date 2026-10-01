package com.cafestudio.bingomod.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BingoWinConditionsTest {
	@Test
	void detectsCompletedRowsAndColumns() {
		boolean[][] completedSlots = new boolean[5][5];
		completedSlots[2] = new boolean[]{true, true, true, true, true};
		assertTrue(BingoWinConditions.checkLines(completedSlots));

		completedSlots = new boolean[5][5];
		for (boolean[] row : completedSlots) {
			row[3] = true;
		}
		assertTrue(BingoWinConditions.checkLines(completedSlots));
	}

	@Test
	void doesNotCountPartialLine() {
		boolean[][] completedSlots = new boolean[5][5];
		for (int col = 0; col < 4; col++) {
			completedSlots[1][col] = true;
		}

		assertFalse(BingoWinConditions.checkLines(completedSlots));
	}

	@Test
	void detectsBothMainDiagonals() {
		boolean[][] firstDiagonal = new boolean[5][5];
		boolean[][] secondDiagonal = new boolean[5][5];
		for (int index = 0; index < 5; index++) {
			firstDiagonal[index][index] = true;
			secondDiagonal[index][4 - index] = true;
		}

		assertTrue(BingoWinConditions.checkDiagonals(firstDiagonal));
		assertTrue(BingoWinConditions.checkDiagonals(secondDiagonal));
	}

	@Test
	void requiresEverySlotForFullCard() {
		boolean[][] completedSlots = new boolean[5][5];
		for (boolean[] row : completedSlots) {
			java.util.Arrays.fill(row, true);
		}
		assertTrue(BingoWinConditions.checkFullCard(completedSlots));

		completedSlots[4][4] = false;
		assertFalse(BingoWinConditions.checkFullCard(completedSlots));
	}

	@Test
	void rejectsBoardsThatAreNotFiveByFive() {
		assertThrows(IllegalArgumentException.class, () -> BingoWinConditions.checkLines(new boolean[4][5]));
		assertThrows(IllegalArgumentException.class, () -> BingoWinConditions.checkDiagonals(new boolean[5][4]));
		assertThrows(NullPointerException.class, () -> BingoWinConditions.checkFullCard(null));
	}
}
