package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.packet.GamePacketRegister;
import ru.timeconqueror.lootgames.common.packet.game.*;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGGamePackets {
    @AutoRegistrable
    private static final GamePacketRegister REGISTER = new GamePacketRegister(LootGames.MODID);

    @AutoRegistrable.InitMethod
    private static void register() {
        REGISTER.regServerPacket(SPChangeStage.class);
        REGISTER.regServerPacket(SPDelayedChangeStage.class);

        REGISTER.regServerPacket(SPPipesGameChangeChunks.class);
        REGISTER.regServerPacket(SPPipesGameSetBoard.class);

        REGISTER.regServerPacket(SPMSFieldChanged.class);
        REGISTER.regServerPacket(SPMSGenBoard.class);
        REGISTER.regServerPacket(SPMSResetFlags.class);
        REGISTER.regServerPacket(SPMSSpawnLevelBeatParticles.class);

        REGISTER.regServerPacket(SPGOLSendDisplayedSymbol.class);
        REGISTER.regClientPacket(CPGOLSymbolsShown.class);
        REGISTER.regServerPacket(SPGOLDrawMark.class);
        REGISTER.regServerPacket(SPGOLSpawnStageUpParticles.class);
    }
}
