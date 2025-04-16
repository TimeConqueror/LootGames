package ru.timeconqueror.lootgames.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.registry.LGAnimations;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.clock.SystemMillisClock;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.AnimationSystemBuilder;
import ru.timeconqueror.timecore.api.common.blockentity.SyncableBlockEntity;

public class TransferenceCrystalBlockEntity extends SyncableBlockEntity implements AnimatedObject<TransferenceCrystalBlockEntity> {
    private AnimationSystem<TransferenceCrystalBlockEntity> animationSystem;

    public TransferenceCrystalBlockEntity(BlockPos pos, BlockState state) {
        super(LGBlockEntities.TRANSFERENCE_CRYSTAL, pos, state);
    }

    @Override
    public void setLevel(Level world_) {
        super.setLevel(world_);
        loadAnimationSystemIfNotLoaded();
    }

    public void tick() {
        animationSystem.onTick(isClientSide());
    }

    public void loadAnimationSystemIfNotLoaded() {
        if (animationSystem != null) {
            return;
        }

        var builder = AnimationSystemBuilder.forBlockEntity(this)
                .withMainAnimationLayer();
        if(level == null) {
            builder.withClock(new SystemMillisClock());
        }

        animationSystem = builder.build();

        if (level == null || !isClientSide()) {
            animationSystem.startAnimation(LGAnimations.transferenceCrystalShake.starter().withNoTransitionTime(), AnimationConstants.MAIN_LAYER_NAME);
        }
    }

    @Override
    public AnimationSystem<TransferenceCrystalBlockEntity> animationSystem() {
        return animationSystem;
    }
}
