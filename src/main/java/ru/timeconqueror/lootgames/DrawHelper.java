package ru.timeconqueror.lootgames;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DrawHelper {
    public static class DrawSettings {
        private float tileLengthX = 1;
        private float tileLengthY = 1;
        private float tileCountX = 1;
        private float tileCountY = 1;

        public DrawSettings tileCount(int tileCount) {
            this.tileCountX = tileCount;
            this.tileCountY = tileCount;
            return this;
        }

        public DrawSettings tileCountX(int tileCount) {
            this.tileCountX = tileCount;
            return this;
        }

        public DrawSettings tileCountY(int tileCount) {
            this.tileCountY = tileCount;
            return this;
        }

        public DrawSettings tileLength(int length) {
            this.tileLengthX = length;
            this.tileLengthY = length;
            return this;
        }

        public DrawSettings tileLengthX(int length) {
            this.tileLengthX = length;
            return this;
        }

        public DrawSettings tileLengthY(int length) {
            this.tileLengthY = length;
            return this;
        }
    }

    public static void drawTile(PoseStack.Pose poseStack, DrawSettings settings, int tileIndexX, int tileIndexY, VertexBuilder builder) {
        Matrix4f pose = poseStack.pose();
        Matrix3f normal = poseStack.normal();

        float x = tileIndexX / settings.tileCountX * settings.tileLengthX;
        float y = tileIndexY / settings.tileCountY * settings.tileLengthY;

        float u1 = tileIndexX / settings.tileCountX;
        float v1 = tileIndexY / settings.tileCountY;
        float u2 = u1 + 1 / settings.tileCountX;
        float v2 = v1 + 1 / settings.tileCountY;
        builder.apply(pose, normal, x, y, u1, v1, u2, v2);
    }
}
