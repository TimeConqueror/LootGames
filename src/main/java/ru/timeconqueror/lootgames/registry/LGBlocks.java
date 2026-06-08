package ru.timeconqueror.lootgames.registry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.LGCreativeTabs;
import ru.timeconqueror.lootgames.common.block.*;
import ru.timeconqueror.lootgames.common.block.TechnicalBlock.Configuration;
import ru.timeconqueror.lootgames.manager.BlockCycleManager;
import ru.timeconqueror.lootgames.common.item.MicroverseManipulatorItem;
import ru.timeconqueror.lootgames.common.item.TransferenceCrystalItem;
import ru.timeconqueror.lootgames.registry.custom.BlockVariations;
import ru.timeconqueror.timecore.api.registry.BlockRegister;
import ru.timeconqueror.timecore.api.registry.BlockRegister.RenderTypeWrapper;
import ru.timeconqueror.timecore.api.registry.ItemRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;

import java.util.ArrayList;
import java.util.List;

import static ru.timeconqueror.lootgames.common.block.DungeonBlock.BLOCK_PROPS_FACTORY;
import static ru.timeconqueror.lootgames.common.block.DungeonBlock.GLOWING_BLOCK_PROPS_CREATOR;

@AutoRegistrable.Entries("block")
public class LGBlocks {
    public static PuzzleMasterBlock PUZZLE_MASTER;

//    public static MSActivatorBlock MS_ACTIVATOR;
//    public static PipesActivatorBlock PIPES_ACTIVATOR;
//    public static GOLActivatorBlock GOL_ACTIVATOR;
//    public static GameMasterBlock MS_MASTER;
//    public static GameMasterBlock PIPES_MASTER;
//    public static GameMasterBlock GOL_MASTER;

    public static TechnicalBlock GLASSY_MATTER;
    public static BoardTechnicalBlock BOARD_GLASSY_MATTER;
    public static BoardTechnicalBlock BOARD_OPAQUE_MATTER;

    public static TechnicalBlock SPACE_FABRIC; // hard wall
    public static MicroverseManipulatorBlock MICROVERSE_MANIPULATOR;
    public static TransferenceCrystalBlock TRANSFERENCE_CRYSTAL;

    public static class Variations {
        public static BlockVariations<DungeonBlock> RIBBED_XENOLITH;
        public static BlockVariations<DungeonBlock> RIBBED_XENOLITH_90;
        public static BlockVariations<DungeonBlock> RIBBED_XENOLITH_180;
        public static BlockVariations<DungeonBlock> RIBBED_XENOLITH_270;
        public static BlockVariations<DungeonBlock> WIDE_RIBBED_XENOLITH;
        public static BlockVariations<DungeonBlock> WIDE_RIBBED_XENOLITH_90;
        public static BlockVariations<DungeonBlock> WIDE_RIBBED_XENOLITH_180;
        public static BlockVariations<DungeonBlock> WIDE_RIBBED_XENOLITH_270;
        public static BlockVariations<DungeonBlock> CHISELED_XENOLITH;
        public static BlockVariations<DungeonBlock> FRAMED_XENOLITH;
        public static BlockVariations<DungeonBlock> TILED_XENOLITH;
        public static BlockVariations<DungeonBlock> TILED_XENOLITH_90;
        public static BlockVariations<DungeonBlock> TILED_XENOLITH_180;
        public static BlockVariations<DungeonBlock> TILED_XENOLITH_270;
        public static BlockVariations<DungeonBlock> WAVED_XENOLITH;
        public static BlockVariations<DungeonBlock> WAVED_XENOLITH_90;
        public static BlockVariations<DungeonBlock> CHEVRON_XENOLITH;
        public static BlockVariations<DungeonBlock> CHEVRON_XENOLITH_90;
        public static BlockVariations<DungeonBlock> CHEVRON_XENOLITH_180;
        public static BlockVariations<DungeonBlock> CHEVRON_XENOLITH_270;
        public static BlockVariations<DungeonBlock> FIBROUS_XENOLITH;
        public static BlockVariations<DungeonBlock> FIBROUS_XENOLITH_90;
        public static BlockVariations<DungeonBlock> FIBROUS_XENOLITH_180;
        public static BlockVariations<DungeonBlock> FIBROUS_XENOLITH_270;
    }

