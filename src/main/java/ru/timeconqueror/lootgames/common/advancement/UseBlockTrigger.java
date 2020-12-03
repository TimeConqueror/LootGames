package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.util.CodecUtils;
import ru.timeconqueror.timecore.util.ExtraCodecs;

public class UseBlockTrigger extends AbstractCriterionTrigger<UseBlockTrigger.Instance> {
    private static final ResourceLocation ID = LootGames.rl("use_block");

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.AndPredicate playerPredicate, ConditionArrayParser conditionsParser) {
        Block block = json.has("block") ? CodecUtils.decodeStrictly(ExtraCodecs.BLOCK_CODEC, CodecUtils.JSON_OPS, json.get("block")) : null;
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

    public void trigger(ServerPlayerEntity player, ExtraInfo info) {
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

    public static class Instance extends CriterionInstance {
        private final Block block;
        private final StatePropertiesPredicate statePredicate;
        private final LocationPredicate locationPredicate;
        private final ItemPredicate itemPredicate;

        public Instance(EntityPredicate.AndPredicate playerPredicate, @Nullable Block block, StatePropertiesPredicate propertiesIn, LocationPredicate locationIn, ItemPredicate itemIn) {
            super(ID, playerPredicate);
            this.block = block;
            this.statePredicate = propertiesIn;
            this.locationPredicate = locationIn;
            this.itemPredicate = itemIn;
        }

        public static Instance forBlock(@Nullable Block block) {
            return new Instance(EntityPredicate.AndPredicate.ANY, block, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity player, ExtraInfo info) {
            if (this.block != null && !info.state.is(this.block)) {
                return false;
            }

            if (!this.statePredicate.matches(info.state)) {
                return false;
            }

            ServerWorld world = player.getLevel();

            if (!this.locationPredicate.matches(world, info.pos.getX(), info.pos.getY(), info.pos.getZ())) {
                return false;
            } else {
                return this.itemPredicate.matches(info.item);
            }
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer conditions) {
            JsonObject root = super.serializeToJson(conditions);

            if (this.block != null) {
                root.add("block", CodecUtils.encodeStrictly(ExtraCodecs.BLOCK_CODEC, CodecUtils.JSON_OPS, this.block));
            }

            root.add("state", statePredicate.serializeToJson());
            root.add("location", locationPredicate.serializeToJson());
            root.add("item", itemPredicate.serializeToJson());

            return root;
        }
    }
}
