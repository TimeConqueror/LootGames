package ru.timeconqueror.lootgames.api.block.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class TileBoardGameMaster<T extends BoardLootGame<T>> extends TileEntityGameMaster<T> {
    public TileBoardGameMaster(TileEntityType<? extends TileEntityGameMaster<T>> type, T game) {
        super(type, game);
    }

    @Override
    public void onBlockLeftClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        super.onBlockLeftClick(player, subordinatePos);
        onClick(player, subordinatePos, MouseClickType.LEFT);
    }

    @Override
    public void onBlockRightClick(ServerPlayerEntity player, BlockPos subordinatePos) {
        super.onBlockRightClick(player, subordinatePos);
        onClick(player, subordinatePos, MouseClickType.RIGHT);
    }

    private void onClick(ServerPlayerEntity player, BlockPos subordinatePos, MouseClickType type) {
        Pos2i pos = game.convertToGamePos(subordinatePos);

        int size = game.getCurrentBoardSize();
        if (pos.getX() >= 0 && pos.getX() < size
                && pos.getY() >= 0 && pos.getY() < size) {
            game.onClick(player, pos, type);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void prepareMatrix(MatrixStack matrixStack, TileBoardGameMaster<?> master) {
        BlockPos boardOrigin = master.getGame().getBoardOrigin();
        Vector3i originOffset = boardOrigin.subtract(master.getBlockPos()).offset(0, 1, 0);

        matrixStack.translate(originOffset.getX(), originOffset.getY(), originOffset.getZ());
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
    }

    @NotNull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
}
