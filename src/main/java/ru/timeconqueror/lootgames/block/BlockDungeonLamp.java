package ru.timeconqueror.lootgames.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.auxiliary.RandHelper;

import javax.annotation.Nullable;

public class BlockDungeonLamp extends Block {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

    public BlockDungeonLamp() {
        super(Material.GLASS);
        setHardness(2.0F);
        setResistance(6.0F);
        setSoundType(SoundType.GLASS);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getMetaFromState(state) == EnumType.DUNGEON_LAMP.getMeta() ? 15 : 0;
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return this.getMetaFromState(state) == EnumType.DUNGEON_LAMP_BROKEN.getMeta() ? 1 : 4;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "pickaxe";
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = this.getMetaFromState(state);

        if (meta == EnumType.DUNGEON_LAMP.ordinal()) {
            meta = RandHelper.flipCoin(meta, EnumType.byMetadata(meta).getCrackedBlockMeta());
        }

        drops.add(new ItemStack(this, 1, RandHelper.flipCoin(meta, EnumType.byMetadata(meta).getCrackedBlockMeta())));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMeta();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType value : EnumType.values()) {
            items.add(new ItemStack(this, 1, value.getMeta()));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }


    public enum EnumType implements IStringSerializable {
        DUNGEON_LAMP(0, 1, "dungeon_lamp"),
        DUNGEON_LAMP_BROKEN(1, "dungeon_lamp_broken");

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];

        static {
            for (EnumType value : values()) {
                META_LOOKUP[value.getMeta()] = value;
            }
        }

        private int meta;
        private int crackedBlockMeta;
        private String name;

        EnumType(int meta, String name) {
            this(meta, meta, name);
        }

        EnumType(int meta, int crackedBlockMeta, String name) {
            this.meta = meta;
            this.crackedBlockMeta = crackedBlockMeta;
            this.name = name;
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public int getMeta() {
            return meta;
        }

        public int getCrackedBlockMeta() {
            return crackedBlockMeta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
