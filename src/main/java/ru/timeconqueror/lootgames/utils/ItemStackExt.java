package ru.timeconqueror.lootgames.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemStackExt {
    public static void setLore(ItemStack stack, List<Component> lore) {
        CompoundTag display = new CompoundTag();
        ListTag loreList = new ListTag();
        for (Component component : lore) {
            loreList.add(StringTag.valueOf(component.getString()));
        }
        display.put(ItemStack.TAG_LORE, loreList);
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(ItemStack.TAG_DISPLAY, display);
    }
}
