package com.boyonk.musicsync.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public interface ClientMusicTracker {

	void play(RegistryEntry<SoundEvent> event, long seed);

}
