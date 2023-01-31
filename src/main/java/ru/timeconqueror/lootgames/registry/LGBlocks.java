package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BoardBorderBlock;
import ru.timeconqueror.lootgames.api.block.GameMasterBlock;
import ru.timeconqueror.lootgames.api.block.SmartSubordinateBlock;
import ru.timeconqueror.lootgames.api.block.tile.GameMasterTile;
import ru.timeconqueror.lootgames.client.resource.BoardBorderBlockResources;
import ru.timeconqueror.lootgames.common.LGCreativeTabs;
import ru.timeconqueror.lootgames.common.block.*;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

import java.util.function.Supplier;

import static ru.timeconqueror.lootgames.common.block.DungeonBlock.BRICKS_PROPS_CREATOR;
import static ru.timeconqueror.lootgames.common.block.DungeonBlock.LAMP_PROPS_CREATOR;

@AutoRegistrable.Entries("block")
public class LGBlocks {
    public static DungeonBlock DUNGEON_CEILING;
    public static DungeonBlock CRACKED_DUNGEON_CEILING;
    public static DungeonBlock DUNGEON_WALL;
    public static DungeonBlock CRACKED_DUNGEON_WALL;
    public static DungeonBlock DUNGEON_FLOOR;
    public static DungeonBlock CRACKED_DUNGEON_FLOOR;
    public static DungeonBlock SHIELDED_DUNGEON_FLOOR;

    public static DungeonBlock DUNGEON_LAMP;
    public static DungeonBlock BROKEN_DUNGEON_LAMP;

    public static PuzzleMasterBlock PUZZLE_MASTER;
    public static SmartSubordinateBlock SMART_SUBORDINATE;
    public static BoardBorderBlock BOARD_BORDER;

    public static MSActivatorBlock MS_ACTIVATOR;
    public static PipesActivatorBlock PIPES_ACTIVATOR;
    public static GOLActivatorBlock GOL_ACTIVATOR;
    public static GameMasterBlock MS_MASTER;
    public static GameMasterBlock PIPES_MASTER;
    public static GameMasterBlock GOL_MASTER;

    @AutoRegistrable
    private static final BlockRegister REGISTER = new BlockRegister(LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.register("dungeon_ceiling", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Dungeon Ceiling");
        REGISTER.register("cracked_dungeon_ceiling", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Cracked Dungeon Ceiling");
        REGISTER.register("dungeon_wall", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Dungeon Wall");
        REGISTER.register("cracked_dungeon_wall", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Cracked Dungeon Wall");
        REGISTER.register("dungeon_floor", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Dungeon Floor");
        REGISTER.register("cracked_dungeon_floor", () -> new DungeonBlock(BRICKS_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Cracked Dungeon Floor");
        REGISTER.register("shielded_dungeon_floor", () -> new DungeonBlock(BlockPropsFactory.unbreakable(BRICKS_PROPS_CREATOR.create()))).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Shielded Dungeon Floor");
        REGISTER.register("dungeon_lamp", () -> new DungeonBlock(LAMP_PROPS_CREATOR.create())).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Dungeon Lamp");
        REGISTER.register("broken_dungeon_lamp", () -> new DungeonBlock(LAMP_PROPS_CREATOR.create().lightLevel(value -> 0))).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Broken Dungeon Lamp");
        REGISTER.register("puzzle_master", PuzzleMasterBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Puzzle Master");

        TextureLocation shieldedDungeonFloorText = new TextureLocation(REGISTER.getModId(), "block/shielded_dungeon_floor");

        REGISTER.register("smart_subordinate", SmartSubordinateBlock::new).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Smart Subordinate");
        REGISTER.register("board_border", BoardBorderBlock::new).also(BoardBorderBlockResources::fillChain).name("Board Border");

        REGISTER.register("gol_activator", GOLActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Game of Light");
        REGISTER.register("ms_activator", MSActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Minesweeper");
        REGISTER.register("pipes_activator", PipesActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Pipes (WIP)");

        REGISTER.register("gol_master", gameMaster(() -> LGBlockEntities.GOL_MASTER)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Game of Light");
        REGISTER.register("ms_master", gameMaster(() -> LGBlockEntities.MS_MASTER)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Minesweeper");
        REGISTER.register("pipes_master", gameMaster(() -> LGBlockEntities.PIPES_MASTER)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Pipes");
    }

    @SuppressWarnings("CodeBlock2Expr")
    private static Supplier<GameMasterBlock> gameMaster(Supplier<BlockEntityType<? extends GameMasterTile<?>>> typeSupplier) {
        return () -> {
            return new GameMasterBlock(typeSupplier);
        };
    }
}
