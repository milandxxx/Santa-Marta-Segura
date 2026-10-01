package com.cafestudio.bingomod.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class RankingManager {
	private final Map<UUID, List<Long>> playerTimes = new HashMap<>();

	public void recordRound(PlayerRoundStats stats) {
		Objects.requireNonNull(stats, "stats");

		if (!stats.isCompleted()) {
			return;
		}

		playerTimes
				.computeIfAbsent(stats.getPlayerId(), id -> new ArrayList<>())
				.add(stats.getCompletionTicks());
	}

	public long calculateTotalTime(UUID playerId) {
		Objects.requireNonNull(playerId, "playerId");
		List<Long> times = playerTimes.getOrDefault(playerId, List.of());
		return sumTicks(times);
	}

	public List<UUID> getRanking() {
		List<UUID> rankedPlayers = new ArrayList<>(playerTimes.keySet());
		rankedPlayers.sort(Comparator.comparingLong(this::calculateTotalTime));
		return Collections.unmodifiableList(rankedPlayers);
	}

	public Map<UUID, List<Long>> getPlayerTimes() {
		Map<UUID, List<Long>> snapshot = new HashMap<>();
		playerTimes.forEach((playerId, times) -> snapshot.put(playerId, List.copyOf(times)));
		return Collections.unmodifiableMap(snapshot);
	}

	public void clear() {
		playerTimes.clear();
	}

	private static long sumTicks(List<Long> times) {
		long total = 0L;
		for (Long ticks : times) {
			total += ticks;
		}
		return total;
	}
}
