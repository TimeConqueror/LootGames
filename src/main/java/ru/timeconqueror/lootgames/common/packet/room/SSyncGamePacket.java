package ru.timeconqueror.lootgames.common.packet.room;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.GameProgress;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.lootgames.room.client.ClientRoom;
import ru.timeconqueror.timecore.api.common.packet.IPacket;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

import java.util.Objects;

public class SSyncGamePacket implements IPacket {
    public GameProgress progress;
    public CompoundTag tag;
    public boolean fullGameSync;

    public SSyncGamePacket(GameProgress progress, @Nullable LootGame<?> game, boolean fullGameSync) {
        this.progress = progress;
        this.fullGameSync = fullGameSync;
        if (fullGameSync) {
            this.tag = GameSerializer.serialize(game, SerializationType.SYNC);
        } else {
            Objects.requireNonNull(game);
            this.tag = new CompoundTag();
            game.writeNBT(tag, SerializationType.SYNC);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(progress.ordinal());
        buf.writeNbt(tag);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        progress = GameProgress.VALUES[buf.readVarInt() % GameProgress.VALUES.length];
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
