package ru.timeconqueror.lootgames.minigame.gol;

import lombok.Getter;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import ru.timeconqueror.timecore.api.util.lookups.EnumLookup;

@Getter
public enum Symbol {
    NORTH_WEST(0, new Vector2i(0, 0)),
    NORTH(1, new Vector2i(1, 0)),
    NORTH_EAST(2, new Vector2i(2, 0)),
    WEST(3, new Vector2i(0, 1)),
    EAST(4, new Vector2i(2, 1)),
    SOUTH_WEST(5, new Vector2i(0, 2)),
    SOUTH(6, new Vector2i(1, 2)),
    SOUTH_EAST(7, new Vector2i(2, 2));

    private static final EnumLookup<Symbol, Integer> LOOKUP = EnumLookup.make(Symbol.class, Symbol::getIndex);
    public static final Symbol[] NWES_SYMBOLS = new Symbol[]{NORTH, WEST, EAST, SOUTH};

    private final int index;
    private final Vector2ic pos;

    Symbol(int index, Vector2ic pos) {
        this.index = index;
        this.pos = pos;
    }

    public static Symbol byIndex(int index) {
        return LOOKUP.by(index);
    }

    public static Symbol byPos(Vector2ic pos) {
        for (Symbol symbol : values()) {
            if (symbol.pos.equals(pos)) {
                return symbol;
            }
        }

        throw new IllegalArgumentException("There's no symbol on pos " + pos);
    }

    public static boolean exists(Vector2ic pos) {
        for (Symbol symbol : values()) {
            if (symbol.pos.equals(pos)) {
                return true;
            }
        }

        return false;
    }
}