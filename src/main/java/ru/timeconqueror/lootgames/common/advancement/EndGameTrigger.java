package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import ru.timeconqueror.lootgames.LootGames;

import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.Instance;

public class EndGameTrigger extends SimpleCriterionTrigger<Instance> {
    private static final ResourceLocation ID = LootGames.rl("end_minigame");
    public static final String TYPE_WIN = "win";
    public static final String TYPE_LOSE = "lose";
    public static final String TYPE_ANY = "any";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext conditionsParser) {
        return new Instance(GsonHelper.getAsString(json, "type", TYPE_ANY), player);
    }

    public void trigger(ServerPlayer player, String type) {
        this.trigger(player, (instance) -> instance.matches(player, type));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final String type;

        public Instance(String type, ContextAwarePredicate player) {
            super(ID, player);
            this.type = type;
        }

        public boolean matches(ServerPlayer player, String type) {
            return this.type.equals(TYPE_ANY) || this.type.equals(type);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject root = super.serializeToJson(conditions);

            root.addProperty("type", type);

            return root;
        }
    }
}
