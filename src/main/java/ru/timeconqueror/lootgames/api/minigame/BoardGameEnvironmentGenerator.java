package ru.timeconqueror.lootgames.api.minigame;

import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGBlocks;

import java.util.function.Supplier;

@AllArgsConstructor
public class BoardGameEnvironmentGenerator implements GameEnvironmentGenerator {
    private final Supplier<BlockState> boardStateSupplier;

    public BoardGameEnvironmentGenerator() {
        this(() -> LGBlocks.GLASSY_MATTER.defaultBlockState());
    }

    @Override
    public void generate(ServerLevel level, Room room, LootGame<?> game) {
        int floorY = BoardLootGame.DEFAULT_FLOOR_POS;
        RoomCoords coords = room.getCoords();

        BlockPos min = coords.minPos(floorY).offset(1, 0, 1);
        BlockPos max = coords.maxPos(floorY).offset(-1, 0, -1);

        BlockPos.betweenClosed(min, max)
                .forEach(pos -> level.setBlock(pos, boardStateSupplier.get(), Block.UPDATE_CLIENTS));
    }
}
