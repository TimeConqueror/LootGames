package ru.timeconqueror.lootgames.minigame.minesweeper.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.registry.ModBlocks;

public class BlockMSActivator extends BlockGame {
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {//todo add advancement
            genBoard(worldIn, pos);
        }
        return true;
    }

    private void genBoard(World world, BlockPos centerPos) {
        int size = LootGamesConfig.minesweeper.boardSize;
        BlockPos startPos = centerPos.add(-size / 2, 0, -size / 2);

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos pos = startPos.add(x, 0, z);
                if (x == 0 && z == 0) {
                    world.setBlockState(pos, ModBlocks.MS_MASTER.getDefaultState());
                } else {
                    world.setBlockState(pos, ModBlocks.SMART_SUBORDINATE.getDefaultState());
                }
            }
        }
    }
}
