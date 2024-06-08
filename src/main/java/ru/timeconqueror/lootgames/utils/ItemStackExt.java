package ru.timeconqueror.lootgames.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemStackExt {
    public static void addLore(ItemStack stack, List<Component> lore) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag display = tag.getCompound(ItemStack.TAG_DISPLAY);
        ListTag loreList = display.getList(ItemStack.TAG_LORE, Tag.TAG_STRING);

        for (Component component : lore) {
            loreList.add(StringTag.valueOf("\"" + component.getString() + "\""));
        }

        display.put(ItemStack.TAG_LORE, loreList);
        tag.put(ItemStack.TAG_DISPLAY, display);
    }
}
