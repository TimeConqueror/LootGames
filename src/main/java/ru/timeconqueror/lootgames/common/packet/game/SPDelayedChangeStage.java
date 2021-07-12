package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.util.Objects;

public class SPDelayedChangeStage extends SPChangeStage {
    @Nullable
    private String prevStageId;

    public SPDelayedChangeStage() {
    }

    public SPDelayedChangeStage(LootGame<?, ?> game, @Nullable LootGame.Stage stage) {
        super(game);
        this.prevStageId = stage != null ? stage.getID() : null;
    }

    @Override
    public void encode(PacketBuffer bufferTo) {
        super.encode(bufferTo);
        bufferTo.writeBoolean(prevStageId != null);
        if (prevStageId != null) {
            bufferTo.writeUtf(prevStageId);
        }
    }

    @Override
    public void decode(PacketBuffer bufferFrom) {
        super.decode(bufferFrom);
        if (bufferFrom.readBoolean()) {
            prevStageId = bufferFrom.readUtf();
        }
    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        String currentStageId = game.getStage() != null ? game.getStage().getID() : null;
        if (Objects.equals(currentStageId, this.prevStageId)) {
            super.runOnClient(game);
        }
    }
}
