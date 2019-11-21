package ru.timeconqueror.lootgames.api.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NBTUtils {

    /***
     * Reads two-dimensional array of any object from NBT.
     *
     * @param tableTag                  tag that is created via {@link #writeTwoDimArrToNBT(INBTSerializable[][])}
     * @param elementClass              array objects' class.
     * @param defaultElementCreator     here you must create element with default fields.
     *                                      The fields of this object will be re-written via {@link INBTSerializable#deserializeNBT(NBTBase)} and then added to array.
     * @param <T>                       the type of objects in array to return.
     */
    @SuppressWarnings("unchecked")
    public static <NBT extends NBTBase, T extends INBTSerializable<NBT>> T[][] readTwoDimArrFromNBT(NBTTagCompound tableTag, Class<T> elementClass, Supplier<T> defaultElementCreator) {
        int size = tableTag.getInteger("size");

        T[][] table = null;

        for (int i = 0; i < size; i++) {
            NBTTagCompound columnTag = tableTag.getCompoundTag(Integer.toString(i));
            int columnSize = columnTag.getInteger("size");

            T[] column = (T[]) Array.newInstance(elementClass, columnSize);

            for (int j = 0; j < columnSize; j++) {
                if (columnTag.hasKey(Integer.toString(j))) {
                    NBT elementTag = (NBT) columnTag.getTag(Integer.toString(j));

                    T element = defaultElementCreator.get();
                    element.deserializeNBT(elementTag);
                    column[j] = element;
                }
            }

            if (i == 0) {
                Class columnClass = column.getClass();
                table = (T[][]) Array.newInstance(columnClass, size);
            }

            table[i] = column;
        }

        return table;
    }

    /**
     * Writes two-dimensional array of any object to NBT.
     *
     * @param objArr two-dimensional array of data to save.
     * @param <T>    must implement {@link INBTSerializable}!
     * @return {@link NBTTagCompound} tag that contains this two-dimensional array.
     */
    public static <NBT extends NBTBase, T extends INBTSerializable<NBT>> NBTTagCompound writeTwoDimArrToNBT(T[][] objArr) {
        return writeTwoDimArrToNBT(objArr, (Predicate<T>) e -> true);
    }

    /**
     * Writes two-dimensional array of any object to NBT.
     *
     * @param objArr         two-dimensional array of data to save.
     * @param <T>            must implement {@link INBTSerializable}!
     * @param writeElementIf controls whether element will be written or not. If element will be rejected by your {@link Predicate}, then
     *                       the element of massive will be null after reading this NBT Compound.
     * @return {@link NBTTagCompound} tag that contains this two-dimensional array.
     */
    public static <NBT extends NBTBase, T extends INBTSerializable<NBT>> NBTTagCompound writeTwoDimArrToNBT(T[][] objArr, Predicate<T> writeElementIf) {
        NBTTagCompound tableTag = new NBTTagCompound();

        for (int i = 0; i < objArr.length; i++) {
            NBTTagCompound column = new NBTTagCompound();

            for (int j = 0; j < objArr[i].length; j++) {
                if (writeElementIf.test(objArr[i][j])) {
                    NBT elementTag = objArr[i][j].serializeNBT();
                    column.setTag(Integer.toString(j), elementTag);
                }
            }

            column.setInteger("size", objArr[i].length);

            tableTag.setTag(Integer.toString(i), column);
        }

        tableTag.setInteger("size", objArr.length);

        return tableTag;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] readTwoDimArrFromNBT(NBTTagCompound tableTag, Class<T> elementClass, Function<NBTTagCompound, T> deserializer) {
        int size = tableTag.getInteger("size");

        T[][] table = null;

        for (int i = 0; i < size; i++) {
            NBTTagCompound columnTag = tableTag.getCompoundTag(Integer.toString(i));
            int columnSize = columnTag.getInteger("size");

            T[] column = (T[]) Array.newInstance(elementClass, columnSize);

            for (int j = 0; j < columnSize; j++) {
                if (columnTag.hasKey(Integer.toString(j))) {
                    NBTTagCompound elementTag = (NBTTagCompound) columnTag.getTag(Integer.toString(j));

                    column[j] = deserializer.apply(elementTag);
                }
            }

            if (i == 0) {
                Class columnClass = column.getClass();
                table = (T[][]) Array.newInstance(columnClass, size);
            }

            table[i] = column;
        }

        return table;
    }

    /**
     * Writes two-dimensional array of any object to NBT.
     *
     * @param objArr     two-dimensional array of data to save.
     * @param serializer realization of "{@code <T>} to NBTTagCompound" serialization.
     * @return {@link NBTTagCompound} tag that contains this two-dimensional array.
     */
    public static <T> NBTTagCompound writeTwoDimArrToNBT(T[][] objArr, Function<T, NBTTagCompound> serializer) {
        return writeTwoDimArrToNBT(objArr, serializer, e -> true);
    }

    /**
     * Writes two-dimensional array of any object to NBT.
     *
     * @param objArr         two-dimensional array of data to save.
     * @param serializer     realization of "{@code <T>} to NBTTagCompound" serialization.
     * @param writeElementIf controls whether element will be written or not. If element will be rejected by your {@link Predicate}, then
     *                       the element of massive will be null after reading this NBT Compound.
     * @return {@link NBTTagCompound} tag that contains this two-dimensional array.
     */
    public static <T> NBTTagCompound writeTwoDimArrToNBT(T[][] objArr, Function<T, NBTTagCompound> serializer, Predicate<T> writeElementIf) {
        NBTTagCompound tableTag = new NBTTagCompound();

        for (int i = 0; i < objArr.length; i++) {
            NBTTagCompound column = new NBTTagCompound();

            for (int j = 0; j < objArr[i].length; j++) {
                if (writeElementIf.test(objArr[i][j])) {
                    NBTTagCompound elementTag = serializer.apply(objArr[i][j]);
                    column.setTag(Integer.toString(j), elementTag);
                }
            }

            column.setInteger("size", objArr[i].length);

            tableTag.setTag(Integer.toString(i), column);
        }

        tableTag.setInteger("size", objArr.length);

        return tableTag;
    }
}
