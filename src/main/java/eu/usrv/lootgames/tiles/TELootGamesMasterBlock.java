package eu.usrv.lootgames.tiles;


import eu.usrv.lootgames.ILootGame;
import eu.usrv.lootgames.LootGames;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import static eu.usrv.lootgames.StructureGenerator.*;


public class TELootGamesMasterBlock extends TileEntity {
    private boolean _mIsActive = false;
    private long _mLastSoundTick = 0L;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            if (LootGames.Rnd.nextInt(100) <= 10) {
                if (_mLastSoundTick < System.currentTimeMillis()) {
                    _mLastSoundTick = System.currentTimeMillis() + (LootGames.Rnd.nextInt(90) + 30) * 1000;
                    worldObj.playSoundEffect(xCoord, yCoord, zCoord, String.format("%s:%s", LootGames.MODID, "masterblock_strange"), 0.5F, 1.0F);
                }
            }
        }
    }

    public void onBlockClickedByPlayer(Object object, EntityPlayer pPlayer) {
        try {
            if (!LootGames.ModConfig.MinigamesEnabled) {
                PlayerChatHelper.SendNotifyWarning(pPlayer, "This structure seems to be inactive...");
                return;
            }

            if (!_mIsActive) {
                _mIsActive = true;

                ILootGame tGame = LootGames.GameMgr.selectRandomGame();

                boolean tMasterPosHasBeenReplaced = false;

                for (int axisXoffset = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisXoffset <= PUZZLEROOM_CENTER_TO_BORDER; axisXoffset++) {
                    for (int axisZoffset = (PUZZLEROOM_CENTER_TO_BORDER * -1); axisZoffset <= PUZZLEROOM_CENTER_TO_BORDER; axisZoffset++) {
                        for (int axisY = yCoord - PUZZLEROOM_MASTER_TE_OFFSET; axisY <= yCoord - PUZZLEROOM_MASTER_TE_OFFSET + PUZZLEROOM_HEIGHT; axisY++) {
                            boolean tState = tGame.onGenerateBlock(worldObj, PUZZLEROOM_CENTER_TO_BORDER, PUZZLEROOM_HEIGHT, xCoord, yCoord - PUZZLEROOM_MASTER_TE_OFFSET, zCoord, axisXoffset, axisY, axisZoffset);

                            // Catch if *this* TE is replaced by the gamegen already
                            if (axisXoffset == 0 && axisY == yCoord && axisZoffset == 0)
                                tMasterPosHasBeenReplaced = tState;
                        }
                    }
                }

                // Destroy this TE if it hasn't been replaced by the game-gen
                if (!tMasterPosHasBeenReplaced)
                    worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            }
        } catch (Exception ex) {
            LootGames.mLog.error(ex.toString());
        }
    }
}