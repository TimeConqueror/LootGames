package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.common.block.entity.MicroverseManipulatorBlockEntity;
import ru.timeconqueror.lootgames.common.item.BoxedMicroverseItem;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.lootgames.registry.LGItems;
import ru.timeconqueror.lootgames.registry.custom.GameDefinition;
import ru.timeconqueror.timecore.api.util.LevelUtils;

//TODO Waila: add inner microverse name
public class MicroverseManipulatorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public MicroverseManipulatorBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .requiresCorrectToolForDrops()
                .strength(10.0F, 600F)
                .sound(SoundType.METAL));

        properties.air();

        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof MicroverseManipulatorBlockEntity manipulatorEntity)) {
            return InteractionResult.FAIL;
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            if (!manipulatorEntity.isEmpty()) {
                manipulatorEntity.initiateTeleport((ServerPlayer) player);
            }
        } else {
            if (manipulatorEntity.isEmpty()) {
                ItemStack held = player.getItemInHand(hand);
                GameDefinition gameDefinition;
                if (held.getItem() == LGItems.BOXED_UNKNOWN_MICROVERSE) {
                    gameDefinition = LootGames.getGameDefinitionRegistry().getRandom();
                } else {
                    gameDefinition = LootGames.getGameDefinitionRegistry().getDefinitionByHolder(held.getItem());
                }

                if (gameDefinition == null) { // not a microverse holder
                    return InteractionResult.FAIL;
                }

                @Nullable
                RoomCoords boundRoom = BoxedMicroverseItem.getBoundRoomCoords(held);

                if (!world.isClientSide()) {
                    if (manipulatorEntity.insertGame(gameDefinition, boundRoom)) {
                        if (!player.isCreative()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            } else if (!manipulatorEntity.isEmpty()) {
                manipulatorEntity.clearGame();
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    public static void generate() {
//TODO
//
//        if (sp.serverLevel().dimension() != LGDimensions.TEST_SITE_DIM) {
//            ServerLevel roomWorld = ServerLifecycleHooks.getCurrentServer().getLevel(LGDimensions.TEST_SITE_DIM);
//            RoomStorage roomStorage = ServerRoomStorage.getInstance(roomWorld).orElseThrow();
//
//            Vec2i vec = MathUtils.OutwardSquareSpiral.offsetByIndex(roomStorage.reserveFreeIndex());
//
//            ServerRoom room = (ServerRoom) roomStorage.getRoom(new RoomCoords(vec.x(), vec.y()));
//            room.startGame();
//            BlockPos center = room.getCoords().centerPos(60);
//
//            sp.teleportTo(roomWorld, center.getX(), center.getY() + 1, center.getZ(), player.getXRot(), player.getYRot());
//        } else {
//            sp.teleportTo(ServerLifecycleHooks.getCurrentServer().overworld(), player.getX(), player.getY() + 1, player.getZ(), player.getXRot(), player.getYRot());
//        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MicroverseManipulatorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world_, BlockState state_, BlockEntityType<T> type_) {
        return LevelUtils.makeSimpleTicker(type_, LGBlockEntities.MICROVERSE_MANIPULATOR, (world_1, pos_, state_1, blockEntity_) -> blockEntity_.tick());
    }

    @Override
    public VoxelShape getShape(BlockState state_, BlockGetter world_, BlockPos pos_, CollisionContext context_) {
        return Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder_) {
        builder_.add(FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved_) {
        // partially copied from ChestBlock#onRemove
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof MicroverseManipulatorBlockEntity entity) {
                entity.makeGameDefinitionStack().ifPresent(stack ->
                        Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, stack)));
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, moved_);
        }
    }
}
