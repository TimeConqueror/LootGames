package ru.timeconqueror.lootgames.common.packet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.forgespi.language.ModFileScanData;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.packet.IGamePacket;

import java.util.Collection;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";//TODO CHECk between versions
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(LootGames.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void registerPackets() {
        int idx = 0;
//        INSTANCE.registerMessage(idx++, SMessageGameUpdate.class, SMessageGameUpdate::encode, SMessageGameUpdate::decode, SMessageGameUpdate::handle);

        registerGamePackets();
    }

    public static void registerGamePackets() {
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(annotationData -> annotationData.getAnnotationType().equals(IGamePacket.RegPacket.ASM_TYPE))
                .forEach(annotationData -> {
                    try {
                        Class.forName(annotationData.getClassType().getClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
