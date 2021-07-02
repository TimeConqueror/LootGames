package ru.timeconqueror.lootgames.minigame.gol;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.NoteBlockEvent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.api.minigame.ILootGameFactory;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.api.util.RewardUtils;
import ru.timeconqueror.lootgames.common.config.ConfigGOL;
import ru.timeconqueror.lootgames.common.config.ConfigGOL.Fail;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.common.packet.game.CPGOLSymbolsShown;
import ru.timeconqueror.lootgames.common.packet.game.SPGOLDrawMark;
import ru.timeconqueror.lootgames.common.packet.game.SPGOLSendDisplayedSymbol;
import ru.timeconqueror.lootgames.common.packet.game.SPGOLSpawnStageUpParticles;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.utils.MouseClickType;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;
import ru.timeconqueror.timecore.api.util.RandHelper;
import ru.timeconqueror.timecore.api.util.client.ClientProxy;

import java.util.*;
import java.util.stream.Collectors;

//TODO add question mark flicker on WaitingStartStage
//TODO add reward giving upon some combination
public class GameOfLight extends BoardLootGame<GameOfLight> {
    public static final String ADV_BEAT_LEVEL3 = "gol_level_3";
    public static final String ADV_BEAT_LEVEL4 = "gol_level_4";

    public static final int BOARD_SIZE = 3;

    private int round = 0;
    private int stage = 0;
    private int ticks = 0;
    private int attempt = 1;
    private int maxReachedStage = 0;

    private Timer resetTimer;
    private boolean tickTimer;

    private final List<DisplayedSymbol> displayedSymbols = new ArrayList<>();

    @Nullable
    private QMarkAppearance specialMarkAppearance = null;

    public GameOfLight() {
        resetTimer = new Timer(0);
        resetTimer();
    }

    @Override
    public void onPlace() {
        if (isServerSide()) {
            setupInitialStage(new StageUnderExpanding());
            getWorld().playSound(null, getGameCenter(), LGSounds.GOL_START_GAME, SoundCategory.MASTER, 0.75F, 1.0F);
        }
    }

    private void resetTimer() {
        resetTimer.reset(LGConfigs.GOL.timeout.get() * 20);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (isServerSide()) {
            if (tickTimer && resetTimer.ended()) {
                failGame(true);
                return;
                //TODO add animation of hard switch to waiting for sequence?
            }

            if (!tickTimer) {
                resetTimer();
            } else {
                resetTimer.update();
            }
        } else {
            displayedSymbols.removeIf((symbol) -> System.currentTimeMillis() - symbol.getClickedTime() > 600);

            if (specialMarkAppearance != null && specialMarkAppearance.isFinished()) {
                specialMarkAppearance = null;
            }
        }
    }

    private void failGame(boolean dueTimeout) {
        getWorld().playSound(null, getGameCenter(), LGSounds.GOL_SEQUENCE_WRONG, SoundCategory.MASTER, dueTimeout ? 0.2F : 0.75F, 1.0F);
        sendToNearby(new TranslationTextComponent("msg.lootgames.gol.wrong_block"), NotifyColor.FAIL);
        sendUpdatePacketToNearby(SPGOLDrawMark.denied());

        if (attempt >= LGConfigs.GOL.attemptCount.get()) {
            if (maxReachedStage == 0) {
                triggerGameLose();
            } else {
                triggerGameWin();
            }
        } else {
            attempt++;

            getWorld().playSound(null, getGameCenter(), LGSounds.GOL_START_GAME, SoundCategory.MASTER, dueTimeout ? 0.2F : 0.75F, 1.0F);
            switchStage(new StageWaitingStart());
        }
    }

    private void playFeedbackSound(PlayerEntity player, Symbol symbol) {
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

        getWorld().playSound(player, convertToBlockPos(symbol.getPos()), SoundEvents.NOTE_BLOCK_HARP, SoundCategory.MASTER, 3.0F, getPitchForNote(note, octave));
    }

