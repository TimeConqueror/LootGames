package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.common.config.ConfigMS;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGTiles;

public class TileEntityMSMaster extends TileEntityGameMaster<GameMineSweeper> {
    public TileEntityMSMaster() {
        super(LGTiles.MS_MASTER, new GameMineSweeper(ConfigMS.INSTANCE.STAGE_1.getBoardSize(), ConfigMS.INSTANCE.STAGE_1.BOMB_COUNT.get()));
    }

    @Override
    public void onSubordinateBlockClicked(ServerPlayerEntity player, BlockPos subordinatePos) {
        Pos2i pos = GameMineSweeper.convertToGamePos(getPos(), subordinatePos);

        game.clickField(player, pos);
    }

    @Override
    public void destroyGameBlocks() {
        for (int x = 0; x < game.getBoardSize(); x++) {
            for (int z = 0; z < game.getBoardSize(); z++) {
                getWorld().setBlockState(getPos().add(x, 0, z), Blocks.AIR.getDefaultState(), 2);
            }
        }
    }

    @NotNull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
}
