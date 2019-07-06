package ru.timeconqueror.lootgames.proxy;

import com.timeconqueror.timecore.registry.EasyRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.ModCreativeTab;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.registry.ModItems;

public class CommonProxy {
    public static EasyRegistry registry = new EasyRegistry(LootGames.MODID, ModCreativeTab.lootGames);

    public void preInit(FMLPreInitializationEvent event) {
        ModItems.register();
        ModBlocks.register();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }
}