    private float getPitchForNote(NoteBlockEvent.Note note, NoteBlockEvent.Octave octave) {
        int noteID = note.ordinal() + octave.ordinal() * 12;
        return (float) Math.pow(2.0D, (double) (noteID - 12) / 12.0D);
    }

    @Override
    protected void triggerGameLose() {
        super.triggerGameLose();

        World world = getWorld();

        EnumSet<Fail> fails = LGConfigs.GOL.getAllowedFails();
        if (fails.isEmpty()) return;

        Fail fail = RandHelper.chooseEqually(fails);

        BlockPos center = getGameCenter();
        if (fail == Fail.EXPLOSION) {
            world.explode(null, center.getX(), center.getY() + 1.5, center.getZ(), 9, Explosion.Mode.DESTROY);
        } else if (fail == Fail.ZOMBIES) {
            for (int i = 0; i < 10; i++) {
                ZombieEntity zombie = new ZombieEntity(world);
                zombie.moveTo(center.getX() + RandHelper.RAND.nextFloat() * 2, center.getY() + 1, center.getZ() + RandHelper.RAND.nextFloat() * 2, MathHelper.wrapDegrees(RandHelper.RAND.nextFloat() * 360.0F), 0.0F);
                world.addFreshEntity(zombie);

                if (RandHelper.RAND.nextBoolean()) {
                    zombie.playAmbientSound();
                }
            }
        } else if (fail == Fail.LAVA) {
            BlockPos.Mutable mutable = center.mutable();
            for (int x = -5; x <= 5; x++)
                for (int z = -5; z <= 5; z++)
                    for (int y = 1; y < 3; y++) {
                        mutable.set(center.getX(), center.getY(), center.getZ());

                        BlockState state = world.getBlockState(mutable.move(x, y, z));
                        if (state.getMaterial().isReplaceable()) {
                            world.setBlock(center.offset(x, y, z), Blocks.LAVA.defaultBlockState(), 3);
                        }
                    }
        } else {
            throw new IllegalArgumentException("Unknown fail type: " + fail);
        }
    }

