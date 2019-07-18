package ru.timeconqueror.lootgames.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.timeconqueror.lootgames.CreativeTabMod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.minigame.GameManager;
import ru.timeconqueror.lootgames.minigame.gameoflight.GameOfLight;
import ru.timeconqueror.lootgames.packets.NetworkHandler;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.registry.ModSounds;
import ru.timeconqueror.lootgames.world.gen.LootGamesWorldGen;
import ru.timeconqueror.timecore.registry.EasyRegistry;

public class CommonProxy {
    public static final EasyRegistry REGISTRY = new EasyRegistry(LootGames.MODID, CreativeTabMod.TAB_LOOTGAMES);

    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.register();
        ModBlocks.registerTileEntites();

        NetworkHandler.registerPackets();
    }

    public void init(FMLInitializationEvent event) {
        LootGamesConfig.initExtras();
        GameRegistry.registerWorldGenerator(new LootGamesWorldGen(), Integer.MAX_VALUE);

        LootGames.gameManager = new GameManager();
        LootGames.gameManager.registerGame(new GameOfLight());
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModSounds.registerSounds();
    }
}
