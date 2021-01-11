package ru.timeconqueror.lootgames.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
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

@ObjectHolder(LootGames.MODID)
public class LGTiles {

    public static final TileEntityType<PuzzleMasterTile> PUZZLE_MASTER = promise();
    public static final TileEntityType<MSMasterTile> MS_MASTER = promise();
    public static final TileEntityType<PipesMasterTile> PIPES_MASTER = promise();
    public static final TileEntityType<GOLMasterTile> GOL_MASTER = promise();

    private static class Init {
        @AutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(LootGames.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.registerSingleBound("puzzle_master", PuzzleMasterTile::new, () -> LGBlocks.PUZZLE_MASTER);
            REGISTER.registerSingleBound("ms_master", MSMasterTile::new, () -> LGBlocks.MS_MASTER).regCustomRenderer(() -> MSMasterRenderer::new);
            REGISTER.registerSingleBound("pipes_master", PipesMasterTile::new, () -> LGBlocks.PIPES_MASTER).regCustomRenderer(() -> PipesMasterRenderer::new);
            REGISTER.registerSingleBound("gol_master", GOLMasterTile::new, () -> LGBlocks.GOL_MASTER).regCustomRenderer(() -> GOLMasterRenderer::new);
        }
    }
}
