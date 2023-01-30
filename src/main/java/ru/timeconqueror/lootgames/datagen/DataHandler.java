package ru.timeconqueror.lootgames.datagen;

import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataHandler {
    @SubscribeEvent
    public static void onDataEvent(GatherDataEvent event) {
        //FIXME port?
//        TimeLootTableGenerator lootTableGenerator = new TimeLootTableGenerator(event.getGenerator())
//                .addSet(new LGBlockLootTableSet());

        LGAdvancementSet advancementSet = new LGAdvancementSet();

        DataProvider.Factory<ForgeAdvancementProvider> advancementProviderFactory = output_ -> new ForgeAdvancementProvider(output_, event.getLookupProvider(), event.getExistingFileHelper(), List.of(advancementSet));
        event.getGenerator().addProvider(event.includeServer(), advancementProviderFactory);
    }
}
