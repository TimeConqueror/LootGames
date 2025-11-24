package ru.timeconqueror.lootgames.api.minigame.event;

import lombok.Data;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;
import ru.timeconqueror.lootgames.utils.MouseClickType;

@Data
public class RightClickBlockEvent {
    private final Level level;
    private final Player player;
    private final BlockState state;
    private final RoomLocalPos localPos;
    private final InteractionHand hand;
    private final Direction face;
    private boolean isAccepted;
}
