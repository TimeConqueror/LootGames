package ru.timeconqueror.lootgames.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.block.BlockGOLSubordinate;
import ru.timeconqueror.lootgames.config.LootGamesConfig;
import ru.timeconqueror.lootgames.registry.ModBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TileEntityGOLMaster extends TileEntityEnhanced implements ITickable {
    public static final int MAX_TICKS_EXPANDING = 20;
    public static final int TICKS_PER_SHOW_SYMBOLS = 24;//TODO add custom for all stages
    public static final int TICKS_PAUSE_BETWEEN_SYMBOLS = 15;

    private int ticks = 0;
    private int clientSymbolIndex = 0;

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
            if (world.isRemote) {
                if (ticks > TICKS_PER_SHOW_SYMBOLS + TICKS_PAUSE_BETWEEN_SYMBOLS) {
                    ticks = 0;
                    clientSymbolIndex++;
                }

                ticks++;
            } else {
                if (feedbackPacketReceived) {
                    feedbackPacketReceived = false;
                    updateGameStage(GameStage.WAITING_FOR_PLAYER_SEQUENCE);
                }
            }
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
    }

    private void startGame(EntityPlayer player) {
        player.sendMessage(new TextComponentString(TextFormatting.AQUA.toString()).appendSibling(new TextComponentTranslation("msg." + LootGames.MODID + ".gol_master.start")));
        player.sendMessage(new TextComponentString(TextFormatting.AQUA.toString()).appendSibling(new TextComponentTranslation("msg." + LootGames.MODID + ".gol_master.rules")));

        symbolSequence = new ArrayList<>();
        for (int i = 0; i < LootGamesConfig.gameOfLight.startDigitAmount; i++) {
            symbolSequence.add(LootGames.rand.nextInt(8));
        }

        updateGameStage(GameStage.SHOWING_SEQUENCE);
    }

    public void onClientThingsDone() {
        feedbackPacketReceived = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        ticks = compound.getInteger("ticks");
        gameStage = GameStage.values()[compound.getInteger("game_stage")];
        onStageSetting(gameStage);

        symbolSequence = Arrays.stream(compound.getIntArray("symbol_sequence")).boxed().collect(Collectors.toList());
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("ticks", ticks);
        compound.setInteger("game_stage", gameStage.ordinal());

        if (symbolSequence != null) {
            compound.setIntArray("symbol_sequence", symbolSequence.stream().mapToInt(i -> i).toArray());
        }
        return super.writeToNBT(compound);
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

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-2, 0, -2), getPos().add(2, 1, 2));
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public boolean hasShowedAllSymbols() {
        return clientSymbolIndex >= symbolSequence.size();
    }

    public BlockGOLSubordinate.EnumPosOffset getCurrentSymbolPosOffset() {
        return BlockGOLSubordinate.EnumPosOffset.byIndex(symbolSequence.get(clientSymbolIndex));
    }

    public int getTicks() {
        return ticks;
    }

    public boolean isFeedbackPacketReceived() {
        return feedbackPacketReceived;
    }

    private void onStageSetting(GameStage stage) {
        ticks = 0;
        if (stage == GameStage.SHOWING_SEQUENCE) {
            feedbackPacketReceived = false;
            clientSymbolIndex = 0;
        }
    }

    public enum GameStage {
        NOT_CONSTRUCTED,
        UNDER_EXPANDING,
        WAITING_FOR_START,
        SHOWING_SEQUENCE,
        WAITING_FOR_PLAYER_SEQUENCE
    }
}
