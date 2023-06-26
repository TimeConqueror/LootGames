package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.server.level.ServerPlayer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;

public interface IClientGamePacket extends IGamePacket {
    /**
     * Do action on the main server thread upon packet receiving.
     */
    <STAGE extends Stage> void runOnServer(ServerPlayer sender, LootGame<STAGE> game);
}
