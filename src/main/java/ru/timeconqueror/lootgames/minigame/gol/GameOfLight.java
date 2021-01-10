package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.utils.MouseClickType;

public class GameOfLight extends BoardLootGame<GameOfLight> {
    public static final int BOARD_SIZE = 3;

    @Override
    public int getCurrentBoardSize() {
        return getAllocatedBoardSize();
    }

    @Override
    public int getAllocatedBoardSize() {
        return BOARD_SIZE;
    }

    @Override
    public void onClick(ServerPlayerEntity player, Pos2i pos, MouseClickType type) {
        if (getStage() instanceof StageNotConstructed) {//todo move to stage.onClick?
            ((StageNotConstructed) getStage()).onClick(player, pos);
        }
//        if (stage instanceof StageNotConstructed) {
//            ((StageNotConstructed) stage).generateSubordinates(player, pos);
//        } else if(stage instanceof Stage)
    }

    @Override
    public @Nullable BoardStage createStageFromNBT(String id, CompoundNBT stageNBT) {
        switch (id) {
            case StageNotConstructed.ID:
                return new StageNotConstructed();
            default:
                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
        }
    }

    public class StageNotConstructed extends BoardStage {
        private static final String ID = "not_constructed";

        @Override
        public String getID() {
            return ID;
        }

        private void onClick(ServerPlayerEntity player, Pos2i pos) {
            getWorld().playSound(null, convertToBlockPos(pos), LGSounds.GOL_START_GAME, SoundCategory.MASTER, 0.75F, 1.0F);

            sendTo(player, new TranslationTextComponent("msg.lootgames.gol_master.start"), NotifyColor.NOTIFY);

            switchStage(new StageUnderExpanding());
        }
    }

    public class StageUnderExpanding extends BoardStage {
        private static final String ID = "under_expanding";
        public static final int MAX_TICKS_EXPANDING = 20;
        private int ticks;

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
