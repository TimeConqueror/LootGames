package ru.timeconqueror.lootgames.minigame.gameoflight;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.achievement.AdvancementManager;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;
import ru.timeconqueror.lootgames.api.util.DirectionOctagonal;
import ru.timeconqueror.lootgames.api.util.GameUtils;
import ru.timeconqueror.lootgames.block.BlockDungeonLamp;
import ru.timeconqueror.lootgames.block.BlockGOLSubordinate;
import ru.timeconqueror.lootgames.config.LGConfigGOL;
import ru.timeconqueror.lootgames.packets.NetworkHandler;
import ru.timeconqueror.lootgames.packets.SMessageGOLDrawStuff;
import ru.timeconqueror.lootgames.packets.SMessageGOLParticle;
import ru.timeconqueror.lootgames.registry.ModBlocks;
import ru.timeconqueror.lootgames.registry.ModSounds;
import ru.timeconqueror.lootgames.world.gen.DungeonGenerator;
import ru.timeconqueror.timecore.api.auxiliary.DirectionTetra;
import ru.timeconqueror.timecore.api.auxiliary.MessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
//TODO add current level reached and attempts remain message
public class TileEntityGOLMaster extends TileEntityGameMaster<GameOfLight> implements ITickable {
    static final int MAX_TICKS_EXPANDING = 20;
    private static final int TICKS_PAUSE_BETWEEN_SYMBOLS = 12;
    private static final int TICKS_PAUSE_BETWEEN_STAGES = 25;
    static int ticksPerShowSymbols = 24;
    private int currentRound = -1;
    private int gameLevel = 1;
    private int ticks = 0;
    private int symbolIndex = 0;
    private boolean particleSent = false;
    private boolean pauseBeforeShowing = true;
    private List<ClickInfo> symbolsEnteredByPlayer;
    private int timeout = 0;

    private GameStage gameStage;
    private List<Integer> symbolSequence;
    private List<Integer> maxLevelBeatList;
    private List<DrawInfo> stuffToDraw;

    private boolean feedbackPacketReceived;

    public TileEntityGOLMaster() {
        super(new GameOfLight());//TODO move game data there
        gameStage = GameStage.NOT_CONSTRUCTED;
    }

    @Override
    public void update() {
        if (gameStage == GameStage.NOT_CONSTRUCTED) {
            return;
        }

        if (gameStage == GameStage.UNDER_EXPANDING) {
            if (ticks > MAX_TICKS_EXPANDING) {
                if (!world.isRemote) {
                    updateGameStage(GameStage.WAITING_FOR_START);
                }
            } else {
                ticks++;
            }
        }

        if (gameStage == GameStage.SHOWING_SEQUENCE) {
            if (pauseBeforeShowing) {
                if (ticks > TICKS_PAUSE_BETWEEN_STAGES) {
                    ticks = 0;
                    pauseBeforeShowing = false;
                } else {
                    ticks++;
                }
            } else {
                if (world.isRemote) {
                    // if it's a last symbol we don't need to wait for pause.
                    if ((symbolIndex == symbolSequence.size() - 1 && ticks > ticksPerShowSymbols) || (ticks > ticksPerShowSymbols + TICKS_PAUSE_BETWEEN_SYMBOLS)) {
                        ticks = 0;
                        symbolIndex++;
                        particleSent = false;
                    } else {
                        ticks++;
                    }

                    if (isShowingSymbols() && !particleSent) {
                        playFeedbackSound(getCurrentSymbolPosOffset());
                        spawnFeedbackParticles(EnumParticleTypes.SPELL, getPos().add(getCurrentSymbolPosOffset().getOffsetX(), 0, getCurrentSymbolPosOffset().getOffsetZ()));
                        particleSent = true;
                    }
                } else {
                    if (feedbackPacketReceived) {
                        feedbackPacketReceived = false;
                        updateGameStage(GameStage.WAITING_FOR_PLAYER_SEQUENCE);
                    }
                }
            }
        }

        if (!world.isRemote && gameStage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            if (timeout >= LGConfigGOL.timeout * 20) {
                updateGameStage(GameStage.WAITING_FOR_START);
            }

            timeout++;
        }

        if (world.isRemote && symbolsEnteredByPlayer != null) {
            symbolsEnteredByPlayer.removeIf((clickInfo) -> System.currentTimeMillis() - clickInfo.msClickedTime > 800);
        }

        if (world.isRemote && stuffToDraw != null && !stuffToDraw.isEmpty()) {
            stuffToDraw.removeIf((stuff) -> System.currentTimeMillis() - stuff.msClickedTime > 800);
        }
    }

