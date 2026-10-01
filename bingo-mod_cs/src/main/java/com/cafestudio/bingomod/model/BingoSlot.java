package com.cafestudio.bingomod.model;

import java.util.Objects;

import net.minecraft.item.Item;

public final class BingoSlot {
	private final Item item;
	private boolean completed;

	public BingoSlot(Item item) {
		this.item = Objects.requireNonNull(item, "item");
		this.completed = false;
	}

	public Item getItem() {
		return item;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void markCompleted() {
		this.completed = true;
	}
}
