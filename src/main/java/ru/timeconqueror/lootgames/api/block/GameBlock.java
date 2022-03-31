package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import ru.timeconqueror.lootgames.client.IconLoader;
import ru.timeconqueror.lootgames.common.config.ConfigGeneral;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.utils.VanillaStyler;
import ru.timeconqueror.lootgames.utils.future.BlockPos;

import javax.annotation.Nullable;

public abstract class GameBlock extends Block {
    public GameBlock() {
        super(Material.rock);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setLightLevel(LGConfigs.GENERAL.worldGen.fitVanillaDungeonStyle ? 0.95F : 1F);
    }

    public GameBlock(Material material) {
        super(material);
    }

    @Nullable
    private BlockPos inWorld;

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        inWorld = BlockPos.of(x, y, z);
        IIcon icon = super.getIcon(worldIn, x, y, z, side);
        inWorld = null;
        return icon;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (this.textureName != null) return blockIcon;

        //fallbacks

        if(LGConfigs.GENERAL.worldGen.fitVanillaDungeonStyle) {
            if(inWorld != null) {
                return VanillaStyler.getIcon(inWorld.getX(), inWorld.getY(), inWorld.getZ(), side);
            }

            return VanillaStyler.getIcon(0,0,0, side);
        }

        return IconLoader.shieldedDungeonFloor;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        if (this.textureName != null) {
            super.registerIcons(reg);
        }
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
