package com.boyonk.musicsync.network.packet.s2c.play;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class PlayMusicS2CPacket implements Packet<ClientPlayPacketListener> {

	@Nullable
	private RegistryEntry<SoundEvent> sound;
	private long seed;

	public PlayMusicS2CPacket(@Nullable RegistryEntry<SoundEvent> sound, long seed) {
		this.sound = sound;
		this.seed = seed;
	}

	public PlayMusicS2CPacket(PacketByteBuf buf) {
		if (buf.readBoolean()) {
			this.sound = buf.readRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), SoundEvent::fromBuf);
		}
		this.seed = buf.readVarLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		if (this.sound != null) {
			buf.writeBoolean(true);
			buf.writeRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), this.sound, (b, sound) -> sound.writeBuf(b));
		} else {
			buf.writeBoolean(false);
		}
		buf.writeVarLong(this.seed);
	}

	@Nullable
	public RegistryEntry<SoundEvent> getSound() {
		return sound;
	}

	public long getSeed() {
		return seed;
	}

	@Override
	public void apply(ClientPlayPacketListener listener) {

	}
}
