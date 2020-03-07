package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.advancement.criteria.TimeSimpleTrigger;

import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.EndType;
import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.Instance;

public class EndGameTrigger extends TimeSimpleTrigger<EndType, Instance> {
    private static final ResourceLocation ID = LootGames.INSTANCE.createRl("end_minigame");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     */
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        EndType type = EndType.ANY;

        if (json.has("type")) {
            String typeStr = JSONUtils.getString(json, "type");

            for (EndType loopType : EndType.values()) {
                if (loopType.name.equals(typeStr)) {
                    type = loopType;
                }
            }
        }

        return new Instance(type);
    }

    @Override
    public PerPlayerListenerSet<EndType, Instance> createListenerSet(PlayerAdvancements advancements) {
        return null;
    }

    public enum EndType {
        WIN("win"),
        LOSE("lose"),
        ANY("any");

        private String name;

        EndType(String name) {
            this.name = name;
        }
    }

    public static class Instance extends TimeSimpleTrigger.TimeCriterionInstance<EndType> {
        private EndType type;

        public Instance(@NotNull EndGameTrigger.EndType type) {
            super(ID);
            this.type = type;
        }

        @Override
        public boolean test(ServerPlayerEntity player, EndType type) {
            return this.type == EndType.ANY || this.type == type;
        }

        @Override
        public JsonElement serialize() {
            JsonObject root = new JsonObject();
            root.addProperty("type", type.name);

            return root;
        }
    }
}
