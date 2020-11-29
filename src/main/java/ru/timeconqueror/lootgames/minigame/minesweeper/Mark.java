package ru.timeconqueror.lootgames.minigame.minesweeper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import ru.timeconqueror.timecore.util.CodecUtils;

import java.util.function.Consumer;

public enum Mark {
    NO_MARK((byte) 0),
    FLAG((byte) 1),
    QUESTION_MARK((byte) 2);

    public static Codec<Mark> CODEC = Codec.BYTE.xmap(Mark::byID, mark -> mark.id);
    private static final Mark[] LOOKUP;

    private final byte id;

    Mark(byte id) {
        this.id = id;
    }

    public static <T> Mark softDecode(final DynamicOps<T> ops, final T input) {
        return CodecUtils.decodeSoftly(CODEC, ops, input, NO_MARK);
    }

    public static <T> void softEncode(final DynamicOps<T> ops, final Mark input, Consumer<T> actionIfProvided) {
        CodecUtils.encodeSoftly(CODEC, ops, input, actionIfProvided);
    }

    public static Mark getNext(Mark mark) {
        return mark.id >= LOOKUP.length - 1 ? LOOKUP[0] : LOOKUP[mark.id + 1];
    }

    public Mark getNext() {
        return getNext(this);
    }

    private static Mark byID(int id) {
        if (id >= LOOKUP.length) {
            throw new IllegalArgumentException("Provided unknown id: " + id);
        }
        return LOOKUP[id];
    }

    static {
        LOOKUP = new Mark[values().length];
        for (Mark value : values()) {
            LOOKUP[value.id] = value;
        }
    }
}
