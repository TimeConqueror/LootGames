package ru.timeconqueror.lootgames.common.block.entity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGAnimations;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.lootgames.registry.LGItems;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.util.AnimationDebugEventListener;
import ru.timeconqueror.timecore.api.animation.*;
import ru.timeconqueror.timecore.api.animation.builders.AnimationManagerBuilder;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.api.common.blockentity.SyncableBlockEntity;

public class MicroverseManipulatorBlockEntity extends SyncableBlockEntity implements AnimatedObject<MicroverseManipulatorBlockEntity> {
    private AnimationSystem<MicroverseManipulatorBlockEntity> animationSystem;
    @Getter
    @Nullable
    private GameInfo gameInfo;

    public MicroverseManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(LGBlockEntities.MICROVERSE_MANIPULATOR, pos, state);
    }

    @Override
    public void onLoad() {
        animationSystem = AnimationSystems.forBlockEntity(this, AnimationManagerBuilder::addMainLayer);
    }

    @Override
    public @NotNull AnimationSystem<MicroverseManipulatorBlockEntity> getSystem() {
        return animationSystem;
    }

    public boolean isEmpty() {
        return gameInfo == null;
    }

    public void insertGame(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        saveAndSync();
        animationSystem.api().startAnimation(LGAnimations.microverseManipulatorPlace.starter().withTransitionTime(0)
                        .withNextAnimation(LGAnimations.microverseManipulatorIdle.starter().withTransitionTime(0)),
                AnimationConstants.MAIN_LAYER_NAME);
    }

    public void clearGame() {
        gameInfo = null;
        saveAndSync();
    }

    public void tick() {
        getSystem().onTick(isClientSide());
    }

    @Override
    protected void readNBT(CompoundTag nbt, SerializationType type) {
        this.gameInfo = null;

        if(nbt.contains("game")) {
            this.gameInfo = GameInfo.deserialize(nbt.getString("game"));
        }

        super.readNBT(nbt, type);
    }

    @Override
    protected void writeNBT(CompoundTag nbt, SerializationType type) {
        if(gameInfo != null) {
            nbt.putString("game", GameInfo.serialize(gameInfo));
        }

        super.writeNBT(nbt, type);
    }
}
