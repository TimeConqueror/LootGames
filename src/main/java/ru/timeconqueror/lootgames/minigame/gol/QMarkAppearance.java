package ru.timeconqueror.lootgames.minigame.gol;

import lombok.Getter;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.client.ClientProxy;
import ru.timeconqueror.timecore.api.util.lookups.EnumLookup;

public class QMarkAppearance {
    private static final State[] HANDLED = {State.ACCEPTED, State.DENIED};
    @Getter
    private final State state;
    private final long startTime;

    public QMarkAppearance(State state, long startTime) {
        if (!canBeHandled(state)) {
            throw new IllegalArgumentException();
        }

        this.state = state;
        this.startTime = startTime;
    }

    public boolean isFinished() {
        return ClientProxy.level().getGameTime() > startTime + 16L;
    }

    public static boolean canBeHandled(State state) {
        return CollectionUtils.contains(HANDLED, state);
    }

    public enum State {
        NONE,
        SHOWING,
        ACCEPTED,
        DENIED;

        public static final EnumLookup<State, Integer> LOOKUP = EnumLookup.makeFromOrdinal(State.class);
    }
}
