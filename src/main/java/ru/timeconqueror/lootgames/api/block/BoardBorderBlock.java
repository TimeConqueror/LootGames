package ru.timeconqueror.lootgames.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    public static BlockPos getMasterPos(Level world, BlockPos pos, BlockState oldState) {
        BlockPos.MutableBlockPos currentPos = pos.mutable();
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
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            LootGamesAPI.getBoardManager().onFieldBlockBroken(worldIn, () -> getMasterPos(worldIn, pos, state));
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public enum Type implements StringRepresentable {
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
