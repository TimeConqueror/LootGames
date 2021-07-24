package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BoardBorderBlock;
import ru.timeconqueror.lootgames.api.block.GameMasterBlock;
import ru.timeconqueror.lootgames.api.block.SmartSubordinateBlock;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.lootgames.client.resource.BoardBorderBlockResources;
import ru.timeconqueror.lootgames.common.LGItemGroup;
import ru.timeconqueror.lootgames.common.block.*;
import ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile;
import ru.timeconqueror.lootgames.common.block.tile.MSMasterTile;
import ru.timeconqueror.lootgames.common.block.tile.PipesMasterTile;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

import java.util.function.Supplier;

import static ru.timeconqueror.lootgames.common.block.DungeonBlock.BRICKS_PROPS_CREATOR;
import static ru.timeconqueror.lootgames.common.block.DungeonBlock.LAMP_PROPS_CREATOR;
import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@ObjectHolder(LootGames.MODID)
public class LGBlocks {
    public static final DungeonBlock DUNGEON_CEILING = promise();
    public static final DungeonBlock CRACKED_DUNGEON_CEILING = promise();
    public static final DungeonBlock DUNGEON_WALL = promise();
    public static final DungeonBlock CRACKED_DUNGEON_WALL = promise();
    public static final DungeonBlock DUNGEON_FLOOR = promise();
    public static final DungeonBlock CRACKED_DUNGEON_FLOOR = promise();
    public static final DungeonBlock SHIELDED_DUNGEON_FLOOR = promise();

    public static final DungeonBlock DUNGEON_LAMP = promise();
    public static final DungeonBlock BROKEN_DUNGEON_LAMP = promise();

    public static final PuzzleMasterBlock PUZZLE_MASTER = promise();
    public static final SmartSubordinateBlock SMART_SUBORDINATE = promise();
    public static final BoardBorderBlock BOARD_BORDER = promise();

    public static final MSActivatorBlock MS_ACTIVATOR = promise();
    public static final PipesActivatorBlock PIPES_ACTIVATOR = promise();
    public static final GOLActivatorBlock GOL_ACTIVATOR = promise();
    public static final GameMasterBlock MS_MASTER = promise();
    public static final GameMasterBlock PIPES_MASTER = promise();
    public static final GameMasterBlock GOL_MASTER = promise();

    private static class Init {
        @AutoRegistrable
        private static final BlockRegister REGISTER = new BlockRegister(LootGames.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.register("dungeon_ceiling", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Dungeon Ceiling");
            REGISTER.register("cracked_dungeon_ceiling", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Cracked Dungeon Ceiling");
            REGISTER.register("dungeon_wall", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Dungeon Wall");
            REGISTER.register("cracked_dungeon_wall", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Cracked Dungeon Wall");
            REGISTER.register("dungeon_floor", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Dungeon Floor");
            REGISTER.register("cracked_dungeon_floor", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Cracked Dungeon Floor");
            REGISTER.register("shielded_dungeon_floor", () -> new DungeonBlock(BlockPropsFactory.unbreakable(BRICKS_PROPS_CREATOR.create()))).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Shielded Dungeon Floor");
            REGISTER.register("dungeon_lamp", () -> new DungeonBlock(LAMP_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Dungeon Lamp");
            REGISTER.register("broken_dungeon_lamp", () -> new DungeonBlock(LAMP_PROPS_CREATOR.create().lightLevel(value -> 0))).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Broken Dungeon Lamp");
            REGISTER.register("puzzle_master", PuzzleMasterBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Puzzle Master");

            TextureLocation shieldedDungeonFloorText = new TextureLocation(REGISTER.getModId(), "block/shielded_dungeon_floor");

            REGISTER.register("smart_subordinate", SmartSubordinateBlock::new).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Smart Subordinate");
            REGISTER.register("board_border", BoardBorderBlock::new).also(BoardBorderBlockResources::fillChain).name("Board Border");

            REGISTER.register("gol_activator", GOLActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Game of Light");
            REGISTER.register("ms_activator", MSActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Minesweeper");
            REGISTER.register("pipes_activator", PipesActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGItemGroup.MAIN).name("Pipes (WIP)");

            REGISTER.register("gol_master", gameMaster(GOLMasterTile::new)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Game of Light");
            REGISTER.register("ms_master", gameMaster(MSMasterTile::new)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Minesweeper");
            REGISTER.register("pipes_master", gameMaster(PipesMasterTile::new)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Pipes");
        }

        @SuppressWarnings("CodeBlock2Expr")
        private static Supplier<GameMasterBlock> gameMaster(Supplier<GameMasterTile<?>> tileEntityFactory) {
            return () -> {
                return new GameMasterBlock((blockState, world) -> tileEntityFactory.get());
            };
        }
    }
}
