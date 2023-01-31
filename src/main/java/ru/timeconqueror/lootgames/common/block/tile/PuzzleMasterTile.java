package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.timecore.api.util.RandHelper;
import ru.timeconqueror.timecorex.api.util.ITickableBlockEntity;

public class PuzzleMasterTile extends BlockEntity implements ITickableBlockEntity {
    private long lastSoundTick = 0L;

    public PuzzleMasterTile(BlockPos pos, BlockState blockState) {
        super(LGBlockEntities.PUZZLE_MASTER, pos, blockState);
    }

    @Override
    public void tick(Level level) {
        if (!level.isClientSide()) {
            if (RandHelper.RAND.nextInt(100) <= 10) {
                if (lastSoundTick < System.currentTimeMillis()) {
                    lastSoundTick = System.currentTimeMillis() + (RandHelper.RAND.nextInt(90) + 30) * 1000;
                    level.playSound(null, worldPosition, LGSounds.PUZZLE_MASTER_STRANGE, SoundSource.MASTER, 0.5F, 1.0F);
                }
            }
        }
    }
}
