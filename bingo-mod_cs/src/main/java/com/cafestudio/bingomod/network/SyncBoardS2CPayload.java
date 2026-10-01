package com.cafestudio.bingomod.network;

import java.util.List;

import com.cafestudio.bingomod.BingoMod_CS;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncBoardS2CPayload(List<Identifier> itemIds) implements CustomPayload {
	public static final CustomPayload.Id<SyncBoardS2CPayload> ID =
			new CustomPayload.Id<>(BingoMod_CS.id("sync_board"));

	public static final PacketCodec<RegistryByteBuf, SyncBoardS2CPayload> CODEC = PacketCodec.tuple(
			Identifier.PACKET_CODEC.collect(PacketCodecs.toList()),
			SyncBoardS2CPayload::itemIds,
			SyncBoardS2CPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
