package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import ru.timeconqueror.timecore.api.registry.block.BlockPropertiesFactory;

import javax.annotation.Nullable;
import java.util.List;

public class BlockDungeon extends Block {
    public static final BlockPropertiesFactory BRICKS_PROPS_CREATOR = new BlockPropertiesFactory(() -> Properties.create(Material.ROCK)
            .hardnessAndResistance(10.0F, 6.0F)
            .sound(SoundType.STONE));

    public static final BlockPropertiesFactory LAMP_PROPS_CREATOR = new BlockPropertiesFactory(() -> Properties.create(Material.GLASS)
            .hardnessAndResistance(2.0F, 6.0F)
            .sound(SoundType.GLASS)
            .lightValue(15));

    public BlockDungeon(Properties props) {
        super(props);
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.lootgames.dungeon_block.tooltip"));
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }
}
