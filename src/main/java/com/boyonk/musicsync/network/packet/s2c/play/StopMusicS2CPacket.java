package com.boyonk.musicsync.network.packet.s2c.play;

import com.boyonk.musicsync.MusicSync;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StopMusicS2CPacket() implements CustomPayload {

	public static final Id<StopMusicS2CPacket> ID = new Id<>(new Identifier(MusicSync.NAMESPACE, "stop_music"));
	public static final PacketCodec<RegistryByteBuf, StopMusicS2CPacket> CODEC = PacketCodec.unit(new StopMusicS2CPacket());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
