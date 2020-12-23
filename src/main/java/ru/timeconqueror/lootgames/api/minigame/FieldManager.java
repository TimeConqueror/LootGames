package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.BlockFieldBorder;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.utils.BlockUtils;
import ru.timeconqueror.lootgames.utils.CollectionUtils;
import ru.timeconqueror.timecore.util.NetworkUtils;

/**
 * Can be accessed via {@link LootGamesAPI#getFieldManager()}.
 */
public class FieldManager {

    public boolean canReplaceAreaWithField(World world, BlockPos cornerPos, int xSize, int ySize, int zSize, @Nullable BlockPos except) {
        return CollectionUtils.all(BlockUtils.iterateArea(cornerPos, xSize, ySize, zSize), (pos) ->
                world.getBlockState(pos).getMaterial().isReplaceable() || pos.equals(except)
        );
    }

    /**
     * Tries to replace area with flat field.
     *
     * @param xSize  size of field without border
     * @param height height of area above field exclude floor block
     * @param zSize  size of field without border
     * @param player to notify about fail
     * @return if field placed.
     */
    public boolean trySetupFlatField(ServerWorld world, BlockPos centerPos, int xSize, int height, int zSize, @Nullable BlockState masterBlock, @Nullable PlayerEntity player) {
        BlockPos cornerPos = centerPos.offset(-xSize / 2 - 1, 0, -zSize / 2 - 1);
        BlockPos.Mutable borderPos = cornerPos.mutable();
        if (!canReplaceAreaWithField(world, borderPos, xSize + 2, height + 1, zSize + 2, centerPos)) {
            if (player != null) {
                NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.lootgames.field.not_enough_space", xSize + 2, height + 1, zSize + 2));
                world.playSound(player, centerPos, LGSounds.GOL_GAME_LOSE, SoundCategory.BLOCKS, 0.6F, 1.0F);
            }
            return false;
        }

        // Filling field with subordinate blocks
        BlockPos fieldPos = centerPos.offset(-xSize / 2, 0, -zSize / 2);
        for (BlockPos pos : BlockUtils.iterateArea(fieldPos, xSize, 1, zSize)) {
            if (masterBlock != null && pos.equals(fieldPos)) {
                world.setBlock(pos, masterBlock, 3);
            } else {
                world.setBlock(pos, LGBlocks.SMART_SUBORDINATE.defaultBlockState(), 2);
            }
        }

        // Filling area above field with air
        if (height > 0) {
            BlockPos abovePos = borderPos.offset(0, 1, 0);
            for (BlockPos pos : BlockUtils.iterateArea(abovePos, xSize + 2, height, zSize + 2)) {
                world.removeBlock(pos, false);
            }
        }

        // Filling border corners
        world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.TOP_LEFT), 2);
        borderPos.move(xSize + 1, 0, 0);
        world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.TOP_RIGHT), 2);
        borderPos.move(0, 0, zSize + 1);
        world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.BOTTOM_RIGHT), 2);
        borderPos.move(-xSize - 1, 0, 0);
        world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.BOTTOM_LEFT), 2);

        // Filling border edges
        borderPos.set(cornerPos);
        for (int i = 0; i < xSize; i++) {
            borderPos.move(1, 0, 0);
            world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.HORIZONTAL), 2);
            borderPos.move(0, 0, zSize + 1);
            world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.HORIZONTAL), 2);
            borderPos.move(0, 0, -zSize - 1);
        }
        borderPos.set(cornerPos);
        for (int i = 0; i < xSize; i++) {
            borderPos.move(0, 0, 1);
            world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.VERTICAL), 2);
            borderPos.move(xSize + 1, 0, 0);
            world.setBlock(borderPos, LGBlocks.FIELD_BORDER.defaultBlockState().setValue(BlockFieldBorder.TYPE, BlockFieldBorder.Type.VERTICAL), 2);
            borderPos.move(-xSize - 1, 0, 0);
        }

        return true;
    }
}
