package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.LootGamesAPI;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;

@ParametersAreNonnullByDefault
public class BoardBorderBlock extends GameBlock implements IGameField {

    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    public BoardBorderBlock() {
        super();
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, Type.HORIZONTAL));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    public static BlockPos getMasterPos(World world, BlockPos pos, BlockState oldState) {
        BlockPos.Mutable currentPos = pos.mutable();
        BlockState currentState = oldState;
        int limit = 128;

        while (--limit >= 0) {
            if (!(currentState.getBlock() instanceof BoardBorderBlock)) {
                break;
            }

            switch (currentState.getValue(TYPE)) {
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

            currentState = world.getBlockState(currentPos);
        }

        return currentPos.immutable();
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            LootGamesAPI.getFieldManager().onFieldBlockBroken(worldIn, () -> getMasterPos(worldIn, pos, state));
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public enum Type implements IStringSerializable {
        HORIZONTAL,
        VERTICAL,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
