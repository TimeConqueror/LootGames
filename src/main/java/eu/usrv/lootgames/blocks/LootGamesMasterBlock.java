package eu.usrv.lootgames.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.achievements.LootGameAchievement;
import eu.usrv.lootgames.tiles.TELootGamesMasterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;


/**
 * This is the Master-Block for the Mod. It is spawned in each dungeon, and acts as a placeholder for all Minigames.
 * The purpose of this is, that this way no world-Regen has to be done, once this mod updates. The
 * Dungeons will always stay the same, only the minigames to change.
 */
public class LootGamesMasterBlock extends Block implements ITileEntityProvider {
    @SideOnly(Side.CLIENT)
    protected IIcon mTexture;

    public LootGamesMasterBlock() {
        super(Material.iron);
        setBlockUnbreakable();
        setUnlocalizedName("lootGamesMaster");
        setCreativeTab(LootGames.CreativeTab);
        setLightLevel(1.0F);
    }

    @Override
    public final boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        mTexture = register.registerIcon(String.format("LootGames:masterblock"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return mTexture;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        return mTexture;
    }

    @Override
    public void randomDisplayTick(World pWorld, int pX, int pY, int pZ, Random pRnd) {
        int tRnd = LootGames.Rnd.nextInt(30);
        for (int i = 0; i <= tRnd; i++)
            pWorld.spawnParticle("enchantmenttable", pX + LootGames.Rnd.nextGaussian() * 0.8, pY + LootGames.Rnd.nextFloat(), pZ + LootGames.Rnd.nextGaussian() * 0.8, LootGames.Rnd.nextGaussian() * 0.02D, 0.5D + LootGames.Rnd.nextGaussian() * 0.02D, LootGames.Rnd.nextGaussian() * 0.02D);
    }

    @Override
    public boolean onBlockActivated(World pWorld, int pX, int pY, int pZ, EntityPlayer pPlayer, int pSide, float pSubX, float pSubY, float pSubZ) {
        if (pWorld.isRemote)
            return true;
        else {
            LootGameAchievement.FIND_MINIDUNGEON.triggerAchievement(pPlayer);
            if (pWorld.getTileEntity(pX, pY, pZ) instanceof TELootGamesMasterBlock) {
                TELootGamesMasterBlock tileEntity = (TELootGamesMasterBlock) pWorld.getTileEntity(pX, pY, pZ);
                tileEntity.onBlockClickedByPlayer(null, pPlayer);
            }
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World pWorld, int p_149915_2_) {
        return new TELootGamesMasterBlock();
    }
}