package ru.timeconqueror.lootgames.api.block.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class BoardGameMasterTile<T extends BoardLootGame<T>> extends GameMasterTile<T> {
    public BoardGameMasterTile(BlockEntityType<? extends GameMasterTile<T>> type, BlockPos pos, BlockState state, T game) {
        super(type, pos, state, game);
    }

    @Override
    public void onBlockLeftClick(Player player, BlockPos subordinatePos) {
        super.onBlockLeftClick(player, subordinatePos);
        onClick(player, subordinatePos, MouseClickType.LEFT);
    }

    @Override
    public void onBlockRightClick(Player player, BlockPos subordinatePos) {
        super.onBlockRightClick(player, subordinatePos);
        onClick(player, subordinatePos, MouseClickType.RIGHT);
    }

    private void onClick(Player player, BlockPos subordinatePos, MouseClickType type) {
        Pos2i pos = game.convertToGamePos(subordinatePos);

        int size = game.getCurrentBoardSize();
        if (pos.getX() >= 0 && pos.getX() < size
                && pos.getY() >= 0 && pos.getY() < size) {
            game.onClick(player, pos, type);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void prepareMatrix(PoseStack matrixStack, BoardGameMasterTile<?> master) {
        BlockPos boardOrigin = master.getGame().getBoardOrigin();
        Vec3i originOffset = boardOrigin.subtract(master.getBlockPos()).offset(0, 1, 0);

        matrixStack.translate(originOffset.getX(), originOffset.getY(), originOffset.getZ());
        matrixStack.mulPose(Axis.XP.rotationDegrees(90));
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }
}
