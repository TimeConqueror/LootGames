package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.util.CodecUtils;
import ru.timeconqueror.timecore.api.util.ExtraCodecs;

public class UseBlockTrigger extends SimpleCriterionTrigger<UseBlockTrigger.Instance> {
    private static final ResourceLocation ID = LootGames.rl("use_block");

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate playerPredicate, DeserializationContext conditionsParser) {
        Block block = json.has("block") ? CodecUtils.decodeStrictly(ExtraCodecs.BLOCK, CodecUtils.JSON_OPS, json.get("block")) : null;
        StatePropertiesPredicate statePredicate = StatePropertiesPredicate.fromJson(json.get("state"));
        if (block != null) {
            statePredicate.checkState(block.getStateDefinition(), (propertyIn) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + propertyIn);
            });
        }

        LocationPredicate locationPredicate = LocationPredicate.fromJson(json.get("location"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(json.get("item"));

        return new Instance(playerPredicate, block, statePredicate, locationPredicate, itemPredicate);
    }

    public void trigger(ServerPlayer player, ExtraInfo info) {
        this.trigger(player, instance -> instance.matches(player, info));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static class ExtraInfo {
        private final BlockPos pos;
        private final ItemStack item;
        private final BlockState state;

        public ExtraInfo(BlockState state, BlockPos pos, ItemStack item) {
            this.pos = pos;
            this.item = item;
            this.state = state;
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final Block block;
        private final StatePropertiesPredicate statePredicate;
        private final LocationPredicate locationPredicate;
        private final ItemPredicate itemPredicate;

        public Instance(ContextAwarePredicate playerPredicate, @Nullable Block block, StatePropertiesPredicate propertiesIn, LocationPredicate locationIn, ItemPredicate itemIn) {
            super(ID, playerPredicate);
            this.block = block;
            this.statePredicate = propertiesIn;
            this.locationPredicate = locationIn;
            this.itemPredicate = itemIn;
        }

        public static Instance forBlock(@Nullable Block block) {
            return new Instance(ContextAwarePredicate.ANY, block, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(ServerPlayer player, ExtraInfo info) {
            if (this.block != null && !info.state.is(this.block)) {
                return false;
            }

            if (!this.statePredicate.matches(info.state)) {
                return false;
            }

            ServerLevel world = player.serverLevel();

            if (!this.locationPredicate.matches(world, info.pos.getX(), info.pos.getY(), info.pos.getZ())) {
                return false;
            } else {
                return this.itemPredicate.matches(info.item);
            }
        }

        @Override
        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject root = super.serializeToJson(conditions);

            if (this.block != null) {
                root.add("block", CodecUtils.encodeStrictly(ExtraCodecs.BLOCK, CodecUtils.JSON_OPS, this.block));
            }

            root.add("state", statePredicate.serializeToJson());
            root.add("location", locationPredicate.serializeToJson());
            root.add("item", itemPredicate.serializeToJson());

            return root;
        }
    }
}
