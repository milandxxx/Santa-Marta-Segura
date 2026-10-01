package com.cafestudio.bingomod.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.item.Item;

public final class BingoBoard {
	public static final int SIZE = 5;
	public static final int SLOT_COUNT = SIZE * SIZE;

	private final BingoSlot[][] grid;

	private BingoBoard(BingoSlot[][] grid) {
		this.grid = grid;
	}

	public static BingoBoard generateBoard(BingoPool pool) {
		Objects.requireNonNull(pool, "pool");
		return generateBoard(pool.getItems());
	}

	public static BingoBoard generateBoard(List<Item> pool) {
		Objects.requireNonNull(pool, "pool");
		List<Item> selectedItems = selectUniqueItems(pool, SLOT_COUNT);
		BingoSlot[][] grid = buildGrid(selectedItems);
		return new BingoBoard(grid);
	}

	public static BingoBoard fromItems(List<Item> items) {
		List<Item> boardItems = List.copyOf(Objects.requireNonNull(items, "items"));
		if (boardItems.size() != SLOT_COUNT) {
			throw new IllegalArgumentException(
					"Board requires exactly " + SLOT_COUNT + " items, got " + boardItems.size());
		}
		validateUniqueItems(boardItems);

		return new BingoBoard(buildGrid(boardItems));
	}

	public BingoSlot getSlot(int row, int col) {
		validateIndex(row, col);
		return grid[row][col];
	}

	public BingoSlot[][] getGrid() {
		return copyGrid(grid);
	}

	static <T> List<T> selectUniqueItems(List<T> pool, int selectionCount) {
		Objects.requireNonNull(pool, "pool");
		if (selectionCount < 1) {
			throw new IllegalArgumentException("selectionCount must be at least 1");
		}

		List<T> uniqueItems = new ArrayList<>(new LinkedHashSet<>(List.copyOf(pool)));
		if (uniqueItems.size() < selectionCount) {
			throw new IllegalArgumentException(
					"Pool must contain at least " + selectionCount + " unique items, got " + uniqueItems.size());
		}

		Collections.shuffle(uniqueItems);
		return uniqueItems.subList(0, selectionCount);
	}

	static <T> void validateUniqueItems(List<T> items) {
		Set<T> uniqueItems = new HashSet<>(items);
		if (uniqueItems.size() != items.size()) {
			throw new IllegalArgumentException("Board items must be unique");
		}
	}

	static <T> T[][] copyGrid(T[][] grid) {
		T[][] gridCopy = grid.clone();
		for (int row = 0; row < grid.length; row++) {
			gridCopy[row] = grid[row].clone();
		}
		return gridCopy;
	}

	private static BingoSlot[][] buildGrid(List<Item> selectedItems) {
		BingoSlot[][] grid = new BingoSlot[SIZE][SIZE];

		for (int index = 0; index < SLOT_COUNT; index++) {
			int row = toRow(index);
			int col = toCol(index);
			grid[row][col] = new BingoSlot(selectedItems.get(index));
		}

		return grid;
	}

	private static int toRow(int index) {
		return index / SIZE;
	}

	private static int toCol(int index) {
		return index % SIZE;
	}

	private static void validateIndex(int row, int col) {
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
			throw new IndexOutOfBoundsException(
					"Slot out of range: (" + row + ", " + col + ")");
		}
	}
}