    private void generateSubordinates(EntityPlayer player) {
        world.playSound(null, pos, ModSounds.golStartGame, SoundCategory.BLOCKS, 0.75F, 1.0F);

        for (DirectionOctagonal value : DirectionOctagonal.values()) {
            IBlockState state = ModBlocks.GOL_SUBORDINATE.getDefaultState().withProperty(BlockGOLSubordinate.OFFSET, value);
            world.setBlockState(pos.add(value.getOffsetX(), 0, value.getOffsetZ()), state);
        }

        player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.start"), TextFormatting.AQUA));

        updateGameStage(GameStage.UNDER_EXPANDING);
    }

    public void onBlockClickedByPlayer(EntityPlayer player) {
        if (gameStage == GameStage.NOT_CONSTRUCTED) {
            generateSubordinates(player);
            return;
        }

        if (gameStage == GameStage.WAITING_FOR_START) {
            startGame(player);
            return;
        }

        if (gameStage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.rules"), TextFormatting.GOLD));
            return;
        }

        player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.not_ready"), TextFormatting.AQUA));
    }

    public void onSubordinateClickedByPlayer(DirectionOctagonal subordinateOffset, EntityPlayer player) {
        if (gameStage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            if (world.isRemote) {
                symbolsEnteredByPlayer.add(new ClickInfo(System.currentTimeMillis(), subordinateOffset));
                playFeedbackSound(subordinateOffset);
            } else {
                timeout = 0;
                checkPlayerReply(subordinateOffset, player);
            }

            return;
        }

        if (!world.isRemote) {
            player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.not_ready"), TextFormatting.AQUA));
        }
    }

    private void checkPlayerReply(DirectionOctagonal subordinateOffset, EntityPlayer player) {
        if (DirectionOctagonal.byIndex(symbolSequence.get(symbolIndex)) == subordinateOffset) {

            //If all sequence was replied
            if (symbolIndex == symbolSequence.size() - 1) {
                currentRound++;

                if (currentRound >= LGConfigGOL.stage4.getMinRoundsRequiredToPass(world.provider.getDimension())) {
                    gameLevel = 4;
                    onGameEnded(player);
                    return;
                }

                NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 15);
                if (currentRound >= LGConfigGOL.getStageByIndex(gameLevel).getMinRoundsRequiredToPass(world.provider.getDimension())) {
                    NetworkHandler.INSTANCE.sendToAllAround(new SMessageGOLParticle(getPos(), EnumParticleTypes.VILLAGER_HAPPY.getParticleID()), point);
                    player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.stage_complete"), TextFormatting.GREEN));
                    world.playSound(null, getPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.75F, 1.0F);

                    gameLevel++;
                    onGameLevelChanged();
                }

                NetworkHandler.INSTANCE.sendToAllAround(new SMessageGOLDrawStuff(getPos(), EnumDrawStuff.SEQUENCE_ACCEPTED.ordinal()), point);
                world.playSound(null, getPos(), ModSounds.golSequenceComplete, SoundCategory.BLOCKS, 0.75F, 1.0F);

                if (LGConfigGOL.getStageByIndex(gameLevel).randomizeSequence) {
                    generateSequence(symbolSequence.size() + 1);//TODO add config for randomizing only at beginning of stage, change then langs
                } else {
                    addRandSymbolToSequence();
                }

                updateGameStage(GameStage.SHOWING_SEQUENCE);
            }

            symbolIndex++;
        } else {
            world.playSound(null, pos, ModSounds.golSequenceWrong, SoundCategory.BLOCKS, 0.75F, 1.0F);
            NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 15);
            NetworkHandler.INSTANCE.sendToAllAround(new SMessageGOLDrawStuff(getPos(), EnumDrawStuff.SEQUENCE_DENIED.ordinal()), point);

