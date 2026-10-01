package com.cafestudio.bingomod.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cafestudio.bingomod.model.GameMode;

import org.junit.jupiter.api.Test;

class BingoRoundManagerTest {
	@Test
	void randomModeResolvesToOneConcreteWinCondition() {
		GameMode resolvedMode = BingoRoundManager.resolveMode(GameMode.RANDOM);

		assertNotEquals(GameMode.RANDOM, resolvedMode);
		assertTrue(resolvedMode == GameMode.LINE
				|| resolvedMode == GameMode.DIAGONAL
				|| resolvedMode == GameMode.MATRIX);
	}

	@Test
	void explicitModeIsPreserved() {
		assertEquals(GameMode.DIAGONAL, BingoRoundManager.resolveMode(GameMode.DIAGONAL));
	}
}
