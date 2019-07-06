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
import net.minecraft.world.World;

import java.util.List;


public class DungeonBrick extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon[] mIcons;

    public DungeonBrick() {
        super(Material.rock);
        setBlockName("dungeonBrick");
        setCreativeTab(LootGames.CreativeTab);
        setHardness(10.0F);
        setResistance(6.0F);
        setStepSound(soundTypeStone);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
        list.add(new ItemStack(item, 1, 5));
        list.add(new ItemStack(item, 1, 6));
    }

    @Override
    public int damageDropped(int meta) {
        int tRetMeta = meta;

        if (meta == eDungeonBricks.CEILING.ordinal() || meta == eDungeonBricks.FLOOR.ordinal() || meta == eDungeonBricks.WALL.ordinal())
            tRetMeta = meta + RandHelper.flipCoin(0, 3);

        return tRetMeta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        mIcons = new IIcon[7];
        mIcons[0] = register.registerIcon(String.format("LootGames:dungeonWall"));
        mIcons[1] = register.registerIcon(String.format("LootGames:dungeonCeiling"));
        mIcons[2] = register.registerIcon(String.format("LootGames:dungeonFloor"));
        mIcons[3] = register.registerIcon(String.format("LootGames:dungeonWallCrack"));
        mIcons[4] = register.registerIcon(String.format("LootGames:dungeonCeilingCrack"));
        mIcons[5] = register.registerIcon(String.format("LootGames:dungeonFloorCrack"));
        mIcons[6] = register.registerIcon(String.format("LootGames:dungeonFloorGameLayer"));
    }

    @Override
    public int getHarvestLevel(int metadata) {
        int tHarvestLevel = super.getHarvestLevel(metadata);

        if (metadata == eDungeonBricks.CEILING.ordinal() || metadata == eDungeonBricks.FLOOR.ordinal() || metadata == eDungeonBricks.WALL.ordinal())
            tHarvestLevel = 4;
        else if (metadata == eDungeonBricks.CEILING_CRACKED.ordinal() || metadata == eDungeonBricks.FLOOR_CRACKED.ordinal() || metadata == eDungeonBricks.WALL_CRACKED.ordinal())
            tHarvestLevel = 2;

        return tHarvestLevel;
    }

    @Override
    public String getHarvestTool(int metadata) {
        return "pickaxe";
    }

    @Override
    public float getBlockHardness(World pWorld, int pX, int pY, int pZ) {
        int tMeta = pWorld.getBlockMetadata(pX, pY, pZ);
        float tBlockHardness = super.getBlockHardness(pWorld, pX, pY, pZ);

        if (tMeta == eDungeonBricks.FLOOR_SHIELDED.ordinal())
            tBlockHardness = -1F;
        else if (tMeta == eDungeonBricks.CEILING_CRACKED.ordinal() || tMeta == eDungeonBricks.FLOOR_CRACKED.ordinal() || tMeta == eDungeonBricks.WALL_CRACKED.ordinal())
            tBlockHardness = 2.0F;

        return tBlockHardness;
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

    public enum eDungeonBricks {
        WALL,
        CEILING,
        FLOOR,
        WALL_CRACKED,
        CEILING_CRACKED,
        FLOOR_CRACKED,
        FLOOR_SHIELDED
    }

}
