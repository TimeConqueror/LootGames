package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.room.RoomAccess;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.common.block.entity.TransferenceCrystalBlockEntity;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.ServerRoom;
import ru.timeconqueror.timecore.api.util.LevelUtils;

public class TransferenceCrystalBlock extends BaseEntityBlock {
    public TransferenceCrystalBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.BARRIER));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos_, BlockState state_) {
        return new TransferenceCrystalBlockEntity(pos_, state_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world_, BlockState state_, BlockEntityType<T> type_) {
        return LevelUtils.makeSimpleTicker(type_, LGBlockEntities.TRANSFERENCE_CRYSTAL, (world_1, pos_, state_1, blockEntity_) -> blockEntity_.tick());
    }

    @Override
    public VoxelShape getShape(BlockState state_, BlockGetter world_, BlockPos pos_, CollisionContext context_) {
        return Block.box(2, 2, 2, 14, 14, 14);
    }

    @Override
    public InteractionResult use(BlockState state_, Level world_, BlockPos pos_, Player player, InteractionHand hand_, BlockHitResult hit_) {
        if (player instanceof FakePlayer || !RoomUtils.inRoomWorld(world_)) {
            return InteractionResult.FAIL;
        }

        if (!world_.isClientSide()) {
            ServerRoom serverRoom = RoomAccess.getServerStorage()
                    .getRoom(RoomCoords.of(pos_), true);
            serverRoom.teleportAway(((ServerPlayer) player));
        }

        return InteractionResult.sidedSuccess(world_.isClientSide());
    }
}
