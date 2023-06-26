package ru.timeconqueror.lootgames.api.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;
import ru.timeconqueror.timecore.api.common.tile.SyncableTile;
import ru.timeconqueror.timecore.api.util.ITickableBlockEntity;

public abstract class GameMasterTile<T extends LootGame<?>> extends SyncableTile implements ITickableBlockEntity {
    // replacement for IForgeBlockEntity#INFINITE_EXTENT_AABB because 1.19.3's frustrum doesn't see it when look right below (https://github.com/MinecraftForge/MinecraftForge/issues/9321#issuecomment-1407420277)
    public static final AABB GIANT_AABB = new AABB(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    protected T game;
    private long age;

    public GameMasterTile(BlockEntityType<? extends GameMasterTile<T>> tileEntityTypeIn, BlockPos pos, BlockState state, T game) {
        super(tileEntityTypeIn, pos, state);
        this.game = game;
    }

    public long getAge() {
        return age;
    }

    @Override
    public void tick(Level level) {
        age++;
        game.onTick();
    }

    @Override
    protected void writeNBT(CompoundTag nbt, SerializationType type) {
        super.writeNBT(nbt, type);

        CompoundTag gameTag = new CompoundTag();
        game.writeNBT(gameTag, type);
        nbt.put("game", gameTag);
    }

    @Override
    protected void readNBT(CompoundTag nbt, SerializationType type) {
        super.readNBT(nbt, type);

        game.readNBT(nbt.getCompound("game"), type);
    }

    /**
     * Will be called when subordinate block is right-clicked by player.
     *
     * @param player         player, who clicked the subordinate block.
     * @param subordinatePos pos of subordinate block.
     */
    public void onBlockRightClick(Player player, BlockPos subordinatePos) {
    }

    /**
     * Will be called when subordinate block is left-clicked by player.
     *
     * @param player         player, who clicked the subordinate block.
     * @param subordinatePos pos of subordinate block.
     */
    public void onBlockLeftClick(Player player, BlockPos subordinatePos) {

    }

    public T getGame() {
        return game;
    }
}
