package ru.timeconqueror.lootgames.api.packet;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.registry.TimeRegister;
import ru.timeconqueror.timecore.api.util.holder.Holder;
import ru.timeconqueror.timecore.api.util.holder.Temporal;

import java.util.ArrayList;
import java.util.List;

public class GamePacketRegister extends TimeRegister {
    private final Temporal<List<Class<? extends IClientGamePacket>>> clientPackets = Temporal.of(new ArrayList<>());
    private final Temporal<List<Class<? extends IServerGamePacket>>> serverPackets = Temporal.of(new ArrayList<>());

    public GamePacketRegister(String modId) {
        super(modId);
    }

    public void regClientPacket(Class<? extends IClientGamePacket> packetClass) {
        clientPackets.get().add(packetClass);
    }

    public void regServerPacket(Class<? extends IServerGamePacket> packetClass) {
        serverPackets.get().add(packetClass);
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onSetup);
    }

    private void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            catchErrors(event, () -> {
                clientPackets.doAndRemove(classes -> {
                    Holder<Integer> i = new Holder<>(0);

                    classes.forEach(clazz -> {
                        GamePacketRegistry.clientStorage().regPacket(getModId(), i.get(), clazz);
                        i.set(i.get() + 1);
                    });
                });
                serverPackets.doAndRemove(classes -> {
                    Holder<Integer> i = new Holder<>(0);

                    classes.forEach(clazz -> {
                        GamePacketRegistry.serverStorage().regPacket(getModId(), i.get(), clazz);
                        i.set(i.get() + 1);
                    });
                });
            });
        });
    }
}
