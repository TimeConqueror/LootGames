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
import ru.timeconqueror.timecore.api.common.registry.block.BlockPropertiesFactory;

import javax.annotation.Nullable;
import java.util.List;

public class BlockDungeonBricks extends Block {
    public static BlockPropertiesFactory defPropsCreator = new BlockPropertiesFactory(() -> Properties.create(Material.ROCK)
            .hardnessAndResistance(10.0F, 6.0F)
            .sound(SoundType.STONE));

    public BlockDungeonBricks() {
        super(defPropsCreator.createProps());
    }

    public BlockDungeonBricks(Properties props) {
        super(props);
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.lootgames.dungeon_bricks.tooltip"));
    }
}
