package ru.timeconqueror.lootgames.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
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
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.lootgames.dungeon_block.tooltip").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
