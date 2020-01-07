package ru.timeconqueror.lootgames.minigame.minesweeper.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.config.LGConfigMinesweeper;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.registry.ModSounds;

import java.util.Objects;

public class BlockMSActivator extends BlockGame {
    public static void generateGameStructure(World world, BlockPos centerPos, int level) {
        int size = Objects.requireNonNull(LGConfigMinesweeper.getStage(level)).boardSize;
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

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {//todo add advancement
            generateGameStructure(worldIn, pos, 1);
            worldIn.playSound(null, pos, ModSounds.msStartGame, SoundCategory.BLOCKS, 0.6F, 1.0F);
        }
        return true;
    }
}
