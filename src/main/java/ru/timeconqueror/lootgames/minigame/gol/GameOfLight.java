package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;

public class GameOfLight extends BoardLootGame<GameOfLight> {
    public static final int BOARD_SIZE = 3;

    @Override
    public @Nullable Stage<GameOfLight> createStageFromNBT(String id, CompoundNBT stageNBT) {
        return null;
    }

    @Override
    public int getBoardSize() {
        return BOARD_SIZE;
    }
}
