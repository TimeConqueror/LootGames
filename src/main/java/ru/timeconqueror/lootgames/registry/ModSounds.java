package ru.timeconqueror.lootgames.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.proxy.CommonProxy;

public class ModSounds {
    public static SoundEvent golConstructGame;
    public static SoundEvent golSequenceWrong;

    public static void registerSounds() {
        golConstructGame = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MODID, "gol_construct_game"));
        golSequenceWrong = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MODID, "gol_sequence_wrong"));
    }
}
