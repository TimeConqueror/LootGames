package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BlockFieldBorder;
import ru.timeconqueror.lootgames.api.block.BlockGameMaster;
import ru.timeconqueror.lootgames.api.block.BlockSmartSubordinate;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.client.resource.FieldBorderBlockResources;
import ru.timeconqueror.lootgames.common.LGItemGroup;
import ru.timeconqueror.lootgames.common.block.*;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPipesMaster;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.BlockPropsFactory;
import ru.timeconqueror.timecore.registry.newreg.BlockRegister;

import java.util.function.Supplier;

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
    public static final BlockFieldBorder FIELD_BORDER = promise();

    public static final BlockMSActivator MS_ACTIVATOR = promise();
    public static final BlockGameMaster MS_MASTER = promise();
    public static final BlockPipesActivator PIPES_ACTIVATOR = promise();
    public static final BlockGameMaster PIPES_MASTER = promise();

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
            REGISTER.register("dungeon_lamp", () -> new BlockDungeon(LAMP_PROPS_CREATOR.create())).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("broken_dungeon_lamp", () -> new BlockDungeon(LAMP_PROPS_CREATOR.create().lightLevel(value -> 0))).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("puzzle_master", BlockPuzzleMaster::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);

            TextureLocation shieldedDungeonFloorText = new TextureLocation(REGISTER.getModId(), "block/shielded_dungeon_floor");

            REGISTER.register("smart_subordinate", BlockSmartSubordinate::new).genDefaultStateAndModel(shieldedDungeonFloorText);
            REGISTER.register("field_border", BlockFieldBorder::new).also(FieldBorderBlockResources::fillChain).regDefaultBlockItem(LGItemGroup.MAIN);

            REGISTER.register("ms_activator", BlockMSActivator::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("pipes_activator", BlockPipesActivator::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("gol_activator", BlockGOLActivator::new).genDefaultStateAndModel().regDefaultBlockItem(LGItemGroup.MAIN);

            REGISTER.register("ms_master", gameMaster(TileEntityMSMaster::new)).genDefaultStateAndModel(shieldedDungeonFloorText).regDefaultBlockItem(LGItemGroup.MAIN);
            REGISTER.register("pipes_master", gameMaster(TileEntityPipesMaster::new)).genDefaultStateAndModel(shieldedDungeonFloorText).regDefaultBlockItem(LGItemGroup.MAIN);
        }

        @SuppressWarnings("CodeBlock2Expr")
        private static Supplier<BlockGameMaster> gameMaster(Supplier<TileEntityGameMaster<?>> tileEntityFactory) {
            return () -> {
                return new BlockGameMaster((blockState, world) -> tileEntityFactory.get());
            };
        }
    }
}