    public static DungeonBlock GLOWING_CHISELED_XENOLITH;
    public static DungeonBlock GLOWING_FRAMED_XENOLITH;
    public static DungeonBlock GLOWING_WAVED_XENOLITH;
    public static DungeonBlock GLOWING_CHEVRON_XENOLITH;
    public static DungeonBlock GLOWING_FIBROUS_XENOLITH;

    @AutoRegistrable.Ignore
    public static List<BlockVariations<DungeonBlock>> COMMON_XENOLITHS;

    @AutoRegistrable
    private static final BlockRegister REGISTER = new BlockRegister(LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.register("puzzle_master", PuzzleMasterBlock::new).oneVarStateAndCubeAllModel().blockItemDefaultModel(LGCreativeTabs.MAIN).name("Puzzle Master");
        REGISTER.register("space_fabric", () -> new TechnicalBlock(BlockBehaviour.Properties.copy(Blocks.BARRIER), Configuration.builder().build())).name("Space Fabric");
        REGISTER.register("glassy_matter", () -> new TechnicalBlock(
                        BlockBehaviour.Properties.copy(Blocks.BARRIER),
                        Configuration.builder().renderShape(RenderShape.INVISIBLE).build()))
                .renderLayer(() -> new RenderTypeWrapper(RenderType.translucent())).name("Glassy Matter");
        REGISTER.register("board_glassy_matter", () -> new BoardTechnicalBlock(
                        BlockBehaviour.Properties.copy(Blocks.BARRIER),
                        Configuration.builder().renderShape(RenderShape.INVISIBLE).build()))
                .renderLayer(() -> new RenderTypeWrapper(RenderType.translucent())).name("Glassy Matter");
        REGISTER.register("board_opaque_matter", () -> new BoardTechnicalBlock(
                BlockBehaviour.Properties.copy(Blocks.BARRIER),
                Configuration.builder().build())
        ).blockItemDefaultModel(LGCreativeTabs.MAIN).name("Opaque Matter");

        List<BlockVariations<DungeonBlock>> xenoliths = new ArrayList<>();
        Variations.RIBBED_XENOLITH = regXenolithVariations("ribbed_xenolith", "Ribbed Xenolith", xenoliths);
        Variations.WIDE_RIBBED_XENOLITH = regXenolithVariations("wide_ribbed_xenolith", "Wide Ribbed Xenolith", xenoliths);
        Variations.CHISELED_XENOLITH = regXenolithVariations("chiseled_xenolith", "Chiseled Xenolith", xenoliths);
        Variations.FRAMED_XENOLITH = regXenolithVariations("framed_xenolith", "Framed Xenolith", xenoliths);
        Variations.TILED_XENOLITH = regXenolithVariations("tiled_xenolith", "Tiled Xenolith", xenoliths);
        Variations.WAVED_XENOLITH = regXenolithVariations("waved_xenolith", "Waved Xenolith", xenoliths);
        Variations.CHEVRON_XENOLITH = regXenolithVariations("chevron_xenolith", "Chevron Xenolith", xenoliths);
        Variations.FIBROUS_XENOLITH = regXenolithVariations("fibrous_xenolith", "Fibrous Xenolith", xenoliths);

        Variations.RIBBED_XENOLITH_90 = regXenolithVariations("ribbed_xenolith_90", "Ribbed Xenolith 90°", xenoliths);
        Variations.RIBBED_XENOLITH_180 = regXenolithVariations("ribbed_xenolith_180", "Ribbed Xenolith 180°", xenoliths);
        Variations.RIBBED_XENOLITH_270 = regXenolithVariations("ribbed_xenolith_270", "Ribbed Xenolith 270°", xenoliths);
        Variations.WIDE_RIBBED_XENOLITH_90 = regXenolithVariations("wide_ribbed_xenolith_90", "Wide Ribbed Xenolith 90°", xenoliths);
        Variations.WIDE_RIBBED_XENOLITH_180 = regXenolithVariations("wide_ribbed_xenolith_180", "Wide Ribbed Xenolith 180°", xenoliths);
        Variations.WIDE_RIBBED_XENOLITH_270 = regXenolithVariations("wide_ribbed_xenolith_270", "Wide Ribbed Xenolith 270°", xenoliths);
        Variations.TILED_XENOLITH_90 = regXenolithVariations("tiled_xenolith_90", "Tiled Xenolith 90°", xenoliths);
        Variations.TILED_XENOLITH_180 = regXenolithVariations("tiled_xenolith_180", "Tiled Xenolith 180°", xenoliths);
        Variations.TILED_XENOLITH_270 = regXenolithVariations("tiled_xenolith_270", "Tiled Xenolith 270°", xenoliths);
        Variations.WAVED_XENOLITH_90 = regXenolithVariations("waved_xenolith_90", "Waved Xenolith 90°", xenoliths);
        Variations.CHEVRON_XENOLITH_90 = regXenolithVariations("chevron_xenolith_90", "Chevron Xenolith 90°", xenoliths);
        Variations.CHEVRON_XENOLITH_180 = regXenolithVariations("chevron_xenolith_180", "Chevron Xenolith 180°", xenoliths);
        Variations.CHEVRON_XENOLITH_270 = regXenolithVariations("chevron_xenolith_270", "Chevron Xenolith 270°", xenoliths);
        Variations.FIBROUS_XENOLITH_90 = regXenolithVariations("fibrous_xenolith_90", "Fibrous Xenolith 90°", xenoliths);
        Variations.FIBROUS_XENOLITH_180 = regXenolithVariations("fibrous_xenolith_180", "Fibrous Xenolith 180°", xenoliths);
        Variations.FIBROUS_XENOLITH_270 = regXenolithVariations("fibrous_xenolith_270", "Fibrous Xenolith 270°", xenoliths);

        COMMON_XENOLITHS = xenoliths;

        REGISTER.register("glowing_chiseled_xenolith", () -> new DungeonBlock(GLOWING_BLOCK_PROPS_CREATOR.create()))
                .blockItem(LGCreativeTabs.MAIN).name("Glowing Chiseled Xenolith");
        REGISTER.register("glowing_framed_xenolith", () -> new DungeonBlock(GLOWING_BLOCK_PROPS_CREATOR.create()))
                .blockItem(LGCreativeTabs.MAIN).name("Glowing Framed Xenolith");
        REGISTER.register("glowing_waved_xenolith", () -> new DungeonBlock(GLOWING_BLOCK_PROPS_CREATOR.create()))
                .blockItem(LGCreativeTabs.MAIN).name("Glowing Waved Xenolith");
        REGISTER.register("glowing_chevron_xenolith", () -> new DungeonBlock(GLOWING_BLOCK_PROPS_CREATOR.create()))
                .blockItem(LGCreativeTabs.MAIN).name("Glowing Chevron Xenolith");
        REGISTER.register("glowing_fibrous_xenolith", () -> new DungeonBlock(GLOWING_BLOCK_PROPS_CREATOR.create()))
                .blockItem(LGCreativeTabs.MAIN).name("Glowing Fibrous Xenolith");

        REGISTER.register("microverse_manipulator", MicroverseManipulatorBlock::new)
                .name("Microverse Manipulator")
                .item(() -> new MicroverseManipulatorItem(LGBlocks.MICROVERSE_MANIPULATOR),
                        chain -> chain
                                .onCreativeTabBuilding(ItemRegister.CreativeTabAdder
                                        .tabBased(LGCreativeTabs.MAIN))
                );
        REGISTER.register("transference_crystal", TransferenceCrystalBlock::new)
                .name("Transference Crystal")
                .item(() -> new TransferenceCrystalItem(LGBlocks.TRANSFERENCE_CRYSTAL),
                        chain -> chain
                                .onCreativeTabBuilding(ItemRegister.CreativeTabAdder
                                        .tabBased(LGCreativeTabs.MAIN))
                );
    }

    private static BlockVariations<DungeonBlock> regXenolithVariations(String id, String enName, List<BlockVariations<DungeonBlock>> xenolithCache) {
        BlockBehaviour.Properties properties = BLOCK_PROPS_FACTORY.create();

        Promised<DungeonBlock> promisedBlock = REGISTER.register(id, () -> new DungeonBlock(properties))
                .blockItem(LGCreativeTabs.MAIN).name(enName)
                .asPromised();

        Promised<SlabBlock> promisedSlab = REGISTER.register(id + "_slab",
                        () -> new SlabBlock(properties))
                .blockItem(LGCreativeTabs.MAIN).name(enName + " Slab")
                .asPromised();

        Promised<StairBlock> promisedStairs = REGISTER.register(id + "_stairs",
                        () -> new StairBlock(() -> promisedBlock.get().defaultBlockState(), properties))
                .blockItem(LGCreativeTabs.MAIN).name(enName + " Stairs")
                .asPromised();

        BlockVariations<DungeonBlock> variations = new BlockVariations<>(promisedBlock, promisedSlab, promisedStairs);
        xenolithCache.add(variations);
        return variations;
    }
}
