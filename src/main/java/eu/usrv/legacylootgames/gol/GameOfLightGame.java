package eu.usrv.legacylootgames.gol;


import cpw.mods.fml.common.registry.GameRegistry;
import eu.usrv.legacylootgames.ILootGame;
import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.legacylootgames.gol.blocks.BlockLightGameBlock;
import eu.usrv.legacylootgames.gol.tiles.TELightGameBlock;
import net.minecraft.world.World;


public class GameOfLightGame implements ILootGame {
    public static BlockLightGameBlock GameBlock;

    @Override
    public void init() {
        GameBlock = new BlockLightGameBlock();
        GameRegistry.registerTileEntity(TELightGameBlock.class, "LOOTGAMES_GOL_TE");
        GameRegistry.registerBlock(GameBlock, "GOLMasterBlock");
    }

    @Override
    public boolean onGenerateBlock(World pWorldObject, int pMaxXZ, int pMaxY, int pCenterX, int pBottom, int pCenterZ, int pOffsetX, int pOffsetY, int pOffsetZ) {
        boolean tPlacedBlock = false;

        LootGamesLegacy.DungeonLogger.trace("GameOfLightGame => onGenerateBlock()");
        if (pOffsetY == (pBottom + 1)) {
            if ((pOffsetX >= -2 && pOffsetX <= 2) && (pOffsetZ >= -2 && pOffsetZ <= 2)) {
                if (pOffsetX == 0 && pOffsetZ == 0) {
                    pWorldObject.setBlock(pOffsetX + pCenterX, pOffsetY, pOffsetZ + pCenterZ, GameBlock);
                    tPlacedBlock = true;
                }
/*        else if( ( pOffsetX == 0 && ( pOffsetZ == -2 || pOffsetZ == +2 ) ) || ( pOffsetZ == 0 && ( pOffsetX == -2 || pOffsetX == +2 ) ) )
        {
          pWorldObject.setBlock( pOffsetX + pCenterX, pOffsetY, pOffsetZ + pCenterZ, LootGames.DungeonLightBlock );
          tPlacedBlock = true;
        }
        else if( ( pOffsetX == 0 && ( pOffsetZ == -1 || pOffsetZ == +1 ) ) || ( pOffsetZ == 0 && ( pOffsetX == -1 || pOffsetX == +1 ) ) )
          pWorldObject.setBlock( pOffsetX + pCenterX, pOffsetY, pOffsetZ + pCenterZ, Blocks.wool );
        else
          pWorldObject.setBlock( pOffsetX + pCenterX, pOffsetY, pOffsetZ + pCenterZ, Blocks.quartz_block );*/
            }
        }

        return tPlacedBlock;
    }

}
