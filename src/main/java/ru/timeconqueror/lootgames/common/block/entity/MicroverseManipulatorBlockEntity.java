package ru.timeconqueror.lootgames.common.block.entity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.common.block.MicroverseManipulatorBlock;
import ru.timeconqueror.lootgames.registry.LGAnimations;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.lootgames.utils.Utils;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.action.BakedActionFactory;
import ru.timeconqueror.timecore.animation.action.RunOnSide;
import ru.timeconqueror.timecore.api.animation.*;
import ru.timeconqueror.timecore.api.animation.action.BakedAction;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.api.common.blockentity.SyncableBlockEntity;

import java.util.Objects;

import static ru.timeconqueror.timecore.api.animation.action.ActionDefinitions.*;

public class MicroverseManipulatorBlockEntity extends SyncableBlockEntity implements AnimatedObject<MicroverseManipulatorBlockEntity> {
    private static final String ACTION_ACTIVATE = "activate";
    private static final String ACTION_DEACTIVATE = "deactivate";
    private static final String LAYER_HOLE = "hole";

    private AnimationSystem<MicroverseManipulatorBlockEntity> animationSystem;
    @Getter
    @Nullable
    private GameInfo gameInfo;
    @Nullable
    private RoomCoords roomCoords;
    @Getter
    private int ticks;
    @Getter
    private boolean canSwitchState;

    @Getter
    private boolean baseEnlighten;

