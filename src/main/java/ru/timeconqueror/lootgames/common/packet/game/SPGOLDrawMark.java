package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.gol.QMarkAppearance.State;

import java.io.IOException;

public class SPGOLDrawMark implements IServerGamePacket {
    private State state;

    private SPGOLDrawMark(State state) {
        this.state = state;
    }

    public static SPGOLDrawMark accepted() {
        return new SPGOLDrawMark(State.ACCEPTED);
    }

    public static SPGOLDrawMark denied() {
        return new SPGOLDrawMark(State.DENIED);
    }

    /**
     * Only for using via reflection
     */
    @Deprecated
    public SPGOLDrawMark() {
    }

    @Override
    public void encode(FriendlyByteBuf bufferTo) throws IOException {
        bufferTo.writeVarInt(State.LOOKUP.from(state));
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) throws IOException {
        this.state = State.LOOKUP.by(bufferFrom.readVarInt());
    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        if (game instanceof GameOfLight) {
            ((GameOfLight) game).addMarkAppearance(this.state);
        }
    }
}
