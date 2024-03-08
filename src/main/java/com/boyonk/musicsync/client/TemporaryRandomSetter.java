package com.boyonk.musicsync.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

@Environment(EnvType.CLIENT)
public interface TemporaryRandomSetter {

	void setTemporaryRandom(Random random);
}
