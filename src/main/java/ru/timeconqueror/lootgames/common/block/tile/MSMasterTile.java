package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;

public class MSMasterTile extends BoardGameMasterTile<GameMineSweeper> {
    public MSMasterTile(BlockPos pos, BlockState state) {
        super(LGBlockEntities.MS_MASTER, pos, state, new GameMineSweeper());
    }
}
