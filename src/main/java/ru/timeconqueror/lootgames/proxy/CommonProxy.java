package ru.timeconqueror.lootgames.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.ModCreativeTab;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.registry.ModItems;
import ru.timeconqueror.lootgames.world.gen.LootGamesWorldGen;
import ru.timeconqueror.timecore.registry.EasyRegistry;

public class CommonProxy {
    public static EasyRegistry registry = new EasyRegistry(LootGames.MODID, ModCreativeTab.lootGames);

    public void preInit(FMLPreInitializationEvent event) {
        ModItems.register();
        ModBlocks.register();
    }

    public void init(FMLInitializationEvent event) {
        LootGamesConfig.initExtras();
        GameRegistry.registerWorldGenerator(new LootGamesWorldGen(), Integer.MAX_VALUE);
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
