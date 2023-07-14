package ru.timeconqueror.lootgames.api.packet;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;

public interface ServerGamePacket extends GamePacket {
    <S extends Stage> void runOnClient(LootGame<S> game);
}
