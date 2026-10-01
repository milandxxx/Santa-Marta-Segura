package com.cafestudio.bingomod.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class BingoInventoryScannerTest {
	@Test
	void findsOwnedItemsOnlyOncePerSlot() {
		List<String> boardItems = List.of("stick", "stone", "apple", "stick");
		Set<String> inventoryItems = Set.of("stick", "apple");
		BitSet completedSlots = new BitSet();

		List<Integer> firstScan = BingoInventoryScanner.findNewlyCompletedSlots(
				boardItems,
				inventoryItems,
				completedSlots);
		List<Integer> secondScan = BingoInventoryScanner.findNewlyCompletedSlots(
				boardItems,
				inventoryItems,
				completedSlots);

		assertEquals(List.of(0, 2, 3), firstScan);
		assertEquals(List.of(), secondScan);
		assertEquals(3, completedSlots.cardinality());
	}
}
