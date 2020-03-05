package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.network.PacketBuffer;
import org.objectweb.asm.Type;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//import ru.timeconqueror.lootgames.api.minigame.LootGame;

public interface IGamePacket<T extends LootGame> {
    void encode(PacketBuffer bufferTo);

    void decode(PacketBuffer bufferFrom);

    void runOnReceptionSide(T game);

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface RegPacket {
        Type ASM_TYPE = Type.getType(RegPacket.class);
    }
}
