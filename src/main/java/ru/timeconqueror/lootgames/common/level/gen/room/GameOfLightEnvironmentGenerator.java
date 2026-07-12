package ru.timeconqueror.lootgames.common.level.gen.room;

import lombok.SneakyThrows;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import ru.timeconqueror.lootgames.api.minigame.BoardGameEnvironmentGenerator;
import ru.timeconqueror.lootgames.api.minigame.FlatBoardGame;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.room.Room;
import ru.timeconqueror.lootgames.api.room.RoomCoords;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.util.ResourceUtils;
import ru.timeconqueror.timecore.structureio.ExtendedStructureTemplate;
import ru.timeconqueror.timecore.structureio.StructureIO;

import java.io.InputStream;

public class GameOfLightEnvironmentGenerator extends BoardGameEnvironmentGenerator {
    private final ExtendedStructureTemplate template;

    @SneakyThrows
    public GameOfLightEnvironmentGenerator() {
        super(() -> LGBlocks.BOARD_GLASSY_MATTER.defaultBlockState());
        InputStream fileStream = ResourceUtils.getStream("data/lootgames/structures/game_of_light_temple.dat");
        template = StructureIO.INSTANCE.loadTemplate(fileStream).orElseThrow();
    }

    @Override
    public void generate(ServerLevel level, Room room, LootGame<?> game) {
        super.generate(level, room, game);

        RoomCoords coords = room.getCoords();
        BlockPos centerPos = coords.centerPos(FlatBoardGame.DEFAULT_FLOOR_POS);
        level.setBlock(centerPos, Blocks.YELLOW_WOOL.defaultBlockState(), 3);

//        Vec3i templateSize = template.getTemplate().getSize();
//        BlockPos genStart = centerPos.offset(-templateSize.getX() / 2, 0, -templateSize.getZ() / 2);
//        StructurePlaceSettings placeSettings = new StructurePlaceSettings();
//        StructureIO.INSTANCE.generate(template, level, genStart, placeSettings);
    }
}
