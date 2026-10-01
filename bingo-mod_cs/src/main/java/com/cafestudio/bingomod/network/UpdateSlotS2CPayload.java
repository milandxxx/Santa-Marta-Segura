package com.cafestudio.bingomod.network;

import com.cafestudio.bingomod.BingoMod_CS;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record UpdateSlotS2CPayload(int row, int col, boolean completed) implements CustomPayload {
	public static final CustomPayload.Id<UpdateSlotS2CPayload> ID =
			new CustomPayload.Id<>(BingoMod_CS.id("update_slot"));

	public static final PacketCodec<RegistryByteBuf, UpdateSlotS2CPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT,
			UpdateSlotS2CPayload::row,
			PacketCodecs.VAR_INT,
			UpdateSlotS2CPayload::col,
			PacketCodecs.BOOL,
			UpdateSlotS2CPayload::completed,
			UpdateSlotS2CPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
