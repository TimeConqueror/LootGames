package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.LGCreativeTabs;
import ru.timeconqueror.lootgames.common.item.BoxedMicroverseItem;
import ru.timeconqueror.timecore.api.registry.ItemRegister;
import ru.timeconqueror.timecore.api.registry.ItemRegister.CreativeTabAdder;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

@AutoRegistrable.Entries("item")
public class LGItems {
    @AutoRegistrable
    private static final ItemRegister REGISTER = new ItemRegister(LootGames.MODID);

    public static BoxedMicroverseItem BOXED_UNKNOWN_MICROVERSE;
    public static BoxedMicroverseItem BOXED_GAME_OF_LIGHT_MICROVERSE;
    public static BoxedMicroverseItem BOXED_MINESWEEPER_MICROVERSE;

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.register("boxed_unknown_microverse", BoxedMicroverseItem::new)
                .name("Boxed Microverse")
                .onCreativeTabBuilding(CreativeTabAdder.tabBased(LGCreativeTabs.MAIN));
        REGISTER.register("boxed_game_of_light_microverse", BoxedMicroverseItem::new)
                .name("Boxed Microverse")
                .onCreativeTabBuilding(CreativeTabAdder.tabBased(LGCreativeTabs.MAIN));
        REGISTER.register("boxed_minesweeper_microverse", BoxedMicroverseItem::new)
                .name("Boxed Microverse")
                .onCreativeTabBuilding(CreativeTabAdder.tabBased(LGCreativeTabs.MAIN));
    }
}
