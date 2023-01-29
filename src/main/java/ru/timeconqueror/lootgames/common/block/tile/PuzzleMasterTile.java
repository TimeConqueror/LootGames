package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.lootgames.registry.LGTiles;
import ru.timeconqueror.timecore.api.util.RandHelper;

public class PuzzleMasterTile extends BlockEntity implements TickableBlockEntity {
    private long lastSoundTick = 0L;

    public PuzzleMasterTile() {
        super(LGTiles.PUZZLE_MASTER);
    }

    @Override
    public void tick() {
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
