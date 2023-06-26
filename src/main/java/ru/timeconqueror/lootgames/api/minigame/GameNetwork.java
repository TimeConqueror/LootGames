package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import ru.timeconqueror.lootgames.api.packet.IClientGamePacket;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;

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
    void sendUpdatePacketToNearby(IServerGamePacket packet);

    void sendUpdatePacketToNearbyExcept(ServerPlayer excepting, IServerGamePacket packet);

    /**
     * Fired on client when {@link IServerGamePacket} comes from server.
     */
    void onUpdatePacket(IServerGamePacket packet);

    /**
     * Sends update packet to the server with given {@link CompoundTag}.
     */
    void sendFeedbackPacket(IClientGamePacket packet);

    /**
     * Fired on server when {@link IClientGamePacket} comes from client.
     */
    void onFeedbackPacket(ServerPlayer sender, IClientGamePacket packet);
}
