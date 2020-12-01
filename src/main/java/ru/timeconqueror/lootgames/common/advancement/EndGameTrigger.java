package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;

import static ru.timeconqueror.lootgames.common.advancement.EndGameTrigger.Instance;

public class EndGameTrigger extends AbstractCriterionTrigger<Instance> {
    private static final ResourceLocation ID = LootGames.rl("end_minigame");
    public static final String TYPE_WIN = "win";
    public static final String TYPE_LOSE = "lose";
    public static final String TYPE_ANY = "any";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.AndPredicate player, ConditionArrayParser conditionsParser) {
        return new Instance(JSONUtils.getAsString(json, "type", TYPE_ANY), player);
    }

    public void trigger(ServerPlayerEntity player, String type) {
        this.trigger(player, (instance) -> instance.matches(player, type));
    }

    public static class Instance extends CriterionInstance {
        private final String type;

        public Instance(String type, EntityPredicate.AndPredicate player) {
            super(ID, player);
            this.type = type;
        }

        public boolean matches(ServerPlayerEntity player, String type) {
            return this.type.equals(TYPE_ANY) || this.type.equals(type);
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer conditions) {
            JsonObject root = super.serializeToJson(conditions);

            root.addProperty("type", type);

            return root;
        }
    }
}
