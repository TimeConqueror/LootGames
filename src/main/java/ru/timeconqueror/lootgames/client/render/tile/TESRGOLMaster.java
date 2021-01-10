package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityGOLMaster;

public class TESRGOLMaster extends TileEntityRenderer<TileEntityGOLMaster> {
    private static final RenderType RT_BOARD = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board.png"));
    private static final RenderType RT_BOARD_ACTIVE = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board_active.png"));

    public TESRGOLMaster(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityGOLMaster tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }
}
