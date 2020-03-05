package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

//TODO move to TimeCore
public interface ITimePacket {
    @NotNull
    default World getWorld(NetworkEvent.Context ctx) {
        return DistExecutor.runForDist(
                () -> () -> Minecraft.getInstance().world,
                () -> () -> ctx.getSender().world);
    }
}
