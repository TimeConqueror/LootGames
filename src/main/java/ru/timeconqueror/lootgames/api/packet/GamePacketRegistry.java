package ru.timeconqueror.lootgames.api.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.registry.Initable;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.api.registry.TimeRegistry;

import java.util.HashMap;
import java.util.Objects;

/**
 * Used for game packet adding. You may extend it and do your stuff in {@link #register()} method.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistrable}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 */
public abstract class GamePacketRegistry extends TimeRegistry implements Initable {
    private static final BiMap<PacketInfo, Class<? extends IGamePacket<?>>> GAME_PACKET_MAP = HashBiMap.create();
    private static final HashMap<String, GamePacketManager> REGISTERED_MANAGERS = new HashMap<>();

    /**
     * Returns the mod-dependent manager to register packets from your mod.
     */
    public static GamePacketManager getManager() {
        String modID = getModID();
        GamePacketManager manager = REGISTERED_MANAGERS.get(modID);
        if (manager == null) {
            manager = new GamePacketManager(modID);
            REGISTERED_MANAGERS.put(modID, manager);
        }

        return manager;
    }

    @NotNull
    static Class<? extends IGamePacket<?>> getPacketClass(PacketInfo info) {
        Class<? extends IGamePacket<?>> packetClass = GAME_PACKET_MAP.get(info);
        if (packetClass == null) {
            throw new NullPointerException("The packet with mod id " + info.getModID() + " and id " + info.getPacketID() + " doesn't exist.");
        }

        return packetClass;
    }

    @NotNull
    static PacketInfo getInfo(Class<? extends IGamePacket<?>> packetClass) {
        PacketInfo info = GAME_PACKET_MAP.inverse().get(packetClass);
        if (info == null) {
            throw new NullPointerException("The id for packet " + packetClass + " doesn't exist.");
        }

        return info;
    }

    public static class GamePacketManager {
        private String modID;

        private GamePacketManager(String modID) {
            this.modID = modID;
        }

        protected void registerPacket(int id, Class<? extends IGamePacket<?>> gamePacketClass) {
            try {
                GAME_PACKET_MAP.put(new PacketInfo(modID, id), gamePacketClass);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("This packet or id has been already registered.", e);
            }
        }
    }

    public static class PacketInfo {
        private String modID;
        private int packetID;

        public PacketInfo(String modID, int packetID) {
            this.modID = modID;
            this.packetID = packetID;
        }

        public String getModID() {
            return modID;
        }

        public int getPacketID() {
            return packetID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PacketInfo)) return false;
            PacketInfo that = (PacketInfo) o;
            return packetID == that.packetID &&
                    modID.equals(that.modID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modID, packetID);
        }
    }
}
