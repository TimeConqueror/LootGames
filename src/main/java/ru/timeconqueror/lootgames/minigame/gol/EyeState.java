package ru.timeconqueror.lootgames.minigame.gol;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.util.lookups.EnumLookup;

import java.util.EnumMap;
import java.util.Map;

public enum EyeState {
    NORTH_WEST(0, Symbol.NORTH_WEST),
    NORTH(1, Symbol.NORTH),
    NORTH_EAST(2, Symbol.NORTH_EAST),
    WEST(3, Symbol.WEST),
    EAST(4, Symbol.EAST),
    SOUTH_WEST(5, Symbol.SOUTH_WEST),
    SOUTH(6, Symbol.SOUTH),
    SOUTH_EAST(7, Symbol.SOUTH_EAST),
    STARING(8),
    THINKING(9),
    ACCEPTED(10),
    FAILED(11);

    @Getter
    private final int index;
    @Nullable
    private final Symbol symbol;

    private static final Map<Symbol, EyeState> SYMBOL_TO_EYE;
    public static final EnumLookup<EyeState, Integer> LOOKUP = EnumLookup.makeFromOrdinal(EyeState.class);

    static {
        SYMBOL_TO_EYE = new EnumMap<>(Symbol.class);
        for (EyeState eye : EyeState.values()) {
            if(eye.symbol != null) {
                if (SYMBOL_TO_EYE.put(eye.symbol, eye) != null) {
                    throw new IllegalArgumentException("Duplicated symbol: " + eye.symbol);
                }
            }
        }

        for (Symbol symbol : Symbol.values()) {
            if (!SYMBOL_TO_EYE.containsKey(symbol)) {
                throw new IllegalStateException("Not all symbols are mapped to eye state: " + symbol);
            }
        }
    }

    EyeState(int index) {
        this(index, null);
    }

    EyeState(int index, @Nullable Symbol symbol) {
        this.index = index;
        this.symbol = symbol;
    }

    public static EyeState ofSymbol(Symbol symbol) {
        return SYMBOL_TO_EYE.get(symbol);
    }
}
