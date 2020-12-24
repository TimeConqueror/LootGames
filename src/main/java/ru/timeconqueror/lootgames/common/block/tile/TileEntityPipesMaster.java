package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.lootgames.registry.LGTiles;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class TileEntityPipesMaster extends TileEntityGameMaster<GamePipes> {

    public TileEntityPipesMaster() {
        super(LGTiles.PIPES_MASTER, new GamePipes(5, 0.5f));
    }

    @Override
    public void onBlockRightClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        Pos2i pos = GameMineSweeper.convertToGamePos(getBlockPos(), subordinatePos);
        game.clickField(player, pos, MouseClickType.RIGHT);
    }

    @Override
    public void onBlockLeftClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        Pos2i pos = GameMineSweeper.convertToGamePos(getBlockPos(), subordinatePos);
        game.clickField(player, pos, MouseClickType.LEFT);
    }

    @Override
    public void onDestroy() {
        for (int x = 0; x < game.getCurrentBoardSize(); x++) {
            for (int z = 0; z < game.getCurrentBoardSize(); z++) {
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
