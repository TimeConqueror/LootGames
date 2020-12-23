package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

public class GameOfLight extends LootGame<GameOfLight> {
    @Override
    protected BlockPos getGameCenter() {
        return null;
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return null;
    }

    @Override
    public @Nullable Stage<GameOfLight> createStageFromNBT(String id, CompoundNBT stageNBT) {
        return null;
    }
}
