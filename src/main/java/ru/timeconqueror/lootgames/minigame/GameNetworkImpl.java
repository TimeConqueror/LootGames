package ru.timeconqueror.lootgames.minigame;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.PacketDistributor;
import ru.timeconqueror.lootgames.api.minigame.GameNetwork;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.api.packet.ClientGamePacket;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.common.packet.CPacketGameUpdate;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.SPacketGameUpdate;
import ru.timeconqueror.timecore.api.util.PlayerUtils;

import static ru.timeconqueror.lootgames.api.minigame.LootGame.DEBUG_MARKER;

@Log4j2
@AllArgsConstructor
public class GameNetworkImpl implements GameNetwork {
    private final Room room;
    private final LootGame<?> game;

    @Override
    public void sendTo(Player player, MutableComponent component) {
        PlayerUtils.sendMessage(player, component);
    }

    @Override
    public void sendTo(Player player, MutableComponent component, ChatFormatting format) {
        sendTo(player, component.withStyle(format));
    }

    @Override
    public void sendTo(Player player, MutableComponent component, NotifyColor format) {
        sendTo(player, component, format.getColor());
    }

    @Override
    public void sendToAllInRoom(MutableComponent component) {
        room.forEachInRoom(player -> PlayerUtils.sendMessage(player, component));
    }

    @Override
    public void sendToAllInRoom(MutableComponent component, ChatFormatting format) {
        sendToAllInRoom(component.withStyle(format));
    }

    @Override
    public void sendToAllInRoom(MutableComponent component, NotifyColor format) {
        sendToAllInRoom(component, format.getColor());
    }

    @Override
    public void saveAndSync() {
        save();
        room.syncGame();
    }

    @Override
    public void save() {
        ChunkPos chunkPos = room.getCoords().containerPos();
        room.getLevel().getChunk(chunkPos.x, chunkPos.z).setUnsaved(true);
    }

    @Override
    public void sendUpdatePacketToNearby(ServerGamePacket packet) {
        if (!game.isServerSide()) {
            return;
        }

        room.forEachInRoom(player -> LGNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                new SPacketGameUpdate(game, packet)));
        log.debug(DEBUG_MARKER, () -> game.logMessage("update packet '{}' was sent.", packet.getClass().getSimpleName()));
    }

    @Override
    public void sendUpdatePacketToNearbyExcept(ServerPlayer excepting, ServerGamePacket packet) {
        if (!game.isServerSide()) {
            return;
        }

        room.forEachInRoom(player -> {
            if (!player.getUUID().equals(excepting.getUUID())) {
                LGNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SPacketGameUpdate(game, packet));
            }
        });

        log.debug(DEBUG_MARKER, () -> game.logMessage("update packet '{}' to all tracking except {} was sent.", packet.getClass().getSimpleName(), excepting.getName()));
    }

    @Override
    public void onUpdatePacket(ServerGamePacket packet) {
        packet.runOnClient(game);
    }

    @Override
    public void sendFeedbackPacket(ClientGamePacket packet) {
        if (game.isServerSide()) {
            return;
        }

        LGNetwork.INSTANCE.sendToServer(new CPacketGameUpdate(game, packet));
        log.debug(DEBUG_MARKER, () -> game.logMessage("feedback packet '{}' was sent.", packet.getClass().getSimpleName()));
    }

    @Override
    public void onFeedbackPacket(ServerPlayer sender, ClientGamePacket packet) {
        packet.runOnServer(sender, game);
    }
}
