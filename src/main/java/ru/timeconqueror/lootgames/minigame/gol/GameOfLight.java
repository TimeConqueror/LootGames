package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.registry.LGSounds;

public class GameOfLight extends BoardLootGame<GameOfLight> {
    public static final int BOARD_SIZE = 3;

    @Override
    public void onPlace() {
        if (isServerSide()) {
            setupInitialStage(new StageUnderExpanding());
            getWorld().playSound(null, getGameCenter(), LGSounds.GOL_START_GAME, SoundCategory.MASTER, 0.75F, 1.0F);
        }
    }

    @Override
    public int getCurrentBoardSize() {
        return getAllocatedBoardSize();
    }

    @Override
    public int getAllocatedBoardSize() {
        return BOARD_SIZE;
    }

    @Override
    public @Nullable BoardStage createStageFromNBT(String id, CompoundNBT stageNBT) {
        switch (id) {
            case StageUnderExpanding.ID:
                return new StageUnderExpanding(stageNBT.getInt("ticks"));
            case StageWaitingStart.ID:
                return new StageWaitingStart();
            default:
                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
        }
    }

    public class StageUnderExpanding extends BoardStage {
        private static final String ID = "under_expanding";
        public static final int MAX_TICKS_EXPANDING = 20;
        private int ticks;

        public StageUnderExpanding() {
            this(0);
        }

        public StageUnderExpanding(int ticks) {
            this.ticks = ticks;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected void onTick() {
            if (ticks > MAX_TICKS_EXPANDING) {
                if (isServerSide()) {
                    switchStage(new StageWaitingStart());
                }
            } else {
                ticks++;
            }
        }

        public int getTicks() {
            return ticks;
        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT nbt = super.serialize();
            nbt.putInt("ticks", ticks);
            return nbt;
        }
    }

    public class StageWaitingStart extends BoardStage {
        private static final String ID = "waiting_start";

        @Override
        public String getID() {
            return ID;
        }

        private void startGame(ServerPlayerEntity player) {
            sendTo(player, new TranslationTextComponent("msg.lootgames.gol_master.rules"), NotifyColor.NOTIFY);

//            currentRound = 0;
//            gameLevel = 1;
//
//            onGameLevelChanged();
//
//            generateSequence(LGConfigGOL.startDigitAmount);
//
//            updateGameStage(GameStage.SHOWING_SEQUENCE);
        }
    }
}
