package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.block.tile.PuzzleMasterTile;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

@AutoRegistrable.Entries("block_entity_type")
public class LGBlockEntities {

    public static BlockEntityType<PuzzleMasterTile> PUZZLE_MASTER;
//    public static BlockEntityType<MSMasterTile> MS_MASTER;
//    public static BlockEntityType<PipesMasterTile> PIPES_MASTER;
//    public static BlockEntityType<GOLMasterTile> GOL_MASTER;

    @AutoRegistrable
    private static final TileEntityRegister REGISTER = new TileEntityRegister(LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.registerSingleBound("puzzle_master", PuzzleMasterTile::new, () -> LGBlocks.PUZZLE_MASTER);
//        REGISTER.registerSingleBound("ms_master", MSMasterTile::new, () -> LGBlocks.MS_MASTER).regCustomRenderer(() -> context -> new MinesweeperRenderer());
//        REGISTER.registerSingleBound("pipes_master", PipesMasterTile::new, () -> LGBlocks.PIPES_MASTER).regCustomRenderer(() -> context -> new PipesMasterRenderer());
//        REGISTER.registerSingleBound("gol_master", GOLMasterTile::new, () -> LGBlocks.GOL_MASTER).regCustomRenderer(() -> context -> new GOLMasterRenderer());
    }
}
