package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.block.BlockGameMaster;
import ru.timeconqueror.lootgames.api.block.tile.TileEntityGameMaster;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityMSMaster;
import ru.timeconqueror.timecore.util.WorldUtils;

public class BlockMSMaster extends BlockGameMaster {

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            WorldUtils.forTypedTileWithWarn(worldIn, pos, TileEntityGameMaster.class, te -> te.onBlockRightClicked(((ServerPlayerEntity) player), pos));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.destroy(worldIn, pos, state);
    }

    @Override
    public @NotNull TileEntityGameMaster<?> createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityMSMaster();
    }
}
