package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.api.block.tile.BoardGameMasterTile;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;

public class GOLMasterTile extends BoardGameMasterTile<GameOfLight> {
    public GOLMasterTile(BlockPos pos, BlockState state) {
        super(LGBlockEntities.GOL_MASTER, pos, state, new GameOfLight());
    }
}
