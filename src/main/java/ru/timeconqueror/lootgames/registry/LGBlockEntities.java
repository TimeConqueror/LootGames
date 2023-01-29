package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.render.tile.GOLMasterRenderer;
import ru.timeconqueror.lootgames.client.render.tile.MSMasterRenderer;
import ru.timeconqueror.lootgames.client.render.tile.PipesMasterRenderer;
import ru.timeconqueror.lootgames.common.block.tile.GOLMasterTile;
import ru.timeconqueror.lootgames.common.block.tile.MSMasterTile;
import ru.timeconqueror.lootgames.common.block.tile.PipesMasterTile;
import ru.timeconqueror.lootgames.common.block.tile.PuzzleMasterTile;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@AutoRegistrable.Entries("block_entity_type")
public class LGBlockEntities {

    public static final BlockEntityType<PuzzleMasterTile> PUZZLE_MASTER = promise();
    public static final BlockEntityType<MSMasterTile> MS_MASTER = promise();
    public static final BlockEntityType<PipesMasterTile> PIPES_MASTER = promise();
    public static final BlockEntityType<GOLMasterTile> GOL_MASTER = promise();

    @AutoRegistrable
    private static final TileEntityRegister REGISTER = new TileEntityRegister(LootGames.MODID);

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.registerSingleBound("puzzle_master", PuzzleMasterTile::new, () -> LGBlocks.PUZZLE_MASTER);
        REGISTER.registerSingleBound("ms_master", MSMasterTile::new, () -> LGBlocks.MS_MASTER).regCustomRenderer(() -> context -> new MSMasterRenderer());
        REGISTER.registerSingleBound("pipes_master", PipesMasterTile::new, () -> LGBlocks.PIPES_MASTER).regCustomRenderer(() -> context -> new PipesMasterRenderer());
        REGISTER.registerSingleBound("gol_master", GOLMasterTile::new, () -> LGBlocks.GOL_MASTER).regCustomRenderer(() -> context -> new GOLMasterRenderer());
    }
}
