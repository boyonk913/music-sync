package com.boyonk.musicsync.client.mixin;

import com.boyonk.musicsync.client.TemporaryRandomSetter;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(AbstractSoundInstance.class)
public class MixinAbstractSoundInstance implements TemporaryRandomSetter {

	@Unique
	@Nullable
	private Random musicsync$tempRandom = null;

	@Override
	public void setTemporaryRandom(Random random) {
		this.musicsync$tempRandom = random;
	}

	@Inject(method = "getSoundSet", at = @At(value = "RETURN"))
	void musicsync$useTemporaryRandom(SoundManager soundManager, CallbackInfoReturnable<WeightedSoundSet> cir) {
		if (musicsync$tempRandom != null) ((TemporaryRandomSetter)cir.getReturnValue()).setTemporaryRandom(musicsync$tempRandom);
	}
}
