package ru.timeconqueror.lootgames.room;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

public class GameSerializer {
    @Nullable
    public static LootGame<?> deserialize(Room room, CompoundTag tag, SerializationType type) {
        if (!tag.contains("id")) {
            return null;
        }

        String id = tag.getString("id");
        ResourceLocation location = new ResourceLocation(id);
        GameInfo gameInfo = LootGames.getGameInfoRegistry().getRegistry().getValue(location);
        if (gameInfo == null) {
            LootGames.LOGGER.warn("Can't find game factory for {}", location);
            return null;
        }

        LootGame<?> game = gameInfo.createGame(location, room);
        game.readNBT(tag.getCompound("game"), type);
        return game;
    }

    public static CompoundTag serialize(@Nullable LootGame<?> game, SerializationType type) {
        CompoundTag out = new CompoundTag();
        if (game == null) {
            return out;
        }

        CompoundTag gameTag = new CompoundTag();
        game.writeNBT(gameTag, type);

        ResourceLocation id = game.getId();

        out.putString("id", id.toString());
        out.put("game", gameTag);
        return out;
    }

    public static <STAGE extends Stage> void serializeStage(LootGame<STAGE> game, CompoundTag nbt, SerializationType serializationType) {
        Stage stage = game.getStage();
        CompoundTag stageWrapper = new CompoundTag();
        stageWrapper.put("stage", stage.serialize(serializationType));
        stageWrapper.putString("id", stage.getID());
        nbt.put("stage_wrapper", stageWrapper);
    }

    public static <S extends Stage, T extends LootGame<S>> S deserializeStage(LootGame<S> game, CompoundTag nbt, SerializationType serializationType) {
        CompoundTag stageWrapper = nbt.getCompound("stage_wrapper");
        return game.createStageFromNBT(stageWrapper.getString("id"), stageWrapper.getCompound("stage"), serializationType);
    }
}
