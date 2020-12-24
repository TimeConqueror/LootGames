package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public class BlockFieldBorder extends BlockGame implements IGameField {

    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    public BlockFieldBorder() {
        super();
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, Type.HORIZONTAL));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
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
