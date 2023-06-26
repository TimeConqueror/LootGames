package ru.timeconqueror.lootgames.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;

public class RenderBuffer {

    private final RenderType renderType;
    private final BufferBuilder bufferBuilder;

    public RenderBuffer(RenderType renderType, BufferBuilder bufferBuilder) {
        this.renderType = renderType;
        this.bufferBuilder = bufferBuilder;
    }

    public BufferBuilder getBuffer() {
        return bufferBuilder;
    }

    public RenderType getType() {
        return renderType;
    }

    public void draw() {
        renderType.end(bufferBuilder, RenderSystem.getVertexSorting());
    }

    public static RenderBuffer of(RenderType renderType) {
        return new RenderBuffer(renderType, new BufferBuilder(renderType.bufferSize()));
    }
}