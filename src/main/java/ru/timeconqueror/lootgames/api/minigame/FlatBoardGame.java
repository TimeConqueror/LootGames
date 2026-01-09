package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;
import ru.timeconqueror.lootgames.registry.custom.GameDefinition;
import ru.timeconqueror.lootgames.utils.MouseClickType;

import java.util.function.Consumer;

import static ru.timeconqueror.lootgames.api.minigame.FlatBoardGame.BoardStage;

/**
 * Loot game that is flat.
 */
public abstract class FlatBoardGame extends LootGame<BoardStage> {
    public static final int DEFAULT_FLOOR_POS = 60;
    public static final double VERTICAL_RENDER_OFFSET = -6 / 16F;

    public FlatBoardGame(GameDefinition definition, Room room) {
        super(definition, room);
        getEventBus().addEventHandler(GameEvents.RIGHT_CLICK_BLOCK, event -> event.setAccepted(
                onBlockClicked(event.getLocalPos(), event.getHand(), event.getFace(), (pos) -> onBoardRightClicked(event.getPlayer(), pos)))
        );
        getEventBus().addEventHandler(GameEvents.LEFT_CLICK_BLOCK, event -> event.setAccepted(
                onBlockClicked(event.getLocalPos(), event.getHand(), event.getFace(), (pos) -> {
                    if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.ABORT) {
                        onBoardLeftClicked(event.getPlayer(), pos);
                    }
                }))
        );
    }

    @Override
    public BlockPos getGameCenter() {
        return room.getCoords().centerPos(DEFAULT_FLOOR_POS);
    }

    protected boolean onBlockClicked(RoomLocalPos clickPos, InteractionHand hand, Direction face, Consumer<Vector2ic> redirectTo) {
        if(getFace() != face) {
            return false;
        }

        Vector2ic boardPos = offsetToBoardPos(clickPos);

        int size = getBoardSize();

        RoomLocalPos origin = room.getCoords().toLocalPos(getBoardOrigin());
        if (getLeadings().isPosOnRange(origin, size, clickPos)) {
            if (hand == InteractionHand.MAIN_HAND) {
                redirectTo.accept(boardPos);
            }
            return true;
        }
        return false;
    }

    protected void onBoardRightClicked(Player player, Vector2ic pos) {
        if (getProgress() == GameProgress.PLAYING) {
            getStage().onClick(player, pos, MouseClickType.RIGHT);
            getStage().onRightClick(player, pos);
        }
    }

    protected void onBoardLeftClicked(Player player, Vector2ic pos) {
        if (getProgress() == GameProgress.PLAYING) {
            getStage().onClick(player, pos, MouseClickType.LEFT);
            getStage().onLeftClick(player, pos);
        }
    }

    public abstract Direction getFace();

    public abstract int getBoardSize();

    public abstract Leadings2D getLeadings();

    public Vector2ic offsetToBoardPos(RoomLocalPos offset) {
        RoomLocalPos origin = getRoom().getCoords().toLocalPos(getBoardOrigin());
        return getLeadings().toFlatPos(origin, offset);
    }

    public BlockPos boardToBlockPos(Vector2ic pos) {
        return getLeadings().fromFlatPos(getBoardOrigin(), pos);
    }

    public BlockPos getBoardOrigin() {
        return getLeadings().origin(getGameCenter(), getBoardSize());
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
