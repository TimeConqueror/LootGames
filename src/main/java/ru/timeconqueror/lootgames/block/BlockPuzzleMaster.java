package ru.timeconqueror.lootgames.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.achievement.AdvancementManager;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.tileentity.TileEntityPuzzleMaster;

import javax.annotation.Nullable;
import java.util.Random;

//TODO 1.7.10 -> change animation relative to 1.12.2
public class BlockPuzzleMaster extends BlockGame {

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int tRnd = LootGames.RAND.nextInt(30);
        for (int i = 0; i <= tRnd; i++) {//TODO 1.7.10 -> change pos of animation, translate by 0.5F, as here
            worldIn.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5F + LootGames.RAND.nextGaussian() * 0.8, pos.getY() + LootGames.RAND.nextFloat(), pos.getZ() + 0.5F + LootGames.RAND.nextGaussian() * 0.8, LootGames.RAND.nextGaussian() * 0.02D, 0.5D + LootGames.RAND.nextGaussian() * 0.02D, LootGames.RAND.nextGaussian() * 0.02D);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (worldIn.getTileEntity(pos) instanceof TileEntityPuzzleMaster) {
                TileEntityPuzzleMaster te = (TileEntityPuzzleMaster) worldIn.getTileEntity(pos);

                if (te != null) {
                    if (playerIn instanceof EntityPlayerMP) {
                        AdvancementManager.BLOCK_ACTIVATED.trigger(((EntityPlayerMP) playerIn), pos, playerIn.getHeldItem(hand));
                    }

                    te.onBlockClickedByPlayer(playerIn);
                } else {
                    playerIn.sendMessage(new TextComponentTranslation("msg.lootgames.puzzle_master.broken"));
                }
            }
        }

        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPuzzleMaster();
    }
}
