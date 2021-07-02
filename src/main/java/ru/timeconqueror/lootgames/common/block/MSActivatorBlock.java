package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.GameBlock;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger.ExtraInfo;
import ru.timeconqueror.lootgames.common.block.tile.MSMasterTile;
import ru.timeconqueror.lootgames.common.config.ConfigMS;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;

public class MSActivatorBlock extends GameBlock {
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            ConfigMS.Snapshot snapshot = LGConfigs.MINESWEEPER.snapshot();
            int allocatedSize = snapshot.getStage4().getBoardSize();

            boolean succeed = LootGamesAPI.getFieldManager()
                    .trySetupBoard(((ServerWorld) worldIn), pos, allocatedSize, 2, allocatedSize, LGBlocks.MS_MASTER.defaultBlockState(), player)
                    .forTileIfSucceed(MSMasterTile.class, master -> master.init(snapshot))
                    .isSucceed();

            if (succeed) {
                worldIn.playSound(null, pos, LGSounds.MS_START_GAME, SoundCategory.BLOCKS, 0.6F, 1.0F);
                LGAdvancementTriggers.USE_BLOCK.trigger(((ServerPlayerEntity) player), new ExtraInfo(state, pos, player.getItemInHand(handIn)));
            }
        }

        return ActionResultType.SUCCESS;
    }
}
