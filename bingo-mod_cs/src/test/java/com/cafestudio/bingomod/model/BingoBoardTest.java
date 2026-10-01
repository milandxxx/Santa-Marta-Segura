package com.cafestudio.bingomod.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class BingoBoardTest {
	@Test
	void selectionReturnsRequiredNumberOfUniqueItems() {
		List<String> pool = List.of(
				"item-1", "item-2", "item-3", "item-4", "item-5",
				"item-1", "item-2", "item-3", "item-4", "item-5");

		assertEquals(5, BingoBoard.selectUniqueItems(pool, 5).size());
	}

	@Test
	void selectionRejectsPoolWithTooFewUniqueItems() {
		List<String> pool = List.of("item-1", "item-2", "item-1", "item-2");

		assertThrows(IllegalArgumentException.class, () -> BingoBoard.selectUniqueItems(pool, 3));
	}

	@Test
	void boardValidationRejectsDuplicateItems() {
		assertThrows(
				IllegalArgumentException.class,
				() -> BingoBoard.validateUniqueItems(List.of("item-1", "item-2", "item-1")));
	}

	@Test
	void copiedGridDoesNotShareRows() {
		String[][] source = {{"a", "b"}, {"c", "d"}};

		String[][] copy = BingoBoard.copyGrid(source);
		copy[0][0] = "changed";

		assertEquals("a", source[0][0]);
	}
}
