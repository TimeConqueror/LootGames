package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import ru.timeconqueror.lootgames.api.packet.ClientGamePacket;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;

public interface GameNetwork {
    void sendTo(Player player, MutableComponent component);

    void sendTo(Player player, MutableComponent component, ChatFormatting format);

    void sendTo(Player player, MutableComponent component, NotifyColor format);

    void sendToAllInRoom(MutableComponent component);

    void sendToAllInRoom(MutableComponent component, ChatFormatting format);

    void sendToAllInRoom(MutableComponent component, NotifyColor format);

    /**
     * Sends update to client.
     */
    void saveAndSync();

    void save();

    /**
     * Sends update packet to the client with given {@link CompoundTag} to all players, tracking the game.
     */
    void sendUpdatePacketToNearby(ServerGamePacket packet);

    void sendUpdatePacketToNearbyExcept(ServerPlayer excepting, ServerGamePacket packet);

    /**
     * Fired on client when {@link ServerGamePacket} comes from server.
     */
    void onUpdatePacket(ServerGamePacket packet);

    /**
     * Sends update packet to the server with given {@link CompoundTag}.
     */
    void sendFeedbackPacket(ClientGamePacket packet);

    /**
     * Fired on server when {@link ClientGamePacket} comes from client.
     */
    void onFeedbackPacket(ServerPlayer sender, ClientGamePacket packet);
}
