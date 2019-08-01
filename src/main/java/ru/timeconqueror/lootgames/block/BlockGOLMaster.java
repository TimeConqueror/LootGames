package ru.timeconqueror.lootgames.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.achievement.AdvancementManager;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.minigame.gameoflight.TileEntityGOLMaster;

import javax.annotation.Nullable;

public class BlockGOLMaster extends BlockGame {

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            if (worldIn.getTileEntity(pos) instanceof TileEntityGOLMaster) {
                TileEntityGOLMaster te = (TileEntityGOLMaster) worldIn.getTileEntity(pos);

                //TODO add specialties
                if (playerIn instanceof EntityPlayerMP) {
                    AdvancementManager.BLOCK_ACTIVATED.trigger(((EntityPlayerMP) playerIn), pos, playerIn.getHeldItem(hand));
                }

                te.onBlockClickedByPlayer(playerIn);
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityGOLMaster();
    }
}
