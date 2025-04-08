package ru.timeconqueror.lootgames.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.lootgames.common.block.MicroverseManipulatorBlock;
import ru.timeconqueror.lootgames.common.block.entity.MicroverseManipulatorBlockEntity;
import ru.timeconqueror.lootgames.registry.LGBlocks;

@OnlyIn(Dist.CLIENT)
public class LGBlockEntityWithoutLevelRenderer extends net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer {
    private final MicroverseManipulatorBlockEntity microverseManipulator = new MicroverseManipulatorBlockEntity(BlockPos.ZERO,
            LGBlocks.MICROVERSE_MANIPULATOR.defaultBlockState()
                    .setValue(MicroverseManipulatorBlock.FACING, Direction.SOUTH)
    );

    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public LGBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher_, EntityModelSet entityModelLoader_) {
        super(blockEntityRenderDispatcher_, entityModelLoader_);
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher_;
        this.microverseManipulator.loadAnimationSystem();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();

            BlockEntity blockentity;
            if (block instanceof MicroverseManipulatorBlock) {
                blockentity = microverseManipulator;
            } else {
                return;
            }

            this.blockEntityRenderDispatcher.renderItem(blockentity, matrices, vertexConsumers, light, overlay);
        }
    }
}