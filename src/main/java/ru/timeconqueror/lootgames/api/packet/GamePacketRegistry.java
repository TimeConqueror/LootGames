package ru.timeconqueror.lootgames.api.packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GamePacketRegistry {
    private static final BiMap<PacketInfo, Class<? extends IGamePacket<?>>> GAME_PACKET_MAP = HashBiMap.create();
    private static final Set<String> REGISTERED_MOD_IDS = new HashSet<>();

    public static GamePacketManager newManager(String modID) {
        if (!REGISTERED_MOD_IDS.add(modID)) {
            throw new IllegalArgumentException("The mod id " + modID + " is already registered!");
        }
        return new GamePacketManager(modID);
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

        public void registerPacket(int id, Class<? extends IGamePacket<?>> gamePacketClass) {
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
