package com.cafestudio.bingomod.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class RankingManagerTest {
	@Test
	void recordsOnlyCompletedRoundsAndSortsByTotalTime() {
		RankingManager ranking = new RankingManager();
		UUID fasterPlayer = UUID.randomUUID();
		UUID slowerPlayer = UUID.randomUUID();

		ranking.recordRound(new PlayerRoundStats(fasterPlayer, 1, 100L, true));
		ranking.recordRound(new PlayerRoundStats(fasterPlayer, 2, 120L, true));
		ranking.recordRound(new PlayerRoundStats(fasterPlayer, 3, 500L, false));
		ranking.recordRound(new PlayerRoundStats(slowerPlayer, 1, 250L, true));

		assertEquals(220L, ranking.calculateTotalTime(fasterPlayer));
		assertEquals(java.util.List.of(fasterPlayer, slowerPlayer), ranking.getRanking());
	}

	@Test
	void returnedTimesCannotMutateRankingState() {
		RankingManager ranking = new RankingManager();
		UUID playerId = UUID.randomUUID();
		ranking.recordRound(new PlayerRoundStats(playerId, 1, 100L, true));

		var times = ranking.getPlayerTimes();
		assertThrows(UnsupportedOperationException.class, () -> times.get(playerId).add(200L));
		assertThrows(UnsupportedOperationException.class, () -> times.clear());
		assertEquals(100L, ranking.calculateTotalTime(playerId));
	}

	@Test
	void roundStatsRejectInvalidRoundAndNegativeTicks() {
		UUID playerId = UUID.randomUUID();

		assertThrows(IllegalArgumentException.class, () -> new PlayerRoundStats(playerId, 0, 0L, true));
		assertThrows(IllegalArgumentException.class, () -> new PlayerRoundStats(playerId, 1, -1L, true));
	}
}
