package ru.timeconqueror.lootgames.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.render.tile.TESRMSMaster;
import ru.timeconqueror.lootgames.client.render.tile.TESRPipesMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityGOLMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPipesMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPuzzleMaster;
import ru.timeconqueror.timecore.api.registry.TileEntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

import static ru.timeconqueror.timecore.api.util.Hacks.promise;

@ObjectHolder(LootGames.MODID)
public class LGTiles {

    public static final TileEntityType<TileEntityPuzzleMaster> PUZZLE_MASTER = promise();
    public static final TileEntityType<TileEntityMSMaster> MS_MASTER = promise();
    public static final TileEntityType<TileEntityPipesMaster> PIPES_MASTER = promise();
    public static final TileEntityType<TileEntityGOLMaster> GOL_MASTER = promise();

    private static class Init {
        @AutoRegistrable
        private static final TileEntityRegister REGISTER = new TileEntityRegister(LootGames.MODID);

        @AutoRegistrable.InitMethod
        private static void register() {
            REGISTER.registerSingleBound("puzzle_master", TileEntityPuzzleMaster::new, () -> LGBlocks.PUZZLE_MASTER);
            REGISTER.registerSingleBound("ms_master", TileEntityMSMaster::new, () -> LGBlocks.MS_MASTER).regCustomRenderer(() -> TESRMSMaster::new);
            REGISTER.registerSingleBound("pipes_master", TileEntityPipesMaster::new, () -> LGBlocks.PIPES_MASTER).regCustomRenderer(() -> TESRPipesMaster::new);
            REGISTER.registerSingleBound("gol_master", TileEntityGOLMaster::new, () -> LGBlocks.GOL_MASTER);
        }
    }
}
