package eu.usrv.lootgames.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.auxiliary.RandHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.List;
import java.util.Random;


public class DungeonLightSource extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon[] mIcons;

    public DungeonLightSource() {
        super(Material.glass);
        setBlockName("dungeonLight");
        setCreativeTab(LootGames.CreativeTab);
        setHardness(2.0F);
        setResistance(6.0F);
        setStepSound(soundTypeGlass);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        mIcons = new IIcon[2];
        mIcons[0] = register.registerIcon(String.format("LootGames:dungeonLight"));
        mIcons[1] = register.registerIcon(String.format("LootGames:dungeonLightBroken"));
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == 0 ? 15 : 0;
    }

    @Override
    public int getHarvestLevel(int metadata) {
        if (metadata == eState.BROKEN.ordinal())
            return 1;
        else
            return 4;
    }

    @Override
    public String getHarvestTool(int metadata) {
        return "pickaxe";
    }

    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 1;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public int damageDropped(int meta) {
        int tRet = eState.BROKEN.ordinal();
        if (meta == eState.NORMAL.ordinal())
            tRet = RandHelper.flipCoin(eState.BROKEN.ordinal(), eState.NORMAL.ordinal());

        return tRet;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int pSide, int pMeta) {
        return mIcons[pMeta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        return mIcons[iBlockAccess.getBlockMetadata(x, y, z)];
    }

    public enum eState {
        NORMAL,
        BROKEN
    }

}
