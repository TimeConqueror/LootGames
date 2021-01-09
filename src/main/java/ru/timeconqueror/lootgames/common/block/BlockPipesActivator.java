package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BlockPipesActivator extends BlockGame {
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            boolean fieldPlaced = LootGamesAPI.getFieldManager().trySetupBoard(
                    (ServerWorld) worldIn,
                    pos, 15, 2, 15,
                    LGBlocks.PIPES_MASTER.defaultBlockState(),
                    player
            ).isSucceed();

            if (fieldPlaced) {
                worldIn.playSound(null, pos, LGSounds.MS_START_GAME, SoundCategory.BLOCKS, 0.6F, 1.0F);
                return ActionResultType.SUCCESS;
            }
        } else {
            return ActionResultType.CONSUME;
        }

        return ActionResultType.FAIL;
    }
}
