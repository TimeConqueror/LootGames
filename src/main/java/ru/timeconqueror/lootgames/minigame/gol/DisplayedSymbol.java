package ru.timeconqueror.lootgames.minigame.gol;

public class DisplayedSymbol {
    private final long startTick;
    private final Symbol symbol;
    private final int ticksToLive;

    public DisplayedSymbol(int startTick, Symbol symbol, int ticksToLive) {
        this.startTick = startTick;
        this.symbol = symbol;
        this.ticksToLive = ticksToLive;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public boolean isEnded(int tickExisted) {
        return startTick + ticksToLive < tickExisted;
    }
}