    @Override
    protected void triggerGameWin() {
        super.triggerGameWin();

        forEachPlayerNearby(player -> {
            sendTo(player, new TranslationTextComponent("msg.lootgames.gol.reward_level_info", stage + 1, maxReachedStage), NotifyColor.SUCCESS);

            if (maxReachedStage >= 3) {
                LGAdvancementTriggers.END_GAME.trigger(player, ADV_BEAT_LEVEL3);
            }

            if (maxReachedStage == 4) {
                LGAdvancementTriggers.END_GAME.trigger(player, ADV_BEAT_LEVEL4);
            }
        });

        RewardUtils.spawnFourStagedReward(((ServerWorld) getWorld()), this, getGameCenter(), maxReachedStage, LGConfigs.REWARDS.gol);
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
    public @Nullable BoardStage createStageFromNBT(String id, CompoundNBT stageNBT, SerializationType serializationType) {
        switch (id) {
            case StageUnderExpanding.ID:
                return new StageUnderExpanding(stageNBT.getInt("ticks"));
            case StageWaitingStart.ID:
                return new StageWaitingStart();
            case StageShowSequence.ID:
                return serializationType != SerializationType.SAVE
                        ? new StageShowSequence(stageNBT)
                        : new StageWaitingStart(); /*resetting if world was reloaded while game was in show mode*/
            case StageWaitingForSequence.ID:
                return serializationType == SerializationType.SAVE
                        ? new StageWaitingForSequence(stageNBT)
                        : new StageWaitingForSequence(Collections.emptyList());
            default:
                throw new IllegalArgumentException("Unknown state with id: " + id + "!");
        }
    }

    @Override
    public void writeNBT(CompoundNBT nbt, SerializationType type) {
        super.writeNBT(nbt, type);

        if (type == SerializationType.SAVE) {
            nbt.putInt("round", round);
            nbt.putInt("level", stage);
            nbt.putInt("attempt", attempt);

            nbt.put("reset_timer", Timer.toNBT(resetTimer));
        }

        nbt.putInt("ticks", ticks);
    }

    @Override
    public void readNBT(CompoundNBT nbt, SerializationType type) {
        super.readNBT(nbt, type);

        if (type == SerializationType.SAVE) {
            round = nbt.getInt("round");
            stage = nbt.getInt("level");
            attempt = nbt.getInt("attempt");
            resetTimer = Timer.fromNBT(nbt.get("reset_timer"));
        }

        ticks = nbt.getInt("ticks");
    }

    private boolean isCenter(Pos2i pos) {
        return !Symbol.exists(pos);
    }

    @Override
    protected void onStageUpdate(BoardStage oldStage, BoardStage newStage) {
        ticks = 0;
        tickTimer = false;
        super.onStageUpdate(oldStage, newStage);
    }

    public void spawnFeedbackParticles(IParticleData particle, BlockPos pos) {
        if (isClientSide()) {
            for (int i = 0; i < 20; i++) {
                getWorld().addParticle(particle, pos.getX() + RandHelper.RAND.nextFloat(), pos.getY() + 1F + RandHelper.RAND.nextFloat() / 2, pos.getZ() + RandHelper.RAND.nextFloat(), RandHelper.RAND.nextGaussian() * 0.02D, (0.02D + RandHelper.RAND.nextGaussian()) * 0.02D, RandHelper.RAND.nextGaussian() * 0.02D);
            }
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
        public CompoundNBT serialize(SerializationType serializationType) {
            CompoundNBT nbt = super.serialize(serializationType);
            nbt.putInt("ticks", ticks);
            return nbt;
        }
    }

    public class StageWaitingStart extends BoardStage {
        private static final String ID = "waiting_start";

        @Override
        public void preInit() {
            super.preInit();
            stage = 0;
            round = 0;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected void onClick(PlayerEntity player, Pos2i pos, MouseClickType type) {
            super.onClick(player, pos, type);

            if (isServerSide()) {
                if (isCenter(pos)) {
                    sendTo(player, new TranslationTextComponent("msg.lootgames.gol.rules"), NotifyColor.NOTIFY);

                    switchStage(new StageShowSequence(true, new ArrayList<>()));
                } else {
                    sendTo(player, new TranslationTextComponent("msg.lootgames.gol.click_center"), NotifyColor.WARN);
                }
            }
        }
    }

    public class StageShowSequence extends BoardStage {
        private static final int TICKS_PAUSE_BETWEEN_SYMBOLS = 12;
        private static final int TICKS_PAUSE_BETWEEN_ROUNDS = 25;
        private static final int FEEDBACK_WAIT_TICKS = 8 * 20;
        private static final String ID = "show_sequence";
        private final List<Symbol> sequence;
        private final int displayTime;

        private boolean pauseBeforeShowing = true;
        private int symbolIndex = 0;

        private boolean feedbackPacketReceived = false;
        private boolean cFeedbackPacketSent = false;

        /**
         * Timer in case client can't or don't want to send packet about finishing watching sequence
         */
        private final Timer feedbackWaitTimer = new Timer(FEEDBACK_WAIT_TICKS);

        public StageShowSequence(CompoundNBT nbt) {
            sequence = deserializeSequence(nbt.getIntArray("sequence"));
            displayTime = nbt.getInt("display_time");
        }

        public StageShowSequence(boolean isNewStage, List<Symbol> prevSequence) {
            int stageIndex = stage;
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
                    if (ticks == Math.max(20, TICKS_PAUSE_BETWEEN_ROUNDS - 5)) {
                        displayedSymbols.clear(); // hard reset, because now we will show new symbols
                    }

                    ticks++;
                }
            } else {
                // makes sound and spawn particles before the first symbol. Others will be handled in the next if-statement
                boolean showParticles = symbolIndex == 0 && ticks == 0;

                // if it's a last symbol we don't need to wait for pause.
                if ((symbolIndex == sequence.size() - 1 && ticks > displayTime) || (ticks > displayTime + TICKS_PAUSE_BETWEEN_SYMBOLS)) {
                    ticks = 0;
                    symbolIndex++;
                    showParticles = true;
                } else {
                    ticks++;
                }

                if (isClientSide()) {
                    if (isShowingSymbols() && showParticles) {
                        Symbol symbol = sequence.get(symbolIndex);
                        playFeedbackSound(ClientProxy.player(), symbol);

                        BlockPos pos = convertToBlockPos(symbol.getPos());
                        spawnFeedbackParticles(ParticleTypes.INSTANT_EFFECT, pos);
                    }

                    if (!isShowingSymbols() && !cFeedbackPacketSent) {
                        sendFeedbackPacket(new CPGOLSymbolsShown());
                        cFeedbackPacketSent = true;
                    }
                } else {
                    if (feedbackPacketReceived) {
                        // at least one watched the full sequence, switching...
                        switchStage(new StageWaitingForSequence(sequence));
                        return;
                    }

                    if (!isShowingSymbols()) {
                        //hard switch to waiting-for-sequence stage if no one want to send a reply
                        if (feedbackWaitTimer.ended()) {
                            switchStage(new StageWaitingForSequence(sequence));
                            return;
                        }

                        feedbackWaitTimer.update();
                    }
                }
            }
        }

        public void onSequenceShown() {
            this.feedbackPacketReceived = true;
        }

        @Override
        protected void onClick(PlayerEntity player, Pos2i pos, MouseClickType type) {
            super.onClick(player, pos, type);

            if (isServerSide()) {
                sendTo(player, new TranslationTextComponent("msg.lootgames.gol.not_ready"), NotifyColor.WARN);
            }
        }

        private boolean isShowingSymbols() {
            return !pauseBeforeShowing && symbolIndex < sequence.size();
        }

        public boolean shouldRenderSymbol() {
            return ticks < displayTime && isShowingSymbols();
        }

        @Nullable
        public Symbol getSymbolForRender() {
            return shouldRenderSymbol() ? sequence.get(symbolIndex) : null;
        }

        private List<Symbol> generateSequence(int stage, int size) {
            List<Symbol> sequence = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                withNewSymbol(stage, sequence);
            }

            return sequence;
        }

        private List<Symbol> withNewSymbol(int stage, List<Symbol> sequence) {
            if (stage >= LGConfigs.GOL.expandFieldAtStage.get() - 1) {
                sequence.add(RandHelper.chooseEqually(Symbol.values()));
            } else {
                sequence.add(RandHelper.chooseEqually(Symbol.NWES_SYMBOLS));
            }

            return sequence;
        }

        @Override
        public String getID() {
            return ID;
        }

        @Override
        public CompoundNBT serialize(SerializationType serializationType) {
            CompoundNBT nbt = super.serialize(serializationType);
            nbt.putIntArray("sequence", serializeSequence(sequence));
            nbt.putInt("display_time", displayTime);

            return nbt;
        }
    }

