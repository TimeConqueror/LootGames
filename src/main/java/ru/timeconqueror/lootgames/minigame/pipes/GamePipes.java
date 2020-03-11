package ru.timeconqueror.lootgames.minigame.pipes;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.pipes.board.PipesBoard;

public class GamePipes extends LootGame<GamePipes> {

    private PipesBoard board;
    private float difficulty;

    public GamePipes(int size, float difficulty) {
        this.board = new PipesBoard(size);
        this.difficulty = difficulty;
    }

    @Override
    protected BlockPos getCentralRoomPos() {
        return masterTileEntity.getPos().add(getBoardSize() / 2, 0, getBoardSize() / 2);
    }

    @Override
    protected BlockPos getRoomFloorPos() {
        return getMasterPos();
    }

    @Override
    public Stage createStageFromNBT(CompoundNBT stageNBT) {
        return null;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public int getBoardSize() {
        return board.getSize();
    }

    public void clickField(ServerPlayerEntity player, Pos2i pos) {

    }
}
