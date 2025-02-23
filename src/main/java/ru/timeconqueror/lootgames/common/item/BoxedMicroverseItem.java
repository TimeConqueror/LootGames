package ru.timeconqueror.lootgames.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGItems;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;

import java.util.List;

public class BoxedMicroverseItem extends Item implements IClientItemExtensions {
    public BoxedMicroverseItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Component
                .translatable("item.lootgames.boxed_microverse.tooltip.1")
                .withStyle(ChatFormatting.BLUE));

        Item item = stack.getItem();
        Component gameName = null;
        if(item == LGItems.BOXED_UNKNOWN_MICROVERSE) {
            gameName = Component.literal("???");
        } else {
           GameInfo info = LootGames.getGameInfoRegistry().getGameInfoByHolder(item);
           if(info != null) {
               gameName = Component.translatable(info.getLangKey());
           }
        }

        if(gameName != null) {
            tooltip.add(Component
                    .translatable("item.lootgames.boxed_microverse.tooltip.2", gameName)
                    .withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, world, tooltip, options);
    }

    @Nullable
    public static RoomCoords getBoundRoomCoords(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("room_coords")) {
                int[] roomCoords = tag.getIntArray("room_coords");
                return new RoomCoords(roomCoords[0], roomCoords[1]);
            }
        }

        return null;
    }
}
