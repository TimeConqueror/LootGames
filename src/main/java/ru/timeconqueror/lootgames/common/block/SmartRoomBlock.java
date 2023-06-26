package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import ru.timeconqueror.lootgames.api.block.ILeftInteractible;
import ru.timeconqueror.lootgames.api.minigame.event.GameEvents;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;

public class SmartRoomBlock extends Block implements ILeftInteractible {
    public SmartRoomBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean onLeftClick(Level level, Player player, BlockPos pos, Direction face) {
        if (!player.isShiftKeyDown()) {
            RoomCoords coords = RoomCoords.of(pos);
            Room room = RoomAccess.getRoom(level, coords);
            if (room == null || room.getGame() == null) {
                return false;
            }

            room.getGame().getEventBus().post(GameEvents.LEFT_CLICK_SMART_ROOM_BLOCK,
                    new GameEvents.SmartRoomBlockLeftClickEvent(level, player, coords.toRelative(pos), face));
            return true;
        }

        return false;
    }
}
