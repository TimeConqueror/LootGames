package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.FloorLootGame;

public class GameOfLight extends FloorLootGame<GameOfLight> {


    @Override
    public @Nullable Stage<GameOfLight> createStageFromNBT(String id, CompoundNBT stageNBT) {
        return null;
    }

    @Override
    public int getBoardSize() {
        return 3;
    }
}
