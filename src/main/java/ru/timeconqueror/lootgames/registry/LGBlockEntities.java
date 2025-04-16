package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.render.blockentity.MicroverseManipulatorRenderer;
import ru.timeconqueror.lootgames.client.render.blockentity.TransferenceCrystalRenderer;
import ru.timeconqueror.lootgames.common.block.entity.MicroverseManipulatorBlockEntity;
import ru.timeconqueror.lootgames.common.block.entity.PuzzleMasterBlockEntity;
import ru.timeconqueror.lootgames.common.block.entity.TransferenceCrystalBlockEntity;
import ru.timeconqueror.timecore.api.registry.BlockEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

@AutoRegistrable.Entries("block_entity_type")
public class LGBlockEntities {

    public static BlockEntityType<PuzzleMasterBlockEntity> PUZZLE_MASTER;
//    public static BlockEntityType<MSMasterTile> MS_MASTER;
//    public static BlockEntityType<PipesMasterTile> PIPES_MASTER;
//    public static BlockEntityType<GOLMasterTile> GOL_MASTER;

    public static BlockEntityType<MicroverseManipulatorBlockEntity> MICROVERSE_MANIPULATOR;
    public static BlockEntityType<TransferenceCrystalBlockEntity> TRANSFERENCE_CRYSTAL;

    @AutoRegistrable
    private static final BlockEntityRegister REGISTER = new BlockEntityRegister(LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.registerSingleBound("puzzle_master", PuzzleMasterBlockEntity::new, () -> LGBlocks.PUZZLE_MASTER);

        REGISTER.registerSingleBound("microverse_manipulator", MicroverseManipulatorBlockEntity::new, () -> LGBlocks.MICROVERSE_MANIPULATOR)
                .regCustomRenderer(() -> MicroverseManipulatorRenderer::new);
        REGISTER.registerSingleBound("transference_crystal", TransferenceCrystalBlockEntity::new, () -> LGBlocks.TRANSFERENCE_CRYSTAL)
                .regCustomRenderer(() -> TransferenceCrystalRenderer::new);
//        REGISTER.registerSingleBound("ms_master", MSMasterTile::new, () -> LGBlocks.MS_MASTER).regCustomRenderer(() -> context -> new MinesweeperRenderer());
//        REGISTER.registerSingleBound("pipes_master", PipesMasterTile::new, () -> LGBlocks.PIPES_MASTER).regCustomRenderer(() -> context -> new PipesMasterRenderer());
//        REGISTER.registerSingleBound("gol_master", GOLMasterTile::new, () -> LGBlocks.GOL_MASTER).regCustomRenderer(() -> context -> new GOLMasterRenderer());
    }
}
