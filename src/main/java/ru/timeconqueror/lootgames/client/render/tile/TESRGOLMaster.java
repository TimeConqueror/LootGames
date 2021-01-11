package ru.timeconqueror.lootgames.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.client.model.animation.Animation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.tile.TileBoardGameMaster;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;
import ru.timeconqueror.lootgames.client.render.LGRenderTypes;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityGOLMaster;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.timecore.animation.Ease;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.client.DrawHelper;

import static ru.timeconqueror.lootgames.minigame.gol.GameOfLight.StageUnderExpanding.MAX_TICKS_EXPANDING;

public class TESRGOLMaster extends TileEntityRenderer<TileEntityGOLMaster> {
    private static final RenderType RT_BOARD = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board.png"));
    private static final RenderType RT_BOARD_ACTIVE = LGRenderTypes.brightened(LootGames.rl("textures/game/gol_board_active.png"));

    public TESRGOLMaster(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEntityGOLMaster tileEntityIn, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrix.pushPose();
        TileBoardGameMaster.prepareMatrix(matrix, tileEntityIn);

        GameOfLight game = tileEntityIn.getGame();
        BoardLootGame.BoardStage stage = game.getStage();
        int ticks = stage instanceof GameOfLight.StageUnderExpanding ? ((GameOfLight.StageUnderExpanding) stage).getTicks() : 0;

        drawBoard(game, matrix, bufferIn, ticks, partialTicks);

        matrix.popPose();
    }

    public void drawBoard(GameOfLight game, MatrixStack matrix, IRenderTypeBuffer bufferIn, int ticksIn, float partialTicks) {
        IVertexBuilder buffer = bufferIn.getBuffer(RT_BOARD);

        boolean isExpanding = false;

        if (game.getStage() instanceof GameOfLight.StageUnderExpanding) {
            isExpanding = true;
        }

        float ticks = Animation.getWorldTime(game.getWorld(), partialTicks) * 14 % MAX_TICKS_EXPANDING;//FIXME move to ticksIn

        float progress = Ease.inOutQuart(MathUtils.coerceInRange(ticks, 0, MAX_TICKS_EXPANDING) / MAX_TICKS_EXPANDING);
        float length = MathUtils.interpolate(progress, 1, 3);

        matrix.translate(3 / 2F, 3 / 2F, 0);

        float textureStart = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 0F : 16F - 16F * progress;
        float textureLength = !isExpanding || ticks >= MAX_TICKS_EXPANDING ? 48F : 32F + 16F * progress - textureStart;

        DrawHelper.drawTexturedRectByParts(buffer, matrix, -length / 2, -length / 2, length, length, -0.005F, textureStart, textureStart, textureLength, textureLength, 48);
    }
}
