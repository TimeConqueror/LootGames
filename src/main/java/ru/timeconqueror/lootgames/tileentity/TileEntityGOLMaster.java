package ru.timeconqueror.lootgames.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import ru.timeconqueror.lootgames.block.BlockGOLSubordinate;
import ru.timeconqueror.lootgames.registry.ModBlocks;

public class TileEntityGOLMaster extends TileEntityEnhanced implements ITickable {
    public static final int MAX_TICKS_EXPANDING = 20;

    private int ticks = 0;
    private GameStage gameStage;

    public TileEntityGOLMaster() {
        gameStage = GameStage.NOT_CONSTRUCTED;
    }

    @Override
    public void update() {
        if (gameStage == GameStage.UNDER_EXPANDING) {
            if (ticks > MAX_TICKS_EXPANDING) {
                updateGameStage(GameStage.SLEEP);
            } else {
                ticks++;
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
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        ticks = compound.getInteger("ticks");
        gameStage = GameStage.values()[compound.getInteger("game_stage")];
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("ticks", ticks);
        compound.setInteger("game_stage", gameStage.ordinal());
        return super.writeToNBT(compound);
    }

    @Override
    public void onDataPacket(NetworkManager connection, SPacketUpdateTileEntity packet) {
        super.onDataPacket(connection, packet);
    }

    public void updateGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
        ticks = 0;
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

    public int getTicks() {
        return ticks;
    }

    public enum GameStage {
        NOT_CONSTRUCTED,
        UNDER_EXPANDING,
        SLEEP,
        ACTIVE_PLAY_SEQUENCE,
        ACTIVE_WAIT_FOR_PLAYER,
        PENDING_GAME_START,
        PLAY_MUSIC
    }
}
