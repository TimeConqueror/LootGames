package ru.timeconqueror.lootgames.api.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;
import ru.timeconqueror.timecore.api.common.tile.SyncableTile;

public abstract class GameMasterTile<T extends LootGame<?, T>> extends SyncableTile implements ITickableTileEntity {
    protected T game;
    private long age;

    public GameMasterTile(TileEntityType<? extends GameMasterTile<T>> tileEntityTypeIn, T game) {
        super(tileEntityTypeIn);
        this.game = game;
        game.setMasterTileEntity(this);
    }

    public long getAge() {
        return age;
    }

    @Override
    public void tick() {
        age++;
        game.onTick();
    }

    @Override
    public void onLoad() {
        game.onLoad();
    }

    /**
     * Called when TileEntityGameMaster or BlockSubordinate should be broken.
     * Block with TileEntity still exists at this point.
     * This method implies destroying of all game blocks.
     */
    public void onDestroy() {
        game.onDestroy();
    }

    @Override
    protected void writeNBT(CompoundNBT nbt, SerializationType type) {
        super.writeNBT(nbt, type);

        CompoundNBT gameTag = new CompoundNBT();
        game.writeNBT(gameTag, type);
        nbt.put("game", gameTag);
    }

    @Override
    protected void readNBT(BlockState state, CompoundNBT nbt, SerializationType type) {
        super.readNBT(state, nbt, type);

        game.readNBT(nbt.getCompound("game"), type);
    }

    /**
     * Will be called when subordinate block is right-clicked by player.
     *
     * @param player         player, who clicked the subordinate block.
     * @param subordinatePos pos of subordinate block.
     */
    public void onBlockRightClick(ServerPlayerEntity player, BlockPos subordinatePos) {
    }

    /**
     * Will be called when subordinate block is left-clicked by player.
     *
     * @param player         player, who clicked the subordinate block.
     * @param subordinatePos pos of subordinate block.
     */
    public void onBlockLeftClick(ServerPlayerEntity player, BlockPos subordinatePos) {

    }

    public T getGame() {
        return game;
    }
}
