package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class BoardGameEnvironmentGenerator implements GameEnvironmentGenerator {
    @Override
    public void generate(ServerLevel level, Room room, LootGame<?> game) {
        int floorY = BoardLootGame.DEFAULT_FLOOR_POS;
        RoomCoords coords = room.getCoords();

        BlockPos min = coords.minPos(floorY).offset(1, 0, 1);
        BlockPos max = coords.maxPos(floorY).offset(-1, 0, -1);

        BlockPos.betweenClosed(min, max)
                .forEach(pos -> level.setBlock(pos, LGBlocks.GLASSY_MATTER.defaultBlockState(), Block.UPDATE_CLIENTS));
    }
}
