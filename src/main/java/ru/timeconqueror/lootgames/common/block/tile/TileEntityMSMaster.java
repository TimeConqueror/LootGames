package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGTiles;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class TileEntityMSMaster extends TileEntityGameMaster<GameMineSweeper> {
    public TileEntityMSMaster() {
        super(LGTiles.MS_MASTER, new GameMineSweeper(LGConfigs.MINESWEEPER.STAGE_1.getBoardSize(), LGConfigs.MINESWEEPER.STAGE_1.BOMB_COUNT.get()));
    }

    @Override
    public void onBlockRightClicked(ServerPlayerEntity player, BlockPos subordinatePos) {
        Pos2i pos = GameMineSweeper.convertToGamePos(getBlockPos(), subordinatePos);

        game.click(player, pos, MouseClickType.RIGHT);
    }

    @Override
    public void onBlockLeftClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        Pos2i pos = GameMineSweeper.convertToGamePos(getBlockPos(), subordinatePos);
        game.click(player, pos, MouseClickType.LEFT);
    }

    @Override
    public void destroyGameBlocks() {
        for (int x = 0; x < game.getBoardSize(); x++) {
            for (int z = 0; z < game.getBoardSize(); z++) {
                getLevel().setBlock(getBlockPos().offset(x, 0, z), Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    @NotNull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
}
