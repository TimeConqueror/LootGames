package ru.timeconqueror.lootgames.common.packet.room;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.lootgames.room.client.ClientRoom;
import ru.timeconqueror.timecore.api.common.packet.IPacket;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

import java.util.Objects;

public class SSyncGamePacket implements IPacket {
    public CompoundTag tag;
    public boolean fromScratch;

    public SSyncGamePacket(@Nullable LootGame<?> game, boolean fromScratch) {
        this.fromScratch = fromScratch;
        if (fromScratch) {
            this.tag = GameSerializer.serialize(game, SerializationType.SYNC);
        } else {
            Objects.requireNonNull(game);
            this.tag = new CompoundTag();
            game.writeNBT(tag, SerializationType.SYNC);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    @Override
    public void handleOnClient(NetworkEvent.Context ctx) {
        ClientRoom instance = ClientRoom.getInstance();
        if (instance == null) {
            LootGames.LOGGER.error("Can't send SSyncGamePacket, because client room is null");
            return;
        }

        instance.handleSyncGamePacket(this);
    }
}
