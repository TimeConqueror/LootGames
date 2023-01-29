package ru.timeconqueror.lootgames.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonPieces;
import ru.timeconqueror.timecore.api.registry.SimpleVanillaRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.Promised;

public class LGStructurePieces {
    @AutoRegistrable
    private static final SimpleVanillaRegister<StructurePieceType> REGISTER = new SimpleVanillaRegister<>(LootGames.MODID, Registry.STRUCTURE_PIECE);

    public static final Promised<StructurePieceType> GAME_DUNGEON_PIECE = REGISTER.register("gd/p", () -> GameDungeonPieces.Piece::new);
    public static final Promised<StructurePieceType> GAME_DUNGEON_ENTRY_PATH = REGISTER.register("gd/ep", () -> GameDungeonPieces.EntryPath::new);
}
