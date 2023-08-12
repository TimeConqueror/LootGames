package ru.timeconqueror.lootgames.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class VertexSink {

    private static final Vector3f TEMP_VEC3 = new Vector3f();
    private static final Vector4f TEMP_VEC4 = new Vector4f();

    private final VertexConsumer consumer;

    private int light = LightTexture.FULL_BRIGHT;

    public VertexSink(VertexConsumer consumer) {
        this.consumer = consumer;
    }

    public VertexSink vertex(float x, float y, float z) {
        consumer.vertex(x, y, z);
        return this;
    }

    public VertexSink vertex(Matrix4f transform, float x, float y, float z) {
        var pos = TEMP_VEC4.set(x, y, z, 1).mul(transform);
        return vertex(pos.x, pos.y, pos.z);
    }

    public VertexSink vertex(Matrix4f transform, float x, float y, float z, float scale) {
        return vertex(transform, x * scale, y * scale, z * scale);
    }

    public VertexSink vertex(Vector3f vec) {
        return vertex(vec.x, vec.y, vec.z);
    }

    public VertexSink vertex(Matrix4f transform, Vector3f vec) {
        return vertex(transform, vec.x, vec.y, vec.z);
    }

    public VertexSink vertex(Matrix4f transform, Vector3f vec, float scale) {
        return vertex(transform, vec.x * scale, vec.y * scale, vec.z * scale);
    }

    public VertexSink vertex(Matrix4f transform, Vector3f pos, Vector3f delta, float scale) {
        return vertex(transform, pos.x + delta.x * scale, pos.y + delta.y * scale, pos.z + delta.z * scale);
    }

    public VertexSink uv(float u, float v) {
        consumer.uv(u, v);
        return this;
    }

    public VertexSink color(float r, float g, float b, float a) {
        consumer.color(r, g, b, a);
        return this;
    }

    public VertexSink colorI(int r, int g, int b, int a) {
        consumer.color(r, g, b, a);
        return this;
    }

    public VertexSink colorAlphaI(int alpha) {
        return colorI(255, 255, 255, alpha);
    }

    public VertexSink color(Vector4f color) {
        return color(color.x, color.y, color.z, color.w);
    }

    public VertexSink color(float r, float g, float b) {
        return color(r, g, b, 1);
    }

    public VertexSink light(int light) {
        this.light = light;
        return this;
    }

    public VertexSink normal(float x, float y, float z) {
        consumer.normal(x, y, z);
        return this;
    }

    public void endVertex(Matrix3f transform, Vector3f normal) {
        endVertex(TEMP_VEC3.set(normal).mul(transform));
    }

    public void endVertex(Matrix3f transform, float x, float y, float z) {
        endVertex(TEMP_VEC3.set(x, y, z).mul(transform));
    }

    public void endVertex(Vector3f normal) {
        consumer
                //overlay
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                //light
                .uv2(light)
                .normal(normal.x, normal.y, normal.z)
                .endVertex();

        light = LightTexture.FULL_BRIGHT;
    }
}