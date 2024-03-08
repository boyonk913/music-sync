package com.boyonk.musicsync.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public interface ClientMusicTracker {

	void play(SoundEvent event, long seed);

}
