package ru.timeconqueror.lootgames.api.packet;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;

public interface IServerGamePacket extends IGamePacket {
    <S extends Stage> void runOnClient(LootGame<S> game);
}
