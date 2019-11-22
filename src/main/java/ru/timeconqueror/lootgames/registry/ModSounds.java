package ru.timeconqueror.lootgames.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.proxy.CommonProxy;

public class ModSounds {
    public static SoundEvent golStartGame;
    public static SoundEvent golSequenceWrong;
    public static SoundEvent golSequenceComplete;
    public static SoundEvent golGameWin;
    public static SoundEvent golGameLose;

    public static SoundEvent puzzleMasterStrange;

    public static void registerSounds() {
        golStartGame = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MOD_ID, "gol_start_game"));
        golSequenceWrong = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MOD_ID, "gol_sequence_wrong"));
        golSequenceComplete = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MOD_ID, "gol_sequence_complete"));
        golGameWin = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MOD_ID, "gol_gameover_win"));
        golGameLose = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MOD_ID, "gol_gameover_lose"));

        puzzleMasterStrange = CommonProxy.REGISTRY.registerSound(new ResourceLocation(LootGames.MOD_ID, "puzzle_master_strange"));
    }
}
