package com.cafestudio.bingomod.spawn;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class SpawnManager {
	private static final double TWO_PI = Math.PI * 2.0;
	private static final SpawnManager INSTANCE = new SpawnManager();

	private BlockPos lobbyPos;
	private ServerWorld lobbyWorld;

	public static SpawnManager getInstance() {
		return INSTANCE;
	}

	public void setLobbyPos(BlockPos lobbyPos) {
		this.lobbyPos = Objects.requireNonNull(lobbyPos, "lobbyPos").toImmutable();
	}

	public void setLobby(ServerWorld world, BlockPos lobbyPos) {
		this.lobbyWorld = Objects.requireNonNull(world, "world");
		setLobbyPos(lobbyPos);
	}

	public Optional<BlockPos> getLobbyPos() {
		return Optional.ofNullable(lobbyPos);
	}

	public Optional<ServerWorld> getLobbyWorld() {
		return Optional.ofNullable(lobbyWorld);
	}

	public void clearLobby() {
		lobbyPos = null;
		lobbyWorld = null;
	}

	public Vec3d calculateSpreadPos(int playerIndex, int totalPlayers, double spreadRadius) {
		validateSpreadParameters(playerIndex, totalPlayers, spreadRadius);
		BlockPos center = getLobbyPos()
				.orElseThrow(() -> new IllegalStateException("Lobby position has not been set"));

		double angle = calculateAngle(playerIndex, totalPlayers);
		double centerX = center.getX() + 0.5;
		double centerZ = center.getZ() + 0.5;
		return new Vec3d(
				centerX + Math.cos(angle) * spreadRadius,
				center.getY(),
				centerZ + Math.sin(angle) * spreadRadius);
	}

	private static void validateSpreadParameters(int playerIndex, int totalPlayers, double spreadRadius) {
		if (totalPlayers < 1) {
			throw new IllegalArgumentException("totalPlayers must be at least 1");
		}
		if (playerIndex < 0 || playerIndex >= totalPlayers) {
			throw new IllegalArgumentException("playerIndex must be within the player count");
		}
		if (!Double.isFinite(spreadRadius) || spreadRadius <= 0.0) {
			throw new IllegalArgumentException("spreadRadius must be a finite positive number");
		}
	}

	private static double calculateAngle(int playerIndex, int totalPlayers) {
		return TWO_PI * playerIndex / totalPlayers;
	}
}
