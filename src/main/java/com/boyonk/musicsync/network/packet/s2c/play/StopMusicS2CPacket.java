package com.boyonk.musicsync.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class StopMusicS2CPacket implements Packet<ClientPlayPacketListener> {

	public StopMusicS2CPacket() {

	}

	public StopMusicS2CPacket(PacketByteBuf buf) {

	}

	@Override
	public void write(PacketByteBuf buf) {

	}

	@Override
	public void apply(ClientPlayPacketListener listener) {

	}
}
