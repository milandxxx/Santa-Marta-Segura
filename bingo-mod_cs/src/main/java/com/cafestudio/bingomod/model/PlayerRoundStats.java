package com.cafestudio.bingomod.model;

import java.util.Objects;
import java.util.UUID;

public final class PlayerRoundStats {
	private final UUID playerId;
	private final int roundNumber;
	private final long completionTicks;
	private final boolean completed;

	public PlayerRoundStats(UUID playerId, int roundNumber, long completionTicks, boolean completed) {
		this.playerId = Objects.requireNonNull(playerId, "playerId");
		if (roundNumber < 1) {
			throw new IllegalArgumentException("roundNumber must be at least 1");
		}
		if (completionTicks < 0L) {
			throw new IllegalArgumentException("completionTicks cannot be negative");
		}
		this.roundNumber = roundNumber;
		this.completionTicks = completionTicks;
		this.completed = completed;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public long getCompletionTicks() {
		return completionTicks;
	}

	public boolean isCompleted() {
		return completed;
	}
}
