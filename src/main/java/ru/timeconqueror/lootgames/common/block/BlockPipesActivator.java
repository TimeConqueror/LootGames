package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;
import ru.timeconqueror.lootgames.registry.LGSounds;

public class BlockPipesActivator extends BlockGame {
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
//            LGAdvancementTriggers.ACTIVATE_BLOCK.trigger(((ServerPlayerEntity) player), new ActivateBlockTrigger.ExtraInfo(pos, player.getHeldItem(handIn)));

            GamePipes.generateGameBoard(worldIn, pos, 1);
            worldIn.playSound(null, pos, LGSounds.MS_START_GAME, SoundCategory.BLOCKS, 0.6F, 1.0F);
        }

        return ActionResultType.SUCCESS;
    }
}
