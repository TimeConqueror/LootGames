package ru.timeconqueror.lootgames.datagen;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import ru.timeconqueror.timecore.api.devtools.gen.advancement.TimeAdvancementGenerator;
import ru.timeconqueror.timecore.api.devtools.gen.loottable.TimeLootTableGenerator;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataHandler {
    @SubscribeEvent
    public static void onDataEvent(GatherDataEvent event) {
        TimeLootTableGenerator lootTableGenerator = new TimeLootTableGenerator(event.getGenerator())
                .addSet(new LGBlockLootTableSet());

        TimeAdvancementGenerator advancementGenerator = new TimeAdvancementGenerator(event.getGenerator()).
                addSet(new LGAdvancementSet());

        event.getGenerator().addProvider(lootTableGenerator);
        event.getGenerator().addProvider(advancementGenerator);
    }
}
