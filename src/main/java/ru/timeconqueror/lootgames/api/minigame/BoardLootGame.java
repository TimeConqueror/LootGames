package ru.timeconqueror.lootgames.api.minigame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.utils.MouseClickType;

import static ru.timeconqueror.lootgames.api.minigame.BoardLootGame.BoardStage;

/**
 * Loot game that is flat.
 */
public abstract class BoardLootGame extends LootGame<BoardStage> {
    private static final int DEFAULT_FLOOR_POS = 60;

    public BoardLootGame(ResourceLocation id, Room room) {
        super(id, room);
    }

    @Override
    public BlockPos getGameCenter() {
        int size = getCurrentBoardSize();
        return getBoardOrigin().offset(size / 2, 0, size / 2);
    }

    public abstract int getCurrentBoardSize();

    public abstract int getAllocatedBoardSize();

    public Pos2i convertToGamePos(BlockPos subordinatePos) {
        BlockPos result = subordinatePos.subtract(getBoardOrigin());
        return new Pos2i(result.getX(), result.getZ());
    }

    public BlockPos convertToBlockPos(Pos2i pos) {
        return getBoardOrigin().offset(pos.getX(), 0, pos.getY());
    }

    public BlockPos getBoardOrigin() {
        int size = getCurrentBoardSize();
        return room.getCoords().centerPos(DEFAULT_FLOOR_POS)
                .offset(-size / 2, 0, -size / 2);
    }

    public void onClick(Player player, Pos2i pos, MouseClickType type) {
        getStage().onClick(player, pos, type);
    }

    @OnlyIn(Dist.CLIENT)
    public void prepareMatrix(PoseStack matrixStack) {
        Vec3i originOffset = getBoardOrigin().offset(0, 1, 0);

        matrixStack.translate(originOffset.getX(), originOffset.getY(), originOffset.getZ());
        matrixStack.mulPose(Axis.XP.rotationDegrees(90));
    }

    public abstract static class BoardStage extends Stage {
        /**
         * Called for both sides when player clicks on board block.
         */
        protected void onClick(Player player, Pos2i pos, MouseClickType type) {
        }
    }
}
