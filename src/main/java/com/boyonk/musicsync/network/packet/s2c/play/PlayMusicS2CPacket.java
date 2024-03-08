package com.boyonk.musicsync.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class PlayMusicS2CPacket implements Packet<ClientPlayPacketListener> {

	@Nullable
	private SoundEvent sound;
	private long seed;

	public PlayMusicS2CPacket(@Nullable SoundEvent sound, long seed) {
		this.sound = sound;
		this.seed = seed;
	}

	public PlayMusicS2CPacket(PacketByteBuf buf) {
		if (buf.readBoolean()) {
			this.sound = Registry.SOUND_EVENT.get(buf.readIdentifier());
		}
		this.seed = buf.readVarLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		if (this.sound != null) {
			buf.writeBoolean(true);
			buf.writeIdentifier(this.sound.getId());
		} else {
			buf.writeBoolean(false);
		}
		buf.writeVarLong(this.seed);
	}

	@Nullable
	public SoundEvent getSound() {
		return sound;
	}

	public long getSeed() {
		return seed;
	}

	@Override
	public void apply(ClientPlayPacketListener listener) {

	}
}
