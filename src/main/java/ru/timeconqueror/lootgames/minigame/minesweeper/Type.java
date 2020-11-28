package ru.timeconqueror.lootgames.minigame.minesweeper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import ru.timeconqueror.lootgames.utils.CodecUtils;

import java.util.function.Consumer;

public enum Type {
    BOMB((byte) -1),
    EMPTY((byte) 0),
    ONE((byte) 1),
    TWO((byte) 2),
    THREE((byte) 3),
    FOUR((byte) 4),
    FIVE((byte) 5),
    SIX((byte) 6),
    SEVEN((byte) 7),
    EIGHT((byte) 8);

    private static final Type[] LOOKUP;
    public static final Codec<Type> CODEC = Codec.BYTE.xmap(Type::byId, type -> type.id);

    private final byte id;

    Type(byte id) {
        this.id = id;
    }

    public static <SERIALIZED> Type softDecode(final DynamicOps<SERIALIZED> ops, final SERIALIZED input) {
        return CodecUtils.decodeSoftly(CODEC, ops, input, EMPTY);
    }

    public static <T> void softEncode(final DynamicOps<T> ops, final Type input, Consumer<T> actionIfProvided) {
        CodecUtils.encodeSoftly(CODEC, ops, input, actionIfProvided);
    }

    public byte getId() {
        return id;
    }

    static Type byId(byte id) {
        if (id + 1 >= LOOKUP.length) {
            throw new IllegalArgumentException("Provided unknown id: " + id);
        }
        return LOOKUP[id + 1];
    }

    static {
        LOOKUP = new Type[values().length];
        for (Type value : values()) {
            LOOKUP[value.id + 1] = value;
        }
    }
}
