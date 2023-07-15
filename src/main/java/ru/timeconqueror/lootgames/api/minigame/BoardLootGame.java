package ru.timeconqueror.lootgames.api.minigame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.api.minigame.event.BlockClickEvent;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomOffset;
import ru.timeconqueror.lootgames.utils.MouseClickType;

import static ru.timeconqueror.lootgames.api.minigame.BoardLootGame.BoardStage;

/**
 * Loot game that is flat.
 */
public abstract class BoardLootGame extends LootGame<BoardStage> {
    public static final int DEFAULT_FLOOR_POS = 60;

    public BoardLootGame(ResourceLocation id, Room room) {
        super(id, room);
        getEventBus().addEventHandler(GameEvents.CLICK_BLOCK, event -> event.setAccepted(onBlockClicked(event)));
    }

    @Override
    public BlockPos getGameCenter() {
        int size = getBoardSize();
        return getBoardOrigin().offset(size / 2, 0, size / 2);
    }

    protected boolean onBlockClicked(BlockClickEvent event) {
        RoomOffset clickPos = event.getRelative();
        Vector2ic boardPos = offsetToBoardPos(clickPos);
        int size = getBoardSize();

        if (boardPos.x() >= 0 && boardPos.x() < size
                && boardPos.y() >= 0 && boardPos.y() < size) {
            onBoardClicked(event.getPlayer(), boardPos, event.getClickType());
            return true;
        }
        return false;
    }

    protected void onBoardClicked(Player player, Vector2ic pos, MouseClickType type) {
        getStage().onClick(player, pos, type);
    }

    public abstract int getBoardSize();

    public Vector2ic offsetToBoardPos(RoomOffset offset) {
        Vec3i result = offset.subtract(getRoom().getCoords().toRelative(getBoardOrigin()));
        return new Vector2i(result.getX(), result.getZ());
    }

    public BlockPos boardToBlockPos(Vector2ic pos) {
        return getBoardOrigin().offset(pos.x(), 0, pos.y());
    }

    /**
     * Returns left-top (north-west) corner of the board.
     */
    public BlockPos getBoardOrigin() {
        int size = getBoardSize();
        return room.getCoords().centerPos(DEFAULT_FLOOR_POS)
                .offset(-size / 2, 0, -size / 2);
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
        protected void onClick(Player player, Vector2ic pos, MouseClickType type) {
        }
    }
}
