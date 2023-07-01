package ru.timeconqueror.lootgames.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.devtools.gen.advancement.DataGeneration;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataHandler {
    @SubscribeEvent
    public static void onDataEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        //FIXME port?
//        TimeLootTableGenerator lootTableGenerator = new TimeLootTableGenerator(event.getGenerator())
//                .addSet(new LGBlockLootTableSet());

        gen.addProvider(event.includeServer(), DataGeneration.advancementProvider(event, new LGAdvancementGenerator()));
        gen.addProvider(event.includeServer(), DataGeneration.factory(output_ -> new LGBlockTagsProvider(output_, lookupProvider, LootGames.MODID, fileHelper)));
        gen.addProvider(event.includeClient(), DataGeneration.factory(LGBlockStateAndBlockProvider::new));
    }
}
