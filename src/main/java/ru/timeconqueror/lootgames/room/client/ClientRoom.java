package ru.timeconqueror.lootgames.room.client;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.GameProgress;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.common.packet.room.SLoadRoomPacket;
import ru.timeconqueror.lootgames.common.packet.room.SSyncGamePacket;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

import java.util.List;

public class ClientRoom implements Room {
    @Nullable
    private static ClientRoom instance;

    @Getter
    private final RoomCoords coords;
    @Nullable
    @Getter
    private LootGame<?> game;
    @Getter
    private final GameProgress progress;
    @Getter
    private final AABB roomBox;

    public ClientRoom(SLoadRoomPacket packet) {
        this.coords = packet.roomCoords;
        this.roomBox = RoomUtils.getRoomBox(getLevel(), packet.roomCoords);
        this.game = GameSerializer.deserialize(this, packet.gameTag, SerializationType.SYNC);
        this.progress = packet.progress;
    }

    @Override
    public void tick() {
        if (game != null) {
            game.onTick();
        }
    }

    @Override
    public List<Player> getPlayers() {
        return getLevel().getEntitiesOfClass(Player.class, roomBox);
    }

    @Override
    public Level getLevel() {
        return Minecraft.getInstance().level;
    }

    @Nullable
    public static ClientRoom getInstance() {
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public static void handleLoadRoomPacket(SLoadRoomPacket packet) {
        instance = new ClientRoom(packet);
    }

    public void handleSyncGamePacket(SSyncGamePacket packet) {
        if (packet.fullGameSync) {
            game = GameSerializer.deserialize(this, packet.tag, SerializationType.SYNC);
        } else {
            if (game == null) {
                LootGames.LOGGER.error("Can't handle SSyncGamePacket, because client game is null");
                return;
            }

            game.readNBT(packet.tag, SerializationType.SYNC);
        }
    }
}
