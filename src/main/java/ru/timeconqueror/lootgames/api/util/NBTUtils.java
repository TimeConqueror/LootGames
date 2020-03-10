package ru.timeconqueror.lootgames.api.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
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
     *                                      The fields of this object will be re-written via {@link INBTSerializable#deserializeNBT(INBT)} and then added to array.
     * @param <T>                       the type of objects in array to return.
     */
    @SuppressWarnings("unchecked")
    public static <NBT extends INBT, T extends INBTSerializable<NBT>> T[][] readTwoDimArrFromNBT(CompoundNBT tableTag, Class<T> elementClass, Supplier<T> defaultElementCreator) {
        int size = tableTag.getInt("size");

        T[][] table = null;

        for (int i = 0; i < size; i++) {
            CompoundNBT columnTag = tableTag.getCompound(Integer.toString(i));
            int columnSize = columnTag.getInt("size");

            T[] column = (T[]) Array.newInstance(elementClass, columnSize);

            for (int j = 0; j < columnSize; j++) {
                if (columnTag.contains(Integer.toString(j))) {
                    NBT elementTag = (NBT) columnTag.get(Integer.toString(j));

                    T element = defaultElementCreator.get();
                    element.deserializeNBT(elementTag);
                    column[j] = element;
                }
            }

            if (i == 0) {
                Class<?> columnClass = column.getClass();
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
     * @return {@link CompoundNBT} tag that contains this two-dimensional array.
     */
    public static <NBT extends INBT, T extends INBTSerializable<NBT>> CompoundNBT writeTwoDimArrToNBT(T[][] objArr) {
        return writeTwoDimArrToNBT(objArr, (Predicate<T>) e -> true);
    }

    /**
     * Writes two-dimensional array of any object to NBT.
     *
     * @param objArr         two-dimensional array of data to save.
     * @param <T>            must implement {@link INBTSerializable}!
     * @param writeElementIf controls whether element will be written or not. If element will be rejected by your {@link Predicate}, then
     *                       the element of massive will be null after reading this NBT Compound.
     * @return {@link CompoundNBT} tag that contains this two-dimensional array.
     */
    public static <NBT extends INBT, T extends INBTSerializable<NBT>> CompoundNBT writeTwoDimArrToNBT(T[][] objArr, Predicate<T> writeElementIf) {
        CompoundNBT tableTag = new CompoundNBT();

        for (int i = 0; i < objArr.length; i++) {
            CompoundNBT column = new CompoundNBT();

            for (int j = 0; j < objArr[i].length; j++) {
                if (writeElementIf.test(objArr[i][j])) {
                    NBT elementTag = objArr[i][j].serializeNBT();
                    column.put(Integer.toString(j), elementTag);
                }
            }

            column.putInt("size", objArr[i].length);

            tableTag.put(Integer.toString(i), column);
        }

        tableTag.putInt("size", objArr.length);

        return tableTag;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] readTwoDimArrFromNBT(CompoundNBT tableTag, Class<T> elementClass, Function<CompoundNBT, T> deserializer) {
        int size = tableTag.getInt("size");

        T[][] table = null;

        for (int i = 0; i < size; i++) {
            CompoundNBT columnTag = tableTag.getCompound(Integer.toString(i));
            int columnSize = columnTag.getInt("size");

            T[] column = (T[]) Array.newInstance(elementClass, columnSize);

            for (int j = 0; j < columnSize; j++) {
                if (columnTag.contains(Integer.toString(j))) {
                    CompoundNBT elementTag = (CompoundNBT) columnTag.get(Integer.toString(j));

                    column[j] = deserializer.apply(elementTag);
                }
            }

            if (i == 0) {
                Class<?> columnClass = column.getClass();
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
     * @return {@link CompoundNBT} tag that contains this two-dimensional array.
     */
    public static <T> CompoundNBT writeTwoDimArrToNBT(T[][] objArr, Function<T, CompoundNBT> serializer) {
        return writeTwoDimArrToNBT(objArr, serializer, e -> true);
    }

    /**
     * Writes two-dimensional array of any object to NBT.
     *
     * @param objArr         two-dimensional array of data to save.
     * @param serializer     realization of "{@code <T>} to NBTTagCompound" serialization.
     * @param writeElementIf controls whether element will be written or not. If element will be rejected by your {@link Predicate}, then
     *                       the element of massive will be null after reading this NBT Compound.
     * @return {@link CompoundNBT} tag that contains this two-dimensional array.
     */
    public static <T> CompoundNBT writeTwoDimArrToNBT(T[][] objArr, Function<T, CompoundNBT> serializer, Predicate<T> writeElementIf) {
        CompoundNBT tableTag = new CompoundNBT();

        for (int i = 0; i < objArr.length; i++) {
            CompoundNBT column = new CompoundNBT();

            for (int j = 0; j < objArr[i].length; j++) {
                if (writeElementIf.test(objArr[i][j])) {
                    CompoundNBT elementTag = serializer.apply(objArr[i][j]);
                    column.put(Integer.toString(j), elementTag);
                }
            }

            column.putInt("size", objArr[i].length);

            tableTag.put(Integer.toString(i), column);
        }

        tableTag.putInt("size", objArr.length);

        return tableTag;
    }
}
