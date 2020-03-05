package ru.timeconqueror.lootgames.registry;

import net.minecraft.tileentity.TileEntityType;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPuzzleMaster;
import ru.timeconqueror.timecore.api.registry.TileEntityTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;

@TimeAutoRegistry
public class LGTiles extends TileEntityTimeRegistry {

    public static TileEntityType<TileEntityPuzzleMaster> PUZZLE_MASTER;

    @Override
    public void register() {
        PUZZLE_MASTER = regTileEntity(TileEntityPuzzleMaster::new, "puzzle_master", LGBlocks.PUZZLE_MASTER).retrieveTileEntityType();
    }
}
