package ru.timeconqueror.lootgames.api.minigame.event;

import lombok.Data;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import ru.timeconqueror.lootgames.api.room.RoomLocalPos;

@Data
public class LeftClickBlockEvent {
    private final Level level;
    private final Player player;
    private final BlockState state;
    private final RoomLocalPos localPos;
    private final InteractionHand hand;
    private final Direction face;
    private final PlayerInteractEvent.LeftClickBlock.Action action;
    private boolean isAccepted;
}
