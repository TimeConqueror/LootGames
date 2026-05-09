package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;
import ru.timeconqueror.lootgames.minigame.gol.EyeState;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;

import java.io.IOException;

public class SPGOLSetTimedEyeState implements ServerGamePacket {
    private EyeState state;

    private SPGOLSetTimedEyeState(EyeState state) {
        this.state = state;
    }

    public static SPGOLSetTimedEyeState accepted() {
        return new SPGOLSetTimedEyeState(EyeState.ACCEPTED);
    }

    public static SPGOLSetTimedEyeState failed() {
        return new SPGOLSetTimedEyeState(EyeState.FAILED);
    }

    /**
     * Only for using via reflection
     */
    @Deprecated
    public SPGOLSetTimedEyeState() {
    }

    @Override
    public void encode(FriendlyByteBuf bufferTo) throws IOException {
        bufferTo.writeVarInt(EyeState.LOOKUP.from(state));
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) throws IOException {
        this.state = EyeState.LOOKUP.by(bufferFrom.readVarInt());
    }

    @Override
    public <S extends Stage> void runOnClient(LootGame<S> game) {
        ((GameOfLight) game).setClientTimedEyeState(this.state);
    }
}