    public MicroverseManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(LGBlockEntities.MICROVERSE_MANIPULATOR, pos, state);
    }

    private static BakedActionFactory<MicroverseManipulatorBlockEntity> actionActivate(int animationTime) {
        return BakedActionFactory.builder(MicroverseManipulatorBlockEntity.class)
                .runOnSide(RunOnSide.BOTH)
                .actionSingleton(BakedAction.sequencedBuilder(MicroverseManipulatorBlockEntity.class)
                        .id("activate")
                        .run(onBoundaryStart((ctx, props) -> ctx.getOwner().setCanSwitchState(false)))
                        .run(everyCycleAt(animationTime, (ctx, props) -> ctx.getOwner().baseEnlighten = true))
                        .run(onBoundaryEnd((ctx, props) -> {
                            ctx.getOwner().animationSystem()
                                    .startAnimationScript(AnimationScript.builder(LGAnimations.microverseManipulatorHoleAppear.starter().withTransitionTime(0))
                                            .withNext(AnimationScript.builder(LGAnimations.microverseManipulatorHoleIdle.starter())), LAYER_HOLE);
                        }))
                        .run(onBoundaryEnd((ctx, props) -> ctx.getOwner().setCanSwitchState(true)))
                        .build()
                )
                .build();
    }

    private static BakedActionFactory<MicroverseManipulatorBlockEntity> actionDeactivate(int animationTime) {
        return BakedActionFactory.builder(MicroverseManipulatorBlockEntity.class)
                .runOnSide(RunOnSide.BOTH)
                .actionSingleton(BakedAction.sequencedBuilder(MicroverseManipulatorBlockEntity.class)
                        .id("activate")
                        .run(onBoundaryEnd((ctx, props) -> ctx.getOwner().setCanSwitchState(false)))
                        .run(onBoundaryEnd((ctx, props) -> {
                            ctx.animationSystem().startAnimationScript(LGAnimations.microverseManipulatorHoleAppear
                                            .starter()
                                            .reversed()
                                            .toScriptBuilder()
                                            .withNext(LGAnimations.microverseManipulatorNoHole.starter().withNoTransitionTime())
                                    , LAYER_HOLE);
                        }))
                        .run(everyCycleAt(animationTime, (ctx, props) -> ctx.getOwner().baseEnlighten = false))
                        .run(onBoundaryStart((ctx, props) -> ctx.getOwner().clearGameAndThrowStack()))
                        .run(onBoundaryStart((ctx, props) -> ctx.getOwner().setCanSwitchState(true)))
                        .build()
                )
                .build();
    }

    private void clearGameAndThrowStack() {
        GameInfo game = Objects.requireNonNull(gameInfo);

        Item holder = LootGames.getGameInfoRegistry().getHolderByGameInfo(game);
        ItemStack stack = new ItemStack(holder, 1);

        Direction direction = getBlockState().getValue(MicroverseManipulatorBlock.FACING);
        Vector3f dirVec = Utils.vec3iToVec3f(direction.getNormal());
        Vector3d pos = Utils.blockPosToVec3d(getBlockPos())
                .add(0.5, 0.5, 0.5)
                .add(dirVec.mul(0.65F));

        Level level = Objects.requireNonNull(getLevel());
        ItemEntity e = Utils.makeItemThrowing(level, pos, stack, dirVec, -20, 0.1F, 20);
        level.addFreshEntity(e);

        gameInfo = null;
        saveAndSync();
    }

    @Override
    public void onLoad() {
        animationSystem = AnimationSystems.forBlockEntity(this, builder -> {
            builder.addMainLayer();
            builder.addLayer(LAYER_HOLE, BlendType.ADD, 1.0F);
        });

        if (getLevel() == null) {
            // skip for ItemStack rendering
            return;
        }

        animationSystem.registerPredefinedAction(ACTION_ACTIVATE, actionActivate(2_100));
        animationSystem.registerPredefinedAction(ACTION_DEACTIVATE, actionDeactivate(610));

        if (!isClientSide()) {
            if (gameInfo != null) {
                animationSystem.startAnimation(LGAnimations.microverseManipulatorIdle.starter().withNoTransitionTime(), AnimationConstants.MAIN_LAYER_NAME);
                animationSystem.startAnimation(LGAnimations.microverseManipulatorHoleIdle.starter().withNoTransitionTime(), LAYER_HOLE);
                this.baseEnlighten = true;
            } else {
                animationSystem.startAnimation(LGAnimations.microverseManipulatorNoHole.starter().withNoTransitionTime(), LAYER_HOLE);
            }
        }

        setCanSwitchState(true);
    }

    @Override
    public @NotNull AnimationSystem<MicroverseManipulatorBlockEntity> animationSystem() {
        return animationSystem;
    }

    public boolean isEmpty() {
        return gameInfo == null;
    }

    public boolean insertGame(GameInfo gameInfo, @Nullable RoomCoords boundRoomCoords) {
        if (!isEmpty() || !canSwitchState) {
            return false;
        }

        this.gameInfo = gameInfo;
        this.roomCoords = boundRoomCoords;
        saveAndSync();
        animationSystem.startAnimationScript(AnimationScript.builder(LGAnimations.microverseManipulatorPlace.starter().withTransitionTime(0).withSpeed(2))
                        .withPredefinedAction(ACTION_ACTIVATE)
                        .withNext(AnimationScript.builder(LGAnimations.microverseManipulatorIdle.starter().withTransitionTime(0))),
                AnimationConstants.MAIN_LAYER_NAME);
        return true;
    }

    public void clearGame() {
        if (isEmpty() || !canSwitchState) {
            return;
        }

        animationSystem.startAnimationScript(AnimationScript.builder(LGAnimations.microverseManipulatorPlace.starter().reversed().withNoTransitionToNone().withSpeed(2))
                        .withPredefinedAction(ACTION_DEACTIVATE),
                AnimationConstants.MAIN_LAYER_NAME);
    }

    public void tick() {
        animationSystem.onTick(isClientSide());
        ticks++;
    }

    @Override
    protected void readNBT(CompoundTag nbt, SerializationType type) {
        this.gameInfo = null;

        if (nbt.contains("game")) {
            this.gameInfo = GameInfo.deserialize(nbt.getString("game"));
        }

        this.baseEnlighten = nbt.getBoolean("enlighten");

        super.readNBT(nbt, type);
    }

    @Override
    protected void writeNBT(CompoundTag nbt, SerializationType type) {
        if (gameInfo != null) {
            nbt.putString("game", GameInfo.serialize(gameInfo));
        }

        nbt.putBoolean("enlighten", baseEnlighten);

        super.writeNBT(nbt, type);
    }

    public void setCanSwitchState(boolean canSwitchState) {
        if (level == null) return;
        System.out.println(level.isClientSide ? "Client CanSwitchState: " + canSwitchState : "Server CanSwitchState: " + canSwitchState);
        this.canSwitchState = canSwitchState;
    }
}
