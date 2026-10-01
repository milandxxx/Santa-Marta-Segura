package com.cafestudio.bingomod.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BingoCommandsTest {
	@Test
	void convertsWholeMinutesToServerTicks() {
		assertEquals(6_000L, BingoCommands.minutesToTicks(5));
		assertEquals(8_400L, BingoCommands.minutesToTicks(7));
		assertEquals(72_000L, BingoCommands.minutesToTicks(60));
	}

	@Test
	void usesAgreedDefaultTimeAndRoundCount() {
		assertEquals(10, BingoCommands.DEFAULT_TIME_MINUTES);
		assertEquals(5, BingoCommands.DEFAULT_TOTAL_ROUNDS);
	}
}
