package ru.timeconqueror.lootgames.room;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.event.GenerateGameEnvironmentEvent;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.api.room.RoomProgress;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.common.packet.room.SSyncGamePacket;
import ru.timeconqueror.lootgames.common.packet.room.SUpdateRoomPacket;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGCapabilities;
import ru.timeconqueror.lootgames.registry.custom.GameDefinition;
import ru.timeconqueror.lootgames.utils.Utils;
import ru.timeconqueror.timecore.api.common.blockentity.SerializationType;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ServerRoom extends CoffeeCapabilityInstance<LevelChunk> implements Room {
    @Getter
    private final RoomCoords coords;
    @Getter
    private final AABB roomBox;
    private final ServerLevel level;

    private final Set<UUID> pendingToEnter = new HashSet<>();
    private final CoffeeProperty<LootGame<?>> game;
    private final CoffeeProperty<RoomProgress> progress = prop("progress", RoomProgress.NOT_GENERATED, RoomProgress.SERIALIZER);
    private final CoffeeProperty<EntryPoint> entryPoint = prop("entry", (EntryPoint) null, EntryPoint.SERIALIZER.nullable());

    private static final TicketType<Integer> LOAD_BEFORE_TELEPORT = TicketType.create(LootGames.MODID + ":" + "load_before_teleport", Integer::compareTo, 5 * 20);

    public ServerRoom(CapabilityOwnerType<LevelChunk> ownerType, ServerLevel level, RoomCoords coords) {
        super(ownerType);
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
        return game.get();
    }

    @Nullable
    public EntryPoint getEntryPoint() {
        return entryPoint.get();
    }

    @Override
    public RoomProgress getProgress() {
        return progress.get();
    }

    public void generate(GameDefinition gameDefinition) {
        var game = gameDefinition.createGame(this);
        this.game.set(game);

        Stopwatch stopwatch = Stopwatch.createStarted();
        log.debug("[Generation] Room generating started.");

        gameDefinition.getGenerator().generate(level, this, game);
        log.debug("[Generation] GameDefinition's generator took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset();

        MinecraftForge.EVENT_BUS.post(new GenerateGameEnvironmentEvent(this, game));
        log.debug("[Generation] GenerateGameEnvironmentEvent took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset();

        RoomGenerator.generateRoomBorders(this);
        log.debug("[Generation] Room borders generation took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        game.start();

        this.progress.set(RoomProgress.GENERATED);
        save();
    }

    public void updateEntryPoint(@Nullable EntryPoint newEntryPoint) {
        this.entryPoint.set(newEntryPoint);
        save();
    }

    public void sendTeleportTicket(Entity entity) {
        GameDefinition gameDefinition = game.get().getGameDefinition();
        BlockPos teleportPos = gameDefinition.getTeleportPos().toAbsolutePos(coords);

        getLevel().getChunkSource().addRegionTicket(LOAD_BEFORE_TELEPORT, new ChunkPos(teleportPos), 1, entity.getId());
    }

    public void teleportInside(Entity entity) {
        if (!isGenerated()) {
            log.error("Cannot teleport inside, room {} is not generated", coords);
            return;
        }

        if (entity instanceof ServerPlayer player) {
            addPendingToEnter(player);
        }

        GameDefinition gameDefinition = game.get().getGameDefinition();
        BlockPos teleportPos = gameDefinition.getTeleportPos().toAbsolutePos(coords);
        ServerLevel level = getLevel();

        entity.teleportTo(level, teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5, Set.of(), gameDefinition.getTeleportYaw(), 0);
    }

    public void teleportAway(ServerPlayer player) {
        if (!teleportToEntryPoint(player)) {
            Utils.teleportToSpawn(player);
        }
    }

    private boolean teleportToEntryPoint(ServerPlayer player) {
        EntryPoint entryPoint = getEntryPoint();
        if (entryPoint == null) {
            return false;
        }
        ServerLevel level = player.server.getLevel(entryPoint.entryDimension());
        BlockPos pos = entryPoint.entryPos();
        if (level == null) {
            return false;
        }

        if (level.getBlockState(pos).getBlock() != LGBlocks.MICROVERSE_MANIPULATOR) {
            // means entry-point was changed (f.e. block is broken by another player)
            updateEntryPoint(null);
            return false;
        }

        return RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, level, pos)
                .map(teleportPos -> {
                    float yaw = Utils.getLookAngle(Utils.vec3ToVec3f(teleportPos), Utils.vec3iToVec3f(pos));
                    player.teleportTo(level, teleportPos.x(), teleportPos.y(), teleportPos.z(), yaw, 0);
                    return true;
                })
                .orElse(false);
    }

    public void finishGame() {
        this.progress.set(RoomProgress.FINISHED);
        save();
        syncNonGameProperties();
        syncGame();
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
    public void save() {
        ChunkPos chunkPos = getCoords().containerPos();
        LevelChunk chunk = getLevel().getChunk(chunkPos.x, chunkPos.z);
        save(chunk);
    }

    @Override
    public void syncGame() {
        forEachInRoom(player -> syncGame((ServerPlayer) player));
    }

    public void syncGame(ServerPlayer player) {
        LGNetwork.sendToPlayer(player, new SSyncGamePacket(getProgress(), getGame(), getGame() == null));
    }

    public void syncNonGameProperties() {
        forEachInRoom(player -> LGNetwork.sendToPlayer((ServerPlayer) player, new SUpdateRoomPacket(getProgress())));
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapabilityInstance<LevelChunk>> getCapability() {
        return LGCapabilities.ROOM;
    }

    @Override
    public void sendChangesToClient(@NotNull SimpleChannel simpleChannel, @NotNull Object message) {
    }
}
