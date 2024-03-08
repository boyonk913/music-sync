package com.boyonk.musicsync.mixin;

import com.boyonk.musicsync.ServerMusicTracker;
import com.boyonk.musicsync.ServerMusicTrackerHolder;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements ServerMusicTrackerHolder {

	@Unique
	ServerMusicTracker musicsync$musicTracker;

	@Inject(method = "<init>", at = @At("TAIL"))
	void musicsync$createMusicTracker(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
		this.musicsync$musicTracker = new ServerMusicTracker((MinecraftServer) (Object) this);
	}

	@Override
	public ServerMusicTracker getMusicTracker() {
		return this.musicsync$musicTracker;
	}
}
