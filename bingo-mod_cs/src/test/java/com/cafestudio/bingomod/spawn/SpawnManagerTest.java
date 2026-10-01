package com.cafestudio.bingomod.spawn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.junit.jupiter.api.Test;

class SpawnManagerTest {
	@Test
	void calculatesPositionsAroundLobbyAtRequestedRadius() {
		SpawnManager spawnManager = new SpawnManager();
		spawnManager.setLobbyPos(new BlockPos(100, 64, -40));

		Vec3d firstPosition = spawnManager.calculateSpreadPos(0, 4, 20.0);
		Vec3d secondPosition = spawnManager.calculateSpreadPos(1, 4, 20.0);

		assertEquals(120.5, firstPosition.x, 0.0001);
		assertEquals(64.0, firstPosition.y, 0.0001);
		assertEquals(-39.5, firstPosition.z, 0.0001);
		assertEquals(20.0, secondPosition.distanceTo(new Vec3d(100.5, 64.0, -39.5)), 0.0001);
	}

	@Test
	void requiresLobbyPositionBeforeCalculating() {
		SpawnManager spawnManager = new SpawnManager();

		assertThrows(IllegalStateException.class, () -> spawnManager.calculateSpreadPos(0, 1, 10.0));
	}

	@Test
	void clearingLobbyRemovesConfiguredPosition() {
		SpawnManager spawnManager = new SpawnManager();
		spawnManager.setLobbyPos(new BlockPos(10, 64, 20));

		spawnManager.clearLobby();

		assertEquals(java.util.Optional.empty(), spawnManager.getLobbyPos());
	}

	@Test
	void rejectsInvalidPlayerIndexAndRadius() {
		SpawnManager spawnManager = new SpawnManager();
		spawnManager.setLobbyPos(BlockPos.ORIGIN);

		assertThrows(IllegalArgumentException.class, () -> spawnManager.calculateSpreadPos(2, 2, 10.0));
		assertThrows(IllegalArgumentException.class, () -> spawnManager.calculateSpreadPos(0, 1, 0.0));
		assertThrows(IllegalArgumentException.class, () -> spawnManager.calculateSpreadPos(0, 1, Double.NaN));
	}
}
