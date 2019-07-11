package ru.timeconqueror.lootgames.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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

public class BlockDungeonBricks extends Block {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

    public BlockDungeonBricks() {
        super(Material.ROCK);
        setHardness(10.0F);
        setResistance(6.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return state.getBlock().getMetaFromState(state) != EnumType.DUNGEON_FLOOR_SHIELDED.getMeta();
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        int meta = blockState.getBlock().getMetaFromState(blockState);
        float hardness = super.getBlockHardness(blockState, worldIn, pos);

        if (meta == EnumType.DUNGEON_CEILING_CRACKED.getMeta() ||
                meta == EnumType.DUNGEON_FLOOR_CRACKED.getMeta() ||
                meta == EnumType.DUNGEON_WALL_CRACKED.getMeta()) {
            hardness = 2.0F;
        } else if (meta == EnumType.DUNGEON_FLOOR_SHIELDED.getMeta()) {
            hardness = -1F;
        }

        return hardness;
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "pickaxe";
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = this.getMetaFromState(state);

        if (meta == EnumType.DUNGEON_CEILING.ordinal() || meta == EnumType.DUNGEON_FLOOR.getMeta() || meta == EnumType.DUNGEON_WALL.getMeta()) {
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType value : EnumType.values()) {
            items.add(new ItemStack(this, 1, value.getMeta()));
        }
    }

    public enum EnumType implements IStringSerializable {
        DUNGEON_WALL(0, 3, "dungeon_wall"),
        DUNGEON_CEILING(1, 4, "dungeon_ceiling"),
        DUNGEON_FLOOR(2, 5, "dungeon_floor"),
        DUNGEON_WALL_CRACKED(3, "dungeon_wall_cracked"),
        DUNGEON_CEILING_CRACKED(4, "dungeon_ceiling_cracked"),
        DUNGEON_FLOOR_CRACKED(5, "dungeon_floor_cracked"),
        DUNGEON_FLOOR_SHIELDED(6, "dungeon_floor_shielded");

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
