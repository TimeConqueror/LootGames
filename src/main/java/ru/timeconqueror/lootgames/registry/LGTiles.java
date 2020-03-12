package ru.timeconqueror.lootgames.registry;

import net.minecraft.tileentity.TileEntityType;
import ru.timeconqueror.lootgames.client.tile.TESRPipesMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPipesMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPuzzleMaster;
import ru.timeconqueror.timecore.api.registry.TileEntityTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable//TODO change to create method
public class LGTiles extends TileEntityTimeRegistry {

    public static TileEntityType<TileEntityPuzzleMaster> PUZZLE_MASTER;
    public static TileEntityType<TileEntityMSMaster> MS_MASTER;
    public static TileEntityType<TileEntityPipesMaster> PIPES_MASTER;

    @Override
    public void register() {
        PUZZLE_MASTER = regTileEntity(TileEntityPuzzleMaster.class, TileEntityPuzzleMaster::new, "puzzle_master", LGBlocks.PUZZLE_MASTER).retrieveTileEntityType();
        MS_MASTER = regTileEntity(TileEntityMSMaster.class, TileEntityMSMaster::new, "ms_master", LGBlocks.MS_MASTER).retrieveTileEntityType();
        PIPES_MASTER = regTileEntity(TileEntityPipesMaster.class, TileEntityPipesMaster::new, "pipes_master", LGBlocks.PIPES_MASTER).regCustomRenderer(() -> TESRPipesMaster::new).retrieveTileEntityType();
    }
}
