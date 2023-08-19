package ru.timeconqueror.lootgames.registry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BoardBorderBlock;
import ru.timeconqueror.lootgames.client.resource.BoardBorderBlockResources;
import ru.timeconqueror.lootgames.common.LGCreativeTabs;
import ru.timeconqueror.lootgames.common.block.DungeonBlock;
import ru.timeconqueror.lootgames.common.block.MinesweeperTechnicalBlock;
import ru.timeconqueror.lootgames.common.block.PuzzleMasterBlock;
import ru.timeconqueror.lootgames.common.block.TechnicalBlock;
import ru.timeconqueror.lootgames.common.block.TechnicalBlock.Configuration;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.BlockRegister.RenderTypeWrapper;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

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
    public static BoardBorderBlock BOARD_BORDER;

//    public static MSActivatorBlock MS_ACTIVATOR;
//    public static PipesActivatorBlock PIPES_ACTIVATOR;
//    public static GOLActivatorBlock GOL_ACTIVATOR;
//    public static GameMasterBlock MS_MASTER;
//    public static GameMasterBlock PIPES_MASTER;
//    public static GameMasterBlock GOL_MASTER;

    public static TechnicalBlock SPACE_FABRIC; // hard wall
    public static TechnicalBlock GLASSY_MATTER; // just a glass block
    public static MinesweeperTechnicalBlock MINESWEEPER_GLASSY_MATTER;

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

        REGISTER.register("board_border", BoardBorderBlock::new).also(BoardBorderBlockResources::fillChain).name("Board Border");
        REGISTER.register("space_fabric", () -> new TechnicalBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER), Configuration.builder().build())).name("Space Fabric");

        REGISTER.register("glassy_matter", () -> new TechnicalBlock(
                        BlockBehaviour.Properties.copy(Blocks.BARRIER),
                        Configuration.builder().renderShape(RenderShape.INVISIBLE).build()))
                .renderLayer(() -> new RenderTypeWrapper(RenderType.translucent())).name("Glassy Matter");
        REGISTER.register("minesweeper_glassy_matter", () -> new MinesweeperTechnicalBlock(
                        BlockBehaviour.Properties.copy(Blocks.BARRIER),
                        Configuration.builder().renderShape(RenderShape.INVISIBLE).build()))
                .renderLayer(() -> new RenderTypeWrapper(RenderType.translucent())).name("Glassy Matter");

//        REGISTER.register("gol_activator", GOLActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Game of Light");
//        REGISTER.register("ms_activator", MSActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Minesweeper");
//        REGISTER.register("pipes_activator", PipesActivatorBlock::new).oneVarStateAndCubeAllModel().defaultBlockItem(LGCreativeTabs.MAIN).name("Pipes (WIP)");

//        REGISTER.register("gol_master", gameMaster(() -> LGBlockEntities.GOL_MASTER)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Game of Light");
//        REGISTER.register("ms_master", gameMaster(() -> LGBlockEntities.MS_MASTER)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Minesweeper");
//        REGISTER.register("pipes_master", gameMaster(() -> LGBlockEntities.PIPES_MASTER)).oneVarStateAndCubeAllModel(shieldedDungeonFloorText).name("Pipes");
    }
}
