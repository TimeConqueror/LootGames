package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.timeconqueror.lootgames.api.minigame.Leadings2D.HORIZONTAL_DEFAULT;
import static ru.timeconqueror.lootgames.api.minigame.Leadings2D.VERTICAL_UP_TO_DOWN;

class Leadings2DTest {
    @Test
    void origin_withSize2_shouldCalculateCorrectPositionHorizontal() {
        BlockPos center = new BlockPos(2, 0, 2);
        int size = 4;

        BlockPos result = HORIZONTAL_DEFAULT.origin(center, size);
        assertEquals(new BlockPos(0, 0, 0), result);
    }

    @Test
    void origin_withSize2_shouldCalculateCorrectPositionVertical() {
        BlockPos center = new BlockPos(2, 2, 0);
        int size = 4;

        BlockPos result = VERTICAL_UP_TO_DOWN.origin(center, size);
        assertEquals(new BlockPos(0, 4, 0), result);
    }

    @Test
    void toFlatPos_HorizontalDefault_HorizontalLeadings() {
        Leadings2D leadings = Leadings2D.HORIZONTAL_DEFAULT; // x+ -> x+, z+ -> y+
        BlockPos origin = new BlockPos(1, 1, 1);

        Vector2ic pos = leadings.toFlatPos(origin, new BlockPos(10, 8, 6));
        assertEquals(9, pos.x());
        assertEquals(5, pos.y());

        pos = leadings.toFlatPos(origin, new BlockPos(-10, -8, -6));
        assertEquals(-11, pos.x());
        assertEquals(-7, pos.y());
    }

    @Test
    void toFlatPos_HorizontalDefault_MinusXMinusYLeadings() {
        Leadings2D leadings = new Leadings2D(Direction.WEST, Direction.DOWN); // x- -> x+, y- -> y+
        BlockPos origin = new BlockPos(1, 1, 1);

        Vector2ic pos = leadings.toFlatPos(origin, new BlockPos(10, 8, 6));
        assertEquals(-9, pos.x());
        assertEquals(-7, pos.y());

        pos = leadings.toFlatPos(origin, new BlockPos(-10, -8, -6));
        assertEquals(11, pos.x());
        assertEquals(9, pos.y());
    }

    @Test
    void fromFlatPos_HorizontalDefault_HorizontalLeadings() {
        Leadings2D leadings = Leadings2D.HORIZONTAL_DEFAULT; // x+ -> x+, z+ -> y+
        BlockPos origin = new BlockPos(1, 1, 1);

        BlockPos pos3d = new BlockPos(10, 8, 6);
        Vector2ic flatPos = leadings.toFlatPos(origin, pos3d);
        BlockPos restoredPos3d = leadings.fromFlatPos(origin, flatPos);
        assertEquals(pos3d.getX(), restoredPos3d.getX());
        assertEquals(pos3d.getZ(), restoredPos3d.getZ());

        pos3d = new BlockPos(-10, -8, -6);
        flatPos = leadings.toFlatPos(origin, pos3d);
        restoredPos3d = leadings.fromFlatPos(origin, flatPos);
        assertEquals(pos3d.getX(), restoredPos3d.getX());
        assertEquals(pos3d.getZ(), restoredPos3d.getZ());
    }

    @Test
    void fromFlatPos_HorizontalDefault_MinusXMinusZLeadings() {
        Leadings2D leadings = new Leadings2D(Direction.WEST, Direction.DOWN); // x- -> x+, y- -> y+
        BlockPos origin = new BlockPos(1, 1, 1);

        BlockPos pos3d = new BlockPos(10, 8, 6);
        Vector2ic flatPos = leadings.toFlatPos(origin, pos3d);
        BlockPos restoredPos3d = leadings.fromFlatPos(origin, flatPos);
        assertEquals(pos3d.getX(), restoredPos3d.getX());
        assertEquals(pos3d.getY(), restoredPos3d.getY());

        pos3d = new BlockPos(-10, -8, -6);
        flatPos = leadings.toFlatPos(origin, pos3d);
        restoredPos3d = leadings.fromFlatPos(origin, flatPos);
        assertEquals(pos3d.getX(), restoredPos3d.getX());
        assertEquals(pos3d.getY(), restoredPos3d.getY());
    }

    @Test
    void fromFlatPos_HorizontalDefault_VerticalLeadings() {
        Leadings2D leadings = VERTICAL_UP_TO_DOWN; // x+ -> x+, y- -> y+
        BlockPos origin = new BlockPos(63, 67, 64);

        BlockPos restoredPos3d = leadings.fromFlatPos(origin, new Vector2i(1, 0));
        assertEquals(64, restoredPos3d.getX());
        assertEquals(67, restoredPos3d.getY());
    }
}