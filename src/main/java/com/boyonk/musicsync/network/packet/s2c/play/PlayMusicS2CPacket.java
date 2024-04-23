package com.boyonk.musicsync.network.packet.s2c.play;

import com.boyonk.musicsync.MusicSync;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record PlayMusicS2CPacket(RegistryEntry<SoundEvent> sound, long seed) implements CustomPayload {

	public static final Id<PlayMusicS2CPacket> ID = new Id<>(new Identifier(MusicSync.NAMESPACE, "play_music"));
	public static final PacketCodec<RegistryByteBuf, PlayMusicS2CPacket> CODEC = PacketCodec.tuple(
			SoundEvent.ENTRY_PACKET_CODEC, PlayMusicS2CPacket::sound,
			PacketCodecs.VAR_LONG, PlayMusicS2CPacket::seed,
			PlayMusicS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
