package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BlockSmartSubordinate;
import ru.timeconqueror.lootgames.common.LGItemGroup;
import ru.timeconqueror.lootgames.common.block.*;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.BlockPropsFactory;
import ru.timeconqueror.timecore.registry.newreg.BlockRegister;
import ru.timeconqueror.timecore.util.Hacks;

import static ru.timeconqueror.lootgames.common.block.BlockDungeon.BRICKS_PROPS_CREATOR;
import static ru.timeconqueror.lootgames.common.block.BlockDungeon.LAMP_PROPS_CREATOR;
import static ru.timeconqueror.timecore.util.Hacks.promise;

@ObjectHolder(LootGames.MODID)
public class LGBlocks {
    public static final BlockDungeon DUNGEON_CEILING = promise();
    public static final BlockDungeon CRACKED_DUNGEON_CEILING = promise();
    public static final BlockDungeon DUNGEON_WALL = promise();
    public static final BlockDungeon CRACKED_DUNGEON_WALL = promise();
    public static final BlockDungeon DUNGEON_FLOOR = promise();
    public static final BlockDungeon CRACKED_DUNGEON_FLOOR = promise();
    public static final BlockDungeon SHIELDED_DUNGEON_FLOOR = promise();

    public static final BlockDungeon DUNGEON_LAMP = promise();
    public static final BlockDungeon BROKEN_DUNGEON_LAMP = promise();

    public static final BlockPuzzleMaster PUZZLE_MASTER = promise();
    public static final BlockSmartSubordinate SMART_SUBORDINATE = promise();

    public static final BlockMSActivator MS_ACTIVATOR = promise();
    public static final BlockMSMaster MS_MASTER = promise();
    public static final BlockPipesActivator PIPES_ACTIVATOR = promise();
    public static final BlockPipesMaster PIPES_MASTER = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(LootGames.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.register("dungeon_ceiling", () -> new BlockDungeon(BRICKS_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("cracked_dungeon_ceiling", () -> new BlockDungeon(BRICKS_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("dungeon_wall", () -> new BlockDungeon(BRICKS_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("cracked_dungeon_wall", () -> new BlockDungeon(BRICKS_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("dungeon_floor", () -> new BlockDungeon(BRICKS_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("cracked_dungeon_floor", () -> new BlockDungeon(BRICKS_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("shielded_dungeon_floor", () -> new BlockDungeon(BlockPropsFactory.unbreakable(BRICKS_PROPS_CREATOR.create()))).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("dungeon_lamp", () -> new BlockDungeon(BlockPropsFactory.unbreakable(LAMP_PROPS_CREATOR.create()))).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("broken_dungeon_lamp", () -> new BlockDungeon(BlockPropsFactory.unbreakable(LAMP_PROPS_CREATOR.create().lightLevel(value -> 0)))).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("puzzle_master", BlockPuzzleMaster::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);

            TextureLocation shieldedDungeonFloorText = new TextureLocation(REGISTER.getModId(), "block/shielded_dungeon_floor");
            REGISTER.register("smart_subordinate", BlockSmartSubordinate::new).genDefaultStateAndModel(shieldedDungeonFloorText).regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("ms_activator", BlockMSActivator::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("ms_master", BlockMSMaster::new).genDefaultStateAndModel(shieldedDungeonFloorText).regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("pipes_activator", BlockPipesActivator::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("pipes_master", BlockPipesMaster::new).genDefaultStateAndModel(shieldedDungeonFloorText).regDefaultBlockItem(LGItemGroup.MAIN);
        }
    }
}
