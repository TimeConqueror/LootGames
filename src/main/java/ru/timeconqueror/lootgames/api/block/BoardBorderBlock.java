package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.utils.future.BlockPos;
import ru.timeconqueror.lootgames.utils.future.BlockState;
import ru.timeconqueror.lootgames.utils.future.WorldExt;

import javax.annotation.ParametersAreNonnullByDefault;

//TODO textures
@ParametersAreNonnullByDefault
public class BoardBorderBlock extends GameBlock implements IGameField {
    public BoardBorderBlock() {
        super();
    }

    public static BlockPos getMasterPos(World world, BlockPos pos, BlockState oldState) {
        BlockPos.Mutable currentPos = pos.mutable();

        Block currentBlock = oldState.getBlock();
        int currentMeta = oldState.getMeta();
        int limit = 128;

        while (--limit >= 0) {
            if (!(currentBlock instanceof BoardBorderBlock)) {
                break;
            }


            Type type = Type.byMeta(currentMeta);

            switch (type) {
                case HORIZONTAL:
                case TOP_RIGHT:
                    currentPos.move(-1, 0, 0);
                    break;
                case BOTTOM_RIGHT:
                case BOTTOM_LEFT:
                case VERTICAL:
                    currentPos.move(0, 0, -1);
                    break;
            }

            currentBlock = WorldExt.getBlock(world, currentPos);
            currentMeta = WorldExt.getMeta(world, currentPos);
        }

        return currentPos.immutable();
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (!worldIn.isRemote) {
            BlockPos pos = BlockPos.of(x, y, z);
            LootGamesAPI.getFieldManager().onFieldBlockBroken(worldIn, () -> getMasterPos(worldIn, pos, BlockState.of(blockBroken, meta)));
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public enum Type {
        HORIZONTAL,
        VERTICAL,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT;

        private static final Type[] VALUES = values();

        public static Type byMeta(int meta) {
            if (meta >= VALUES.length)
                throw new IllegalArgumentException(String.format("Provided meta (%s) which is more than type storage size (%s)", meta, VALUES.length));
            return VALUES[meta];
        }
    }
}
