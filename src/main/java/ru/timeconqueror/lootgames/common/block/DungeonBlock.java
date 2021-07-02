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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

import java.util.List;

public class DungeonBlock extends Block {
    public static final BlockPropsFactory BRICKS_PROPS_CREATOR = new BlockPropsFactory(() -> Properties.of(Material.STONE)
            .strength(10.0F, 6.0F)
            .sound(SoundType.STONE));

    public static final BlockPropsFactory LAMP_PROPS_CREATOR = new BlockPropsFactory(() -> Properties.of(Material.GLASS)
            .strength(2.0F, 6.0F)
            .sound(SoundType.GLASS)
            .lightLevel(value -> 15));

    public DungeonBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.lootgames.dungeon_block.tooltip").withStyle(TextFormatting.DARK_GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }
}