            player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.wrong_block"), TextFormatting.DARK_PURPLE));

            maxLevelBeatList.add(gameLevel - 1);
            onGameEnded(player);
        }
    }

    private void onGameLevelChanged() {
        ticksPerShowSymbols = LGConfigGOL.getStageByIndex(gameLevel).displayTime;
    }

    private void generateSequence(int size) {
        symbolSequence = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            addRandSymbolToSequence();
        }
    }

    private void addRandSymbolToSequence() {
        if (symbolSequence == null) {
            symbolSequence = new ArrayList<>();
        }

        if (gameLevel >= LGConfigGOL.expandFieldAtStage) {
            symbolSequence.add(LootGames.RAND.nextInt(8));
        } else {
            symbolSequence.add(LootGames.RAND.nextInt(4) * 2);
        }
    }

    private void startGame(EntityPlayer player) {
        player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.rules"), TextFormatting.GOLD));

        currentRound = 0;
        gameLevel = 1;

        onGameLevelChanged();

        generateSequence(LGConfigGOL.startDigitAmount);

        updateGameStage(GameStage.SHOWING_SEQUENCE);
    }

    @SideOnly(Side.CLIENT)
    private void playFeedbackSound(DirectionOctagonal offset) {
        NoteBlockEvent.Note note = NoteBlockEvent.Note.G_SHARP;
        NoteBlockEvent.Octave octave = NoteBlockEvent.Octave.LOW;
        switch (offset) {
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

        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, 3.0F, getPitchForNote(note, octave), false);
    }

    private float getPitchForNote(NoteBlockEvent.Note note, NoteBlockEvent.Octave octave) {
        int noteID = note.ordinal() + octave.ordinal() * 12;
        return (float) Math.pow(2.0D, (double) (noteID - 12) / 12.0D);
    }

    public void onClientThingsDone() {
        feedbackPacketReceived = true;
    }

    @Override
    protected void readCommonNBT(NBTTagCompound compound) {
        super.readCommonNBT(compound);
        ticks = compound.getInteger("ticks");
        gameStage = GameStage.values()[compound.getInteger("game_stage")];
        onStageSetting(gameStage);

        timeout = compound.getInteger("timeout");
        currentRound = compound.getInteger("current_round");
        gameLevel = compound.getInteger("game_level");

        symbolSequence = Arrays.stream(compound.getIntArray("symbol_sequence")).boxed().collect(Collectors.toList());
        maxLevelBeatList = Arrays.stream(compound.getIntArray("max_level_beat")).boxed().collect(Collectors.toList());
    }

    @Override
    protected NBTTagCompound writeCommonNBT(NBTTagCompound compound) {
        compound = super.writeCommonNBT(compound);
        compound.setInteger("ticks", ticks);
        compound.setInteger("game_stage", gameStage.ordinal());
        compound.setInteger("game_level", gameLevel);
        compound.setInteger("current_round", currentRound);
        compound.setInteger("timeout", timeout);

        if (symbolSequence != null) {
            compound.setIntArray("symbol_sequence", symbolSequence.stream().mapToInt(i -> i).toArray());
        }

        if (maxLevelBeatList != null) {
            compound.setIntArray("max_level_beat", maxLevelBeatList.stream().mapToInt(i -> i).toArray());
        }

        return compound;
    }

    private void onGameEnded(EntityPlayer player) {
        if (isLastStagePassed() ||
                (maxLevelBeatList.size() == LGConfigGOL.maxAttempts + 1 && getBestLevelReached() > 0)) {
            onGameWon(player);
        } else if (maxLevelBeatList.size() < LGConfigGOL.maxAttempts + 1) {
            world.playSound(null, getPos(), ModSounds.golStartGame, SoundCategory.BLOCKS, 0.75F, 1.0F);
            startGame(player);
        } else {
            onGameLost(player);
        }
    }

    private void onGameWon(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            AdvancementManager.WIN_GAME.trigger(((EntityPlayerMP) player), "win");
        }

        if (maxLevelBeatList.size() == LGConfigGOL.maxAttempts + 1 && getBestLevelReached() > 0) {
            gameLevel -= 1;
        }

        player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.win"), TextFormatting.GREEN));

        world.playSound(null, getPos(), ModSounds.golGameWin, SoundCategory.BLOCKS, 0.75F, 1.0F);

        int bestLevelReached = getBestLevelReached();
        if (bestLevelReached != -1) {
            player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.gol_master.reward_level_reached", gameLevel, isLastStagePassed() ? 4 : bestLevelReached), TextFormatting.GREEN));
        }

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                world.setBlockToAir(pos.add(x, 0, z));
            }
        }

        destroyStructure();

        genLootChests(player);
    }

    private void onGameLost(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            AdvancementManager.WIN_GAME.trigger(((EntityPlayerMP) player), "lose");
        }

        world.playSound(null, getPos(), ModSounds.golGameLose, SoundCategory.BLOCKS, 0.75F, 1.0F);
        player.sendMessage(MessageUtils.color(new TextComponentTranslation("msg.lootgames.lose"), TextFormatting.DARK_PURPLE));

        destroyStructure();

        List<String> failEffects = new ArrayList<>();

        if (LGConfigGOL.onFailExplode)
            failEffects.add("explode");
        if (LGConfigGOL.onFailLava)
            failEffects.add("lava");
        if (LGConfigGOL.onFailZombies)
            failEffects.add("zombies");

        if (failEffects.size() != 0)
            executeFailEvent(failEffects.get(LootGames.RAND.nextInt(failEffects.size())));
    }

    private void executeFailEvent(String pEffect) {
        if (pEffect.equalsIgnoreCase("explode")) {
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 6, true);
        } else if (pEffect.equalsIgnoreCase("lava")) {
            for (int x = -5; x <= 5; x++)
                for (int z = -5; z <= 5; z++)
                    for (int y = 1; y < 3; y++)
                        world.setBlockState(pos.add(x, y, z), Blocks.LAVA.getDefaultState());
        } else if (pEffect.equalsIgnoreCase("zombies")) {
            for (int i = 0; i < 10; i++) {
                EntityZombie zombie = new EntityZombie(world);
                zombie.setLocationAndAngles(pos.getX() + LootGames.RAND.nextFloat() * 2, pos.getY() + 1, pos.getZ() + LootGames.RAND.nextFloat() * 2, MathHelper.wrapDegrees(LootGames.RAND.nextFloat() * 360.0F), 0.0F);
                zombie.rotationYawHead = zombie.rotationYaw;
                zombie.renderYawOffset = zombie.rotationYaw;
                world.spawnEntity(zombie);
                zombie.playLivingSound();
            }
        }
    }

    private void destroyStructure() {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {

                if (x == 0 || z == 0) {
                    world.setBlockToAir(pos.add(x, 0, z));
                } else {
                    world.setBlockState(pos.add(x, 0, z), ModBlocks.DUNGEON_LAMP.getDefaultState().withProperty(BlockDungeonLamp.BROKEN, false));
                }
            }
        }

        DungeonGenerator.resetUnbreakablePlayfield(world, pos);
    }

    @Override
    public void destroyGameBlocks() {
        //TODO do while refactoring
    }

    private boolean isLastStagePassed() {
        return gameLevel == 4 && currentRound >= LGConfigGOL.stage4.getMinRoundsRequiredToPass(world.provider.getDimension());
    }

    private void genLootChests(EntityPlayer player) {
        int bestLevelReached = getBestLevelReached();

        if (bestLevelReached == -1 || isLastStagePassed()) {
            bestLevelReached = 4;
        }

        if (bestLevelReached < 1) {
            LootGames.logHelper.error("GenLootChests method was called in an appropriate time!");
            return;
        }

        spawnLootChest(DirectionTetra.NORTH, 1);

        if (bestLevelReached > 1) {

            spawnLootChest(DirectionTetra.EAST, 2);
        }

        if (bestLevelReached > 2) {
            if (player instanceof EntityPlayerMP) {
                AdvancementManager.WIN_GAME.trigger(((EntityPlayerMP) player), "gol_level3");
            }
            spawnLootChest(DirectionTetra.SOUTH, 3);
        }

        if (bestLevelReached > 3) {
            if (player instanceof EntityPlayerMP) {
                AdvancementManager.WIN_GAME.trigger(((EntityPlayerMP) player), "gol_level4");
            }
            spawnLootChest(DirectionTetra.WEST, 4);
        }
    }

    private void spawnLootChest(DirectionTetra direction, int gameLevel) {
        LGConfigGOL.Stage stage = LGConfigGOL.getStageByIndex(gameLevel);
        GameUtils.SpawnChestInfo chestInfo = new GameUtils.SpawnChestInfo(GameOfLight.class, stage.getLootTableRL(world.provider.getDimension()), stage.minItems, stage.maxItems);
        GameUtils.spawnLootChest(getWorld(), getPos(), direction, chestInfo);
    }

    private void updateGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
        onStageSetting(gameStage);
        setBlockToUpdateAndSave();
    }

    /**
     * Returns best level from played. Returns -1, if game was won without mistakes.
     */
    private int getBestLevelReached() {
        int i = -1;
        for (Integer attempt : maxLevelBeatList) {
            if (attempt > i) {
                i = attempt;
            }
        }

        return i;
    }

    private void spawnFeedbackParticles(EnumParticleTypes particle, BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(particle, pos.getX() + LootGames.RAND.nextFloat(), pos.getY() + 0.5F + LootGames.RAND.nextFloat(), pos.getZ() + LootGames.RAND.nextFloat(), LootGames.RAND.nextGaussian() * 0.02D, (0.02D + LootGames.RAND.nextGaussian()) * 0.02D, LootGames.RAND.nextGaussian() * 0.02D);
        }
    }

    @NotNull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-2, 0, -2), getPos().add(2, 1, 2));
    }

    GameStage getGameStage() {
        return gameStage;
    }

    boolean isShowingSymbols() {
        return symbolIndex < symbolSequence.size();
    }

    DirectionOctagonal getCurrentSymbolPosOffset() {
        return DirectionOctagonal.byIndex(symbolSequence.get(symbolIndex));
    }

    int getTicks() {
        return ticks;
    }

    List<ClickInfo> getSymbolsEnteredByPlayer() {
        return symbolsEnteredByPlayer;
    }

    @SideOnly(Side.CLIENT)
    List<DrawInfo> getStuffToDraw() {
        return stuffToDraw;
    }

    @SideOnly(Side.CLIENT)
    public void addStuffToDraw(DrawInfo stuff) {
        if (stuffToDraw == null) {
            stuffToDraw = new ArrayList<>();
        }
        stuffToDraw.add(stuff);
    }

    boolean isFeedbackPacketReceived() {
        return feedbackPacketReceived;
    }

    boolean isOnPause() {
        return pauseBeforeShowing;
    }

    private void onStageSetting(GameStage stage) {
        ticks = 0;

        if (stage == GameStage.SHOWING_SEQUENCE) {
            feedbackPacketReceived = false;
            symbolIndex = 0;
            particleSent = false;
            pauseBeforeShowing = true;
        }

        if (stage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            timeout = 0;
            symbolIndex = 0;

            if (maxLevelBeatList == null) {
                maxLevelBeatList = new ArrayList<>();
            }

            if (hasWorld() && world.isRemote) {
                symbolsEnteredByPlayer = new LinkedList<>();
            }
        }
    }

    public enum GameStage {
        NOT_CONSTRUCTED,
        UNDER_EXPANDING,
        WAITING_FOR_START,
        SHOWING_SEQUENCE,
        WAITING_FOR_PLAYER_SEQUENCE
    }

    public enum EnumDrawStuff {
        SEQUENCE_ACCEPTED,
        SEQUENCE_DENIED,
        SHOWING_SEQUENCE
    }

    public static class DrawInfo {
        private long msClickedTime;
        private EnumDrawStuff stuff;

        public DrawInfo(long msClickedTime, EnumDrawStuff stuff) {
            this.msClickedTime = msClickedTime;
            this.stuff = stuff;
        }

        EnumDrawStuff getStuff() {
            return stuff;
        }
    }

    public static class ClickInfo {
        private long msClickedTime;
        private DirectionOctagonal offset;

        private ClickInfo(long msClickedTime, DirectionOctagonal offset) {
            this.msClickedTime = msClickedTime;
            this.offset = offset;
        }

        DirectionOctagonal getOffset() {
            return offset;
        }
    }
}
