package ru.timeconqueror.lootgames.datagen;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import ru.timeconqueror.timecore.api.datagen.DataGen;
import ru.timeconqueror.timecore.api.datagen.loottable.LootTableSet;
import ru.timeconqueror.timecore.api.datagen.loottable.TimeLootTableProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataHandler {
    @SubscribeEvent
    public static void onDataEvent(GatherDataEvent event) {
        DataGen.disableFileDeletion = true;

        TimeLootTableProvider lootTableProvider = new TimeLootTableProvider(event.getGenerator());

        LootTableSet set = new LGBlockLootTableSet();

        lootTableProvider.addLootTableSet(set);

        event.getGenerator().addProvider(lootTableProvider);
    }
}
