package com.boyonk.musicsync.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

import java.io.IOException;

public class StopMusicS2CPacket implements Packet<ClientPlayPacketListener> {

	public StopMusicS2CPacket() {

	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {

	}

	@Override
	public void write(PacketByteBuf buf) {

	}

	@Override
	public void apply(ClientPlayPacketListener listener) {

	}
}