    public class StageWaitingForSequence extends BoardStage {
        private static final String ID = "waiting_for_sequence";

        private final List<Symbol> sequence;
        private int currentSymbol;

        public StageWaitingForSequence(List<Symbol> sequence) {
            this.sequence = sequence;
        }

        public StageWaitingForSequence(CompoundNBT nbt) {
            this.sequence = deserializeSequence(nbt.getIntArray("sequence"));
            this.currentSymbol = nbt.getInt("symbol_index");
        }

        @Override
        protected void onStart(boolean clientSide) {
            tickTimer = true;
        }

        @Override
        protected void onClick(PlayerEntity player, Pos2i pos, MouseClickType type) {
            if (isClientSide() && isCenter(pos)) {
                sendTo(player, new TranslationTextComponent("msg.lootgames.gol.rules"), NotifyColor.WARN);
                return;
            }

            if (!isCenter(pos)) {
                Symbol chosen = Symbol.byPos(pos);

                playFeedbackSound(player, chosen);
                if (isClientSide()) {
                    addDisplayedSymbol(chosen);
                } else {
                    sendUpdatePacketToNearbyExcept(((ServerPlayerEntity) player), new SPGOLSendDisplayedSymbol(chosen));

                    Symbol correct = sequence.get(currentSymbol);
                    if (chosen != correct) {
                        failGame(false);
                        return;
                    }

                    if (currentSymbol == sequence.size() - 1) {
                        onSuccessSequence((ServerPlayerEntity) player);
                    } else {
                        currentSymbol++;
                    }
                }
            }
        }

