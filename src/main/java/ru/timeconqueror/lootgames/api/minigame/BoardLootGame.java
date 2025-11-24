package ru.timeconqueror.lootgames.api.minigame;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.minigame.event.LeftClickBlockEvent;
import ru.timeconqueror.lootgames.api.minigame.event.RightClickBlockEvent;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;
import ru.timeconqueror.lootgames.registry.custom.GameDefinition;
import ru.timeconqueror.lootgames.utils.MouseClickType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ru.timeconqueror.lootgames.api.minigame.BoardLootGame.BoardStage;

/**
 * Loot game that is flat.
 */
public abstract class BoardLootGame extends LootGame<BoardStage> {
    public static final int DEFAULT_FLOOR_POS = 60;
    public static final double VERTICAL_RENDER_OFFSET = -6 / 16F;

    public BoardLootGame(GameDefinition definition, Room room) {
        super(definition, room);
        getEventBus().addEventHandler(GameEvents.RIGHT_CLICK_BLOCK, event -> event.setAccepted(
                onBlockClicked(event.getLocalPos(), event.getHand(), (pos) -> onBoardRightClicked(event.getPlayer(), pos)))
        );
        getEventBus().addEventHandler(GameEvents.LEFT_CLICK_BLOCK, event -> event.setAccepted(
                onBlockClicked(event.getLocalPos(), event.getHand(), (pos) -> {
                    if(event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.ABORT) {
                        onBoardLeftClicked(event.getPlayer(), pos);
                    }
                }))
        );
    }

    @Override
    public BlockPos getGameCenter() {
        return room.getCoords().centerPos(DEFAULT_FLOOR_POS);
    }

    protected boolean onBlockClicked(RoomLocalPos clickPos, InteractionHand hand, Consumer<Vector2ic> redirectTo) {
        if (clickPos.getY() != DEFAULT_FLOOR_POS) {
            return false;
        }

        Vector2ic boardPos = offsetToBoardPos(clickPos);
        int size = getBoardSize();

        if (boardPos.x() >= 0 && boardPos.x() < size
                && boardPos.y() >= 0 && boardPos.y() < size) {
            if (hand == InteractionHand.MAIN_HAND) {
                redirectTo.accept(boardPos);
            }
            return true;
        }
        return false;
    }

    protected void onBoardRightClicked(Player player, Vector2ic pos) {
        if(getProgress() == GameProgress.PLAYING) {
            getStage().onClick(player, pos, MouseClickType.RIGHT);
            getStage().onRightClick(player, pos);
        }
    }

    protected void onBoardLeftClicked(Player player, Vector2ic pos) {
        if(getProgress() == GameProgress.PLAYING) {
            getStage().onClick(player, pos, MouseClickType.LEFT);
            getStage().onLeftClick(player, pos);
        }
    }

    public abstract int getBoardSize();

    public Vector2ic offsetToBoardPos(RoomLocalPos offset) {
        Vec3i result = offset.subtract(getRoom().getCoords().toLocalPos(getBoardOrigin()));
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
        return getGameCenter().offset(-size / 2, 0, -size / 2);
    }

    @OnlyIn(Dist.CLIENT)
    public void prepareMatrix(PoseStack matrixStack) {
        matrixStack.translate(0, BoardLootGame.VERTICAL_RENDER_OFFSET, 0);
        Vec3i originOffset = getBoardOrigin().offset(0, 1, 0);

        matrixStack.translate(originOffset.getX(), originOffset.getY(), originOffset.getZ());
    }

    public abstract static class BoardStage extends Stage {
        /**
         * Called for both sides when player left/right clicks on board block.
         */
        protected void onClick(Player player, Vector2ic pos, MouseClickType type) {
        }

        /**
         * Called for both sides when player right-clicks on board block;
         */
        protected void onRightClick(Player player, Vector2ic pos) {

        }

        /**
         * Called for both sides when player left-clicks on board block;
         */
        protected void onLeftClick(Player player, Vector2ic pos) {

        }
    }
}
