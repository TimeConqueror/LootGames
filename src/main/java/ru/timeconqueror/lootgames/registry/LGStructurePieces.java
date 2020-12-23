package ru.timeconqueror.lootgames.registry;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonPieces;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.Promised;
import ru.timeconqueror.timecore.registry.newreg.SimpleVanillaRegister;

public class LGStructurePieces {
    @AutoRegistrable
    private static final SimpleVanillaRegister<IStructurePieceType> REGISTER = new SimpleVanillaRegister<>(LootGames.MODID, Registry.STRUCTURE_PIECE);

    public static final Promised<IStructurePieceType> GAME_DUNGEON_PIECE = REGISTER.register("gd/p", () -> GameDungeonPieces.Piece::new);
    public static final Promised<IStructurePieceType> GAME_DUNGEON_ENTRY_PATH = REGISTER.register("gd/ep", () -> GameDungeonPieces.EntryPath::new);
}
