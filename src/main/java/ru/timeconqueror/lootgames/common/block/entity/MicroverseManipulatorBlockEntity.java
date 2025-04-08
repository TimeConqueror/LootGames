package ru.timeconqueror.lootgames.common.block.entity;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.common.block.MicroverseManipulatorBlock;
import ru.timeconqueror.lootgames.registry.LGAnimations;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.lootgames.registry.custom.GameDefinition;
import ru.timeconqueror.lootgames.room.EntryPoint;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.lootgames.room.ServerRoomStorage;
import ru.timeconqueror.lootgames.utils.Utils;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.action.BakedActionFactory;
import ru.timeconqueror.timecore.animation.action.RunOnSide;
import ru.timeconqueror.timecore.api.animation.*;
import ru.timeconqueror.timecore.api.animation.action.BakedAction;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.api.common.blockentity.SyncableBlockEntity;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Vec2i;

import java.util.Objects;

import static ru.timeconqueror.timecore.api.animation.action.ActionDefinitions.*;

@Log4j2
public class MicroverseManipulatorBlockEntity extends SyncableBlockEntity implements AnimatedObject<MicroverseManipulatorBlockEntity> {
    private static final String ACTION_ACTIVATE = "activate";
    private static final String ACTION_DEACTIVATE = "deactivate";
    private static final String LAYER_HOLE = "hole";

    private AnimationSystem<MicroverseManipulatorBlockEntity> animationSystem;
    @Getter
    @Nullable
    private GameDefinition gameDefinition;
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
                .action(BakedAction.sequencedBuilder(MicroverseManipulatorBlockEntity.class)
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
                .action(BakedAction.sequencedBuilder(MicroverseManipulatorBlockEntity.class)
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
        GameDefinition game = Objects.requireNonNull(gameDefinition);

        Item holder = LootGames.getGameDefinitionRegistry().getHolderByDefinition(game);
        ItemStack stack = new ItemStack(holder, 1);

        Direction direction = getBlockState().getValue(MicroverseManipulatorBlock.FACING);
        Vector3f dirVec = Utils.vec3iToVec3f(direction.getNormal());
        Vector3d pos = Utils.blockPosToVec3d(getBlockPos())
                .add(0.5, 0.5, 0.5)
                .add(dirVec.mul(0.65F));

        Level level = Objects.requireNonNull(getLevel());
        ItemEntity e = Utils.makeItemThrowing(level, pos, stack, dirVec, -20, 0.1F, 20);
        level.addFreshEntity(e);

        gameDefinition = null;
        saveAndSync();
    }

    @Override
    public void setLevel(Level world_) {
        super.setLevel(world_);
        loadAnimationSystem();
    }

    public void loadAnimationSystem() {
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
            if (gameDefinition != null) {
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
        return gameDefinition == null;
    }

    public boolean insertGame(GameDefinition gameDefinition, @Nullable RoomCoords boundRoomCoords) {
        if (!isEmpty() || !canSwitchState) {
            return false;
        }

        this.gameDefinition = gameDefinition;
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

        this.canSwitchState = false;
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
        this.gameDefinition = null;

        if (nbt.contains("game")) {
            this.gameDefinition = GameDefinition.deserialize(nbt.getString("game"));
        }

        this.baseEnlighten = nbt.getBoolean("enlighten");

        super.readNBT(nbt, type);
    }

    @Override
    protected void writeNBT(CompoundTag nbt, SerializationType type) {
        if (gameDefinition != null) {
            nbt.putString("game", GameDefinition.serialize(gameDefinition));
        }

        nbt.putBoolean("enlighten", baseEnlighten);

        super.writeNBT(nbt, type);
    }

    public void setCanSwitchState(boolean canSwitchState) {
        if (level == null) return;
        System.out.println(level.isClientSide ? "Client CanSwitchState: " + canSwitchState : "Server CanSwitchState: " + canSwitchState);
        this.canSwitchState = canSwitchState;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public void initiateTeleport(ServerPlayer player) {
        BakedAction<MicroverseManipulatorBlockEntity> delayedTeleport = BakedAction.sequencedBuilder(MicroverseManipulatorBlockEntity.class)
                .run(onBoundaryStart((ctx, nothing) -> ctx.getOwner().setCanSwitchState(false)))
                .run(onBoundaryStart((ctx, nothing) -> prepareRoom(gameDefinition)))
                .run(everyCycleAt(620, (ctx, nothing) -> {
                    MicroverseManipulatorBlockEntity owner = ctx.getOwner();
                    BlockPos pos = owner.getBlockPos();

                    if (!player.hasDisconnected()
                            && player.level() == owner.getLevel()
                            && !(player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > 10 * 10)
                    ) {
                        ServerRoom room = RoomAccess.getServerStorage().getRoom(roomCoords, true);
                        room.teleportInside(player);
                    }
                }))
                .run(onBoundaryEnd((ctx, nothing) -> ctx.getOwner().setCanSwitchState(true)))
                .build();

        animationSystem.startAnimationScript(AnimationScript.builder(LGAnimations.microverseManipulatorHoleCollapse.starter().withSpeed(1/3F))
                        .withInplaceAction(delayedTeleport)
                , LAYER_HOLE);
    }

    private void prepareRoom(GameDefinition gameDefinition) {
        ServerRoom room;
        if (roomCoords == null) {
            ServerRoomStorage serverStorage = RoomAccess.getServerStorage();

            int freeIndex = serverStorage.reserveFreeIndex();
            Vec2i vec = MathUtils.OutwardSquareSpiral.offsetByIndex(freeIndex);
            this.roomCoords = new RoomCoords(vec.x(), vec.y());
            log.debug("Reserved spiral index: {}, Resulted coords: {}", freeIndex, roomCoords);
            save();
            room = serverStorage.getRoom(roomCoords, true);
        } else {
            room = RoomAccess.getServerStorage().getRoom(roomCoords, true);
        }

        if (!room.isGenerated()) {
            log.debug("Generating game in room: {}", gameDefinition.toKey());
            room.generate(gameDefinition);
        }

        Level level = Objects.requireNonNull(getLevel());
        room.updateEntryPoint(new EntryPoint(level.dimension(), getBlockPos()));
    }
}
