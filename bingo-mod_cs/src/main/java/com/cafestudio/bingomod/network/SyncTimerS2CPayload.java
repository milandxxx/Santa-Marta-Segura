package com.cafestudio.bingomod.network;

import com.cafestudio.bingomod.BingoMod_CS;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncTimerS2CPayload(long remainingTicks) implements CustomPayload {
	public static final CustomPayload.Id<SyncTimerS2CPayload> ID =
			new CustomPayload.Id<>(BingoMod_CS.id("sync_timer"));

	public static final PacketCodec<RegistryByteBuf, SyncTimerS2CPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_LONG,
			SyncTimerS2CPayload::remainingTicks,
			SyncTimerS2CPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
