package ru.timeconqueror.lootgames.api.minigame.event;

import lombok.Data;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.api.room.RoomOffset;
import ru.timeconqueror.lootgames.utils.MouseClickType;

@Data
public final class ClickBlockEvent {
    private final Level level;
    private final Player player;
    private final BlockState state;
    private final RoomOffset relative;
    private final InteractionHand hand;
    private final Direction face;
    private final MouseClickType clickType;
    private boolean isAccepted;
}