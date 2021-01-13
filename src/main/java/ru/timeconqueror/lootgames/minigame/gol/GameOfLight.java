package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.world.NoteBlockEvent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.api.minigame.StageSerializationType;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.common.config.ConfigGOL;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.common.packet.game.CPGOLSymbolsShown;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.utils.MouseClickType;
import ru.timeconqueror.timecore.api.util.EnumLookup;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameOfLight extends BoardLootGame<GameOfLight> {
    public static final int BOARD_SIZE = 3;

    private int round = 0;
    private int stage = 0;
    private int ticks = 0;

    @Override
    public void onPlace() {
        if (isServerSide()) {
            setupInitialStage(new StageUnderExpanding());
            getWorld().playSound(null, getGameCenter(), LGSounds.GOL_START_GAME, SoundCategory.MASTER, 0.75F, 1.0F);
        }
    }

    private void playFeedbackSound(Symbol symbol) {
        NoteBlockEvent.Note note = NoteBlockEvent.Note.G_SHARP;
        NoteBlockEvent.Octave octave = NoteBlockEvent.Octave.LOW;
        switch (symbol) {
            case NORTH_WEST:
                note = NoteBlockEvent.Note.G;
                break;
            case NORTH:
                note = NoteBlockEvent.Note.A;
                break;
            case NORTH_EAST:
                note = NoteBlockEvent.Note.B;
                break;
            case EAST:
                note = NoteBlockEvent.Note.C;
                break;
            case SOUTH_EAST:
                note = NoteBlockEvent.Note.D;
                break;
            case SOUTH:
                note = NoteBlockEvent.Note.E;
                break;
            case SOUTH_WEST:
                note = NoteBlockEvent.Note.F;
                break;
            case WEST:
                note = NoteBlockEvent.Note.G;
                octave = NoteBlockEvent.Octave.MID;
                break;
        }

        getWorld().playSound(null, convertToBlockPos(symbol.getPos()), SoundEvents.NOTE_BLOCK_HARP, SoundCategory.MASTER, 3.0F, getPitchForNote(note, octave));
    }

    private float getPitchForNote(NoteBlockEvent.Note note, NoteBlockEvent.Octave octave) {
        int noteID = note.ordinal() + octave.ordinal() * 12;
        return (float) Math.pow(2.0D, (double) (noteID - 12) / 12.0D);
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
    public @Nullable BoardStage createStageFromNBT(String id, CompoundNBT stageNBT, StageSerializationType serializationType) {
        switch (id) {
            case StageUnderExpanding.ID:
                return new StageUnderExpanding(stageNBT.getInt("ticks"));
            case StageWaitingStart.ID:
                return new StageWaitingStart();
            case StageShowSequence.ID:
                return new StageShowSequence(stageNBT);
            default:
                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
        }
    }

    @Override
    public void writeNBTForSaving(CompoundNBT nbt) {
        super.writeNBTForSaving(nbt);

        nbt.putInt("round", round);
        nbt.putInt("level", stage);
    }

    @Override
    public void readNBTFromSave(CompoundNBT nbt) {
        super.readNBTFromSave(nbt);

        round = nbt.getInt("round");
        stage = nbt.getInt("level");
    }

    @Override
    public void writeCommonNBT(CompoundNBT nbt) {
        super.writeCommonNBT(nbt);
        nbt.putInt("ticks", ticks);
    }

    @Override
    public void readCommonNBT(CompoundNBT nbt) {
        super.readCommonNBT(nbt);
        ticks = nbt.getInt("ticks");
    }

    private boolean isCenter(Pos2i pos) {
        return convertToBlockPos(pos).equals(getGameCenter());
    }

    @Override
    protected void onStageUpdate(BoardStage oldStage, BoardStage newStage) {
        ticks = 0;
        super.onStageUpdate(oldStage, newStage);
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
        public CompoundNBT serialize(StageSerializationType serializationType) {
            CompoundNBT nbt = super.serialize(serializationType);
            nbt.putInt("ticks", ticks);
            return nbt;
        }
    }

    public class StageWaitingStart extends BoardStage {
        private static final String ID = "waiting_start";

        @Override
        public void init() {
            super.init();
            stage = 0;
            round = 0;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected void onClick(ServerPlayerEntity player, Pos2i pos, MouseClickType type) {
            if (isCenter(pos)) {
                sendTo(player, new TranslationTextComponent("msg.lootgames.gol_master.rules"), NotifyColor.NOTIFY);

                switchStage(new StageShowSequence(stage, true, new ArrayList<>()));
            }
        }
    }

    public class StageShowSequence extends BoardStage {
        private static final int TICKS_PAUSE_BETWEEN_SYMBOLS = 12;
        private static final int TICKS_PAUSE_BETWEEN_ROUNDS = 25;
        private static final String ID = "show_sequence";
        private final List<Integer> sequence;
        private final int displayTime;

        private boolean pauseBeforeShowing = true;
        private int symbolIndex = 0;
        private boolean particleSent = false;
        private boolean feedbackPacketReceived = false;

        public StageShowSequence(CompoundNBT nbt) {
            sequence = Arrays.stream(nbt.getIntArray("sequence")).boxed().collect(Collectors.toList());
            displayTime = nbt.getInt("display_time");
        }

        public StageShowSequence(int stageIndex, boolean isNewStage, List<Integer> prevSequence) {
            ConfigGOL.StageConfig stage = LGConfigs.GOL.getStageByIndex(stageIndex);
            if (isNewStage) {
                if (stageIndex == 0) {
                    sequence = generateSequence(stageIndex, LGConfigs.GOL.startDigitAmount.get());
                } else if (stage.randomizeSequence.get()) {
                    sequence = generateSequence(stageIndex, prevSequence.size() + 1);
                } else {
                    sequence = withNewSymbol(stageIndex, prevSequence);
                }
            } else {
                sequence = withNewSymbol(stageIndex, prevSequence);
            }

            displayTime = stage.displayTime.get();
        }

        @Override
        protected void onTick() {
            if (pauseBeforeShowing) {
                if (ticks > TICKS_PAUSE_BETWEEN_ROUNDS) {
                    ticks = 0;
                    pauseBeforeShowing = false;
                } else {
                    ticks++;
                }
            } else {
                if (isClientSide()) {
                    // if it's a last symbol we don't need to wait for pause.
                    if ((symbolIndex == sequence.size() - 1 && ticks > displayTime) || (ticks > displayTime + TICKS_PAUSE_BETWEEN_SYMBOLS)) {
                        ticks = 0;
                        symbolIndex++;
                        particleSent = false;
                    } else {
                        ticks++;
                    }

                    if (isShowingSymbols() && !particleSent) {
                        Symbol symbol = Symbol.byIndex(symbolIndex);
                        playFeedbackSound(symbol);

                        BlockPos pos = convertToBlockPos(symbol.getPos());

                        spawnFeedbackParticles(ParticleTypes.INSTANT_EFFECT, pos);
                        particleSent = true;
                    }

                    sendFeedbackPacket(new CPGOLSymbolsShown());
                } else {
                    if (feedbackPacketReceived) {
                        feedbackPacketReceived = false;
                        System.out.println("Feedback received!");
//                        switchStage(GameStage.WAITING_FOR_PLAYER_SEQUENCE);
                    }
                }
            }
        }

        private boolean isShowingSymbols() {
            return symbolIndex < sequence.size();
        }

        private void spawnFeedbackParticles(IParticleData particle, BlockPos pos) {
            for (int i = 0; i < 20; i++) {
                getWorld().addParticle(particle, pos.getX() + RandHelper.RAND.nextFloat(), pos.getY() + 0.5F + RandHelper.RAND.nextFloat(), pos.getZ() + RandHelper.RAND.nextFloat(), RandHelper.RAND.nextGaussian() * 0.02D, (0.02D + RandHelper.RAND.nextGaussian()) * 0.02D, RandHelper.RAND.nextGaussian() * 0.02D);
            }
        }

        private List<Integer> generateSequence(int stage, int size) {
            List<Integer> sequence = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                withNewSymbol(stage, sequence);
            }

            return sequence;
        }

        private List<Integer> withNewSymbol(int stage, List<Integer> sequence) {
            if (stage >= LGConfigs.GOL.expandFieldAtStage.get() - 1) {
                sequence.add(RandHelper.RAND.nextInt(8));
            } else {
                sequence.add(RandHelper.RAND.nextInt(4) * 2);
            }

            return sequence;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        public CompoundNBT serialize(StageSerializationType serializationType) {
            CompoundNBT nbt = super.serialize(serializationType);
            nbt.putIntArray("sequence", sequence);
            nbt.putInt("display_time", displayTime);
            return nbt;
        }
    }

    private enum Symbol {
        NORTH_WEST(0, new Pos2i(0, 0)),
        NORTH(1, new Pos2i(1, 0)),
        NORTH_EAST(2, new Pos2i(2, 0)),
        WEST(3, new Pos2i(0, 1)),
        EAST(4, new Pos2i(2, 1)),
        SOUTH_WEST(5, new Pos2i(0, 2)),
        SOUTH(6, new Pos2i(1, 2)),
        SOUTH_EAST(7, new Pos2i(2, 2));

        private static final EnumLookup<Symbol, Integer> LOOKUP = EnumLookup.make(Symbol.class, Symbol::getIndex);

        private final int index;
        private final Pos2i pos;

        Symbol(int index, Pos2i pos) {
            this.index = index;
            this.pos = pos;
        }

        public Pos2i getPos() {
            return pos;
        }

        public int getIndex() {
            return index;
        }

        public static Symbol byIndex(int index) {
            return LOOKUP.get(index);
        }
    }
}
