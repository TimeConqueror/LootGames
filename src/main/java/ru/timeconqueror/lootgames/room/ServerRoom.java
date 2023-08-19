package ru.timeconqueror.lootgames.room;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.event.GenerateGameEnvironmentEvent;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.GameProgress;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.room.SSyncGamePacket;
import ru.timeconqueror.lootgames.registry.LGCapabilities;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerCodec;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

import java.util.*;

@Log4j2
public class ServerRoom extends CoffeeCapabilityInstance<LevelChunk> implements Room {
    @Getter
    private final RoomCoords coords;
    @Getter
    private final AABB roomBox;
    private final ServerLevel level;

    private final Set<UUID> pendingToEnter = new HashSet<>();
    private final CoffeeProperty<LootGame<?>> game;
    private final CoffeeProperty<GameProgress> progress = prop("progress", GameProgress.NOT_STARTED, GameProgress.SERIALIZER);

    public ServerRoom(ServerLevel level, RoomCoords coords) {
        this.level = level;
        this.coords = coords;
        this.roomBox = RoomUtils.getRoomBox(level, coords);
        game = prop("game", (LootGame<?>) null, new IPropertySerializer<>() {
            @Override
            public void serialize(@NotNull String s, LootGame<?> game, @NotNull CompoundTag compoundTag) {
                log.debug("Room [{}, {}] is being saved...", coords.x(), coords.z());
                CompoundTag tag = GameSerializer.serialize(game, SerializationType.SAVE);
                compoundTag.put(s, tag);
            }

            @Override
            public LootGame<?> deserialize(@NotNull String s, @NotNull CompoundTag compoundTag) {
                return GameSerializer.deserialize(ServerRoom.this, compoundTag.getCompound(s), SerializationType.SAVE);
            }
        });
    }

    public List<Player> getPlayers() {
        //noinspection unchecked,rawtypes
        return (List) RoomUtils.getPlayers(level, roomBox);
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Nullable
    public LootGame<?> getGame() {
        if (getProgress() != GameProgress.STARTED) {
            return null;
        }

        return game.get();
    }

    @Override
    public GameProgress getProgress() {
        return progress.get();
    }

    public void startGame() {
        var registry = LootGames.getGameInfoRegistry();
        var key = registry.getRandom();
        var info = Objects.requireNonNull(registry.getById(key));
        var game = info.createGame(key, this);
        this.game.set(game);
        this.progress.set(GameProgress.STARTED);

        info.getGenerator().generate(level, this, game);
        MinecraftForge.EVENT_BUS.post(new GenerateGameEnvironmentEvent(this, game));
        RoomGenerator.generateRoomBorders(this);

        game.start();
    }

    public void finishGame() {
        this.game.get().generateRewards();

        this.progress.set(GameProgress.FINISHED);
        this.game.set(null);
    }

    public void addPendingToEnter(ServerPlayer player) {
        pendingToEnter.add(player.getUUID());
    }

    public boolean isPendingToEnter(ServerPlayer player) {
        return pendingToEnter.contains(player.getUUID());
    }

    @Override
    public void tick() {
        pendingToEnter.clear();

        if (game.get() != null) {
            game.get().onTick();
        }
    }

    @Override
    public void syncGame() {
        forEachInRoom(player -> syncGame((ServerPlayer) player));
    }

    public void syncGame(ServerPlayer player) {
        LGNetwork.sendToPlayer(player, new SSyncGamePacket(getProgress(), getGame(), getGame() == null));
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapabilityInstance<LevelChunk>> getCapability() {
        return LGCapabilities.ROOM;
    }

    @NotNull
    @Override
    public CapabilityOwnerCodec<LevelChunk> getOwnerSerializer() {
        return CapabilityOwner.CHUNK.getSerializer();
    }

    @Override
    public void sendChangesToClient(@NotNull SimpleChannel simpleChannel, @NotNull Object o) {

    }
}
