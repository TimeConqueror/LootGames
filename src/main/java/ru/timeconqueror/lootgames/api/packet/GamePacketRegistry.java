package ru.timeconqueror.lootgames.api.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.registry.TimeRegister;

import java.util.HashMap;
import java.util.Objects;

public abstract class GamePacketRegistry extends TimeRegister {
    private static final BiMap<PacketInfo, Class<? extends IServerGamePacket>> GAME_PACKET_MAP = HashBiMap.create();
    private static final HashMap<String, GamePacketManager> REGISTERED_MANAGERS = new HashMap<>();

    public GamePacketRegistry(String modid) {
        super(modid);
    }

    /**
     * Returns the mod-dependent manager to register packets from your mod.
     */
    public static GamePacketManager getManager(String modId) {
        GamePacketManager manager = REGISTERED_MANAGERS.get(modId);
        if (manager == null) {
            manager = new GamePacketManager(modId);
            REGISTERED_MANAGERS.put(modId, manager);
        }

        return manager;
    }

    @NotNull
    static Class<? extends IServerGamePacket> getPacketClass(PacketInfo info) {
        Class<? extends IServerGamePacket> packetClass = GAME_PACKET_MAP.get(info);
        if (packetClass == null) {
            throw new NullPointerException("The packet with mod id " + info.getModID() + " and id " + info.getPacketID() + " doesn't exist.");
        }

        return packetClass;
    }

    @NotNull
    static PacketInfo getInfo(Class<? extends IServerGamePacket> packetClass) {
        PacketInfo info = GAME_PACKET_MAP.inverse().get(packetClass);
        if (info == null) {
            throw new NullPointerException("The id for packet " + packetClass + " doesn't exist.");
        }

        return info;
    }

    public static class GamePacketManager {
        private final String modID;

        private GamePacketManager(String modID) {
            this.modID = modID;
        }

        public void registerPacket(int id, Class<? extends IServerGamePacket> gamePacketClass) {
            try {
                GAME_PACKET_MAP.put(new PacketInfo(modID, id), gamePacketClass);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("This packet or id has been already registered.", e);
            }
        }
    }

    public static class PacketInfo {
        private final String modID;
        private final int packetID;

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
