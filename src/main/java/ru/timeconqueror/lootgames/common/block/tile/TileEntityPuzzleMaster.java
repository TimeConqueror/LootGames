package ru.timeconqueror.lootgames.common.block.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import ru.timeconqueror.lootgames.registry.LGTiles;
import ru.timeconqueror.timecore.api.util.RandHelper;

public class TileEntityPuzzleMaster extends TileEntity implements ITickableTileEntity {
    private long lastSoundTick = 0L;

    public TileEntityPuzzleMaster() {
        super(LGTiles.PUZZLE_MASTER);
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            if (RandHelper.RAND.nextInt(100) <= 10) {
                if (lastSoundTick < System.currentTimeMillis()) {
                    lastSoundTick = System.currentTimeMillis() + (RandHelper.RAND.nextInt(90) + 30) * 1000;
//                    world.playSound(null, pos, ModSounds.puzzleMasterStrange, SoundCategory.MASTER, 0.5F, 1.0F);//fixme uncomment
                }
            }
        }
    }
}