        private void onSuccessSequence(ServerPlayerEntity player) {
            getWorld().playSound(null, getGameCenter(), LGSounds.GOL_SEQUENCE_COMPLETE, SoundCategory.MASTER, 0.75F, 1.0F);
            sendUpdatePacketToNearby(SPGOLDrawMark.accepted());

            ConfigGOL.StageConfig stageCfg = LGConfigs.GOL.getStageByIndex(stage);
            Integer maxRounds = stageCfg.rounds.get();
            if (round == maxRounds - 1) {//move to the next stage
                if (stage == 3) {
                    maxReachedStage = 4;
                    save();
                    triggerGameWin();
                } else {
                    stage++;
                    round = 0;
                    maxReachedStage = Math.max(maxReachedStage, stage);
                    save();

                    getWorld().playSound(null, getGameCenter(), SoundEvents.PLAYER_LEVELUP, SoundCategory.MASTER, 0.75F, 1.0F);
                    sendUpdatePacketToNearby(new SPGOLSpawnStageUpParticles());
                    sendToNearby(new TranslationTextComponent("msg.lootgames.stage_complete"), NotifyColor.SUCCESS);

                    switchStage(new StageShowSequence(true, sequence));
                }
            } else {
                round++;
                save();

                switchStage(new StageShowSequence(false, sequence));
            }
        }

        @Override
        public CompoundNBT serialize(SerializationType serializationType) {
            CompoundNBT nbt = super.serialize(serializationType);

            if (serializationType == SerializationType.SAVE) {
                nbt.putIntArray("sequence", serializeSequence(sequence));
                nbt.putInt("symbol_index", currentSymbol);
            }

            return nbt;
        }

        @Override
        public String getID() {
            return ID;
        }

    }

    public void addDisplayedSymbol(Symbol symbol) {
        if (isClientSide()) {
            displayedSymbols.add(new DisplayedSymbol(System.currentTimeMillis(), symbol));
        }
    }

    public void addMarkAppearance(QMarkAppearance.State state) {
        if (isClientSide() && QMarkAppearance.canBeHandled(state)) {
            specialMarkAppearance = new QMarkAppearance(state, getWorld().getGameTime());
        }
    }

    public List<DisplayedSymbol> getDisplayedSymbols() {
        return displayedSymbols;
    }

    public QMarkAppearance.State getMarkState() {
        return specialMarkAppearance != null ? specialMarkAppearance.getState() : getStage() instanceof StageShowSequence ? QMarkAppearance.State.SHOWING : QMarkAppearance.State.NONE;
    }

    private static List<Symbol> deserializeSequence(int[] data) {
        return Arrays.stream(data).mapToObj(Symbol::byIndex).collect(Collectors.toList());
    }

    private static int[] serializeSequence(List<Symbol> sequence) {
        return sequence.stream().mapToInt(Symbol::getIndex).toArray();
    }

    public static class Factory implements ILootGameFactory {
        @Override
        public void genOnPuzzleMasterClick(World world, BlockPos puzzleMasterPos) {
            BlockPos floorCenterPos = puzzleMasterPos.offset(0, -2, 0);//TODo make puzzle master be also manually placeable and in this case in should spawn activator in its place
            world.setBlockAndUpdate(floorCenterPos, LGBlocks.GOL_ACTIVATOR.defaultBlockState());
        }
    }
}
