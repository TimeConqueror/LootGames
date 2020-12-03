package ru.timeconqueror.lootgames;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.timecore.api.TimeMod;
import ru.timeconqueror.timecore.util.reflection.ReflectionHelper;

@Mod(LootGames.MODID)
public class LootGames implements TimeMod {
    public static final String MODID = "lootgames";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static LootGames INSTANCE = null;

    public LootGames() {
        INSTANCE = this;
    }

    private void onSetup(FMLCommonSetupEvent event) {
        ReflectionHelper.createClass(LGAdvancementTriggers.class.getName());
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }
}
