package ru.timeconqueror.lootgames.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.block.BlockGOLSubordinate;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.registry.ModBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TileEntityGOLMaster extends TileEntityEnhanced implements ITickable {
    public static final int MAX_TICKS_EXPANDING = 20;
    public static final int TICKS_PER_SHOW_SYMBOLS = 24;//TODO add custom for all stages
    public static final int TICKS_PAUSE_BETWEEN_SYMBOLS = 15;

    private int ticks = 0;
    private int symbolIndex = 0;
    private boolean particleSended = false;
    private boolean pauseBeforeShowing = true;
    private List<ClickInfo> symbolsEnteredByPlayer;

    private GameStage gameStage;
    private List<Integer> symbolSequence;

    private boolean feedbackPacketReceived;

    public TileEntityGOLMaster() {
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
                if (ticks > TICKS_PAUSE_BETWEEN_SYMBOLS) {
                    ticks = 0;
                    pauseBeforeShowing = false;
                } else {
                    ticks++;
                }
            } else {
                if (world.isRemote) {
                    if (ticks > TICKS_PER_SHOW_SYMBOLS + TICKS_PAUSE_BETWEEN_SYMBOLS) {
                        ticks = 0;
                        symbolIndex++;
                        particleSended = false;
                    } else {
                        ticks++;
                    }

                    if (!hasShowedAllSymbols() && !particleSended) {
                        spawnFeedbackParticles(EnumParticleTypes.SPELL, getPos().add(getCurrentSymbolPosOffset().getOffsetX(), 0, getCurrentSymbolPosOffset().getOffsetZ()));
                        particleSended = true;
                    }
                } else {
                    if (feedbackPacketReceived) {
                        feedbackPacketReceived = false;
                        updateGameStage(GameStage.WAITING_FOR_PLAYER_SEQUENCE);
                    }
                }
            }
        }

        if (world.isRemote && symbolsEnteredByPlayer != null) {
            symbolsEnteredByPlayer.removeIf((clickInfo) -> System.currentTimeMillis() - clickInfo.msClickedTime > 800);
        }
    }

    public void generateSubordinates() {
        for (BlockGOLSubordinate.EnumPosOffset value : BlockGOLSubordinate.EnumPosOffset.values()) {
            IBlockState state = ModBlocks.GOL_SUBORDINATE.getDefaultState()
                    .withProperty(BlockGOLSubordinate.ACTIVATED, false)
                    .withProperty(BlockGOLSubordinate.OFFSET, value);
            world.setBlockState(getPos().add(value.getOffsetX(), 0, value.getOffsetZ()), state);
        }

        updateGameStage(GameStage.UNDER_EXPANDING);
    }

    public void onBlockClickedByPlayer(EntityPlayer player) {
        if (gameStage == GameStage.NOT_CONSTRUCTED) {
            generateSubordinates();
            return;
        }

        if (gameStage == GameStage.WAITING_FOR_START) {
            startGame(player);
            return;
        }

        if (gameStage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            player.sendMessage(new TextComponentTranslation("msg.lootgames.gol_master.rules").setStyle(new Style().setColor(TextFormatting.GOLD)));
            return;
        }

        player.sendMessage(new TextComponentTranslation("msg.lootgames.gol_master.not_ready").setStyle(new Style().setColor(TextFormatting.AQUA)));
    }

    public void onSubordinateClickedByPlayer(BlockGOLSubordinate.EnumPosOffset subordinateOffset, EntityPlayer player) {
        if (gameStage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            if (world.isRemote) {
                symbolsEnteredByPlayer.add(new ClickInfo(System.currentTimeMillis(), subordinateOffset));
            } else {
                checkPlayerReply(subordinateOffset, player);
            }

            return;
        }

        if (!world.isRemote) {
            player.sendMessage(new TextComponentTranslation("msg.lootgames.gol_master.not_ready").setStyle(new Style().setColor(TextFormatting.AQUA)));
        }
    }

    public void checkPlayerReply(BlockGOLSubordinate.EnumPosOffset subordinateOffset, EntityPlayer player) {
        if (BlockGOLSubordinate.EnumPosOffset.byIndex(symbolSequence.get(symbolIndex)) == subordinateOffset) {
            if (symbolIndex == symbolSequence.size() - 1) {
                addRandSymbolToSequence();
                updateGameStage(GameStage.SHOWING_SEQUENCE);
            }

            symbolIndex++;
        } else {
            player.sendMessage(new TextComponentTranslation("msg.lootgames.gol_master.wrong_block").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
            //TODO
        }
    }

    private void generateSequence(int size) {
        symbolSequence = new ArrayList<>();
        for (int i = 0; i < size; i++) {//TODO add 2x2
            symbolSequence.add(LootGames.rand.nextInt(8));
        }
    }

    private void addRandSymbolToSequence() {
        if (symbolSequence == null) {
            symbolSequence = new ArrayList<>();
        }
        //ToDO add2x2
        symbolSequence.add(LootGames.rand.nextInt(8));
    }

    private void startGame(EntityPlayer player) {
        player.sendMessage(new TextComponentTranslation("msg.lootgames.gol_master.start").setStyle(new Style().setColor(TextFormatting.AQUA)));
        player.sendMessage(new TextComponentTranslation("msg.lootgames.gol_master.rules").setStyle(new Style().setColor(TextFormatting.GOLD)));
        //TODO add pause before start
        generateSequence(LootGamesConfig.gameOfLight.startDigitAmount);

        updateGameStage(GameStage.SHOWING_SEQUENCE);
    }

    public void onClientThingsDone() {
        feedbackPacketReceived = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ticks = compound.getInteger("ticks");
        gameStage = GameStage.values()[compound.getInteger("game_stage")];
        onStageSetting(gameStage);

        symbolSequence = Arrays.stream(compound.getIntArray("symbol_sequence")).boxed().collect(Collectors.toList());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("ticks", ticks);
        compound.setInteger("game_stage", gameStage.ordinal());

        if (symbolSequence != null) {
            compound.setIntArray("symbol_sequence", symbolSequence.stream().mapToInt(i -> i).toArray());
        }

        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager connection, SPacketUpdateTileEntity packet) {
        super.onDataPacket(connection, packet);
    }

    public void updateGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
        onStageSetting(gameStage);
        setBlockToUpdate();
    }

    private void setBlockToUpdate() {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
        world.scheduleBlockUpdate(pos, this.getBlockType(), 0, 0);
        markDirty();
    }

    private void spawnFeedbackParticles(EnumParticleTypes particle, BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(particle, pos.getX() + LootGames.rand.nextFloat(), pos.getY() + 0.5F + LootGames.rand.nextFloat(), pos.getZ() + LootGames.rand.nextFloat(), LootGames.rand.nextGaussian() * 0.02D, (0.02D + LootGames.rand.nextGaussian()) * 0.02D, LootGames.rand.nextGaussian() * 0.02D);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-2, 0, -2), getPos().add(2, 1, 2));
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public boolean hasShowedAllSymbols() {
        return symbolIndex >= symbolSequence.size();
    }

    public BlockGOLSubordinate.EnumPosOffset getCurrentSymbolPosOffset() {
        return BlockGOLSubordinate.EnumPosOffset.byIndex(symbolSequence.get(symbolIndex));
    }

    public int getTicks() {
        return ticks;
    }

    public List<ClickInfo> getSymbolsEnteredByPlayer() {
        return symbolsEnteredByPlayer;
    }

    public boolean isFeedbackPacketReceived() {
        return feedbackPacketReceived;
    }

    public boolean isOnPause() {
        return pauseBeforeShowing;
    }

    private void onStageSetting(GameStage stage) {
        ticks = 0;
        if (stage == GameStage.SHOWING_SEQUENCE) {
            feedbackPacketReceived = false;
            symbolIndex = 0;
            particleSended = false;
            pauseBeforeShowing = true;
        }

        if (stage == GameStage.WAITING_FOR_PLAYER_SEQUENCE) {
            symbolIndex = 0;
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

    public class ClickInfo {
        private long msClickedTime;
        private BlockGOLSubordinate.EnumPosOffset offset;

        public ClickInfo(long msClickedTime, BlockGOLSubordinate.EnumPosOffset offset) {
            this.msClickedTime = msClickedTime;
            this.offset = offset;
        }

        public BlockGOLSubordinate.EnumPosOffset getOffset() {
            return offset;
        }
    }
}
