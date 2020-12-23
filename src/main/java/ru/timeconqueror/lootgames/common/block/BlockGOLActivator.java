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
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;

public class BlockGOLActivator extends BlockGame {
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            LGAdvancementTriggers.USE_BLOCK.trigger(((ServerPlayerEntity) player), new UseBlockTrigger.ExtraInfo(state, pos, player.getItemInHand(handIn)));

            generateGameBoard(worldIn, pos);
            worldIn.playSound(null, pos, LGSounds.MS_START_GAME, SoundCategory.BLOCKS, 0.6F, 1.0F);
        }

        return ActionResultType.SUCCESS;
    }

    public static void generateGameBoard(World world, BlockPos centerPos) {
        int size = GameOfLight.BOARD_SIZE;
        BoardLootGame.generateGameBoard(world, centerPos.offset(-size / 2, 0, -size / 2), size, LGBlocks.GOL_MASTER.defaultBlockState());
    }
}
