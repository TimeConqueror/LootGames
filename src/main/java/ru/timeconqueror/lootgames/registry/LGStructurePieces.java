package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonPieces;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.newreg.StructurePieceRegister;

public class LGStructurePieces {
    @AutoRegistrable
    private static final StructurePieceRegister REGISTER = new StructurePieceRegister(LootGames.MODID);

    public static final IStructurePieceType GAME_DUNGEON_PIECE = REGISTER.register("gd/p", GameDungeonPieces.Piece::new);
}
