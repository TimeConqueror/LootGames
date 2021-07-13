
package eu.usrv.legacylootgames.gol.tiles;


import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.legacylootgames.StructureGenerator;
import eu.usrv.legacylootgames.achievements.LootGameAchievement;
import eu.usrv.legacylootgames.auxiliary.ExtendedDirections;
import eu.usrv.legacylootgames.blocks.DungeonLightSource;
import eu.usrv.legacylootgames.config.LootGamesConfig.LootStageConfig;
import eu.usrv.legacylootgames.gol.GameOfLightGame;
import eu.usrv.legacylootgames.network.NetDispatcher;
import eu.usrv.legacylootgames.network.msg.SpawnParticleFXMessage;
import eu.usrv.yamcore.auxiliary.ItemDescriptor;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.event.world.NoteBlockEvent.Note;
import net.minecraftforge.event.world.NoteBlockEvent.Octave;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;

import java.util.*;


public class TELightGameBlock extends TileEntity {
    private static final String NBTTAG_ISACTIVE = "mIsActive";
    private static final String NBTTAG_ISMASTER = "mIsMaster";
    private static final String NBTTAG_DIRECTION = "mTEDirection";
    private static final String NBTTAG_BLOCK_NORTH = "mGameBlockNorth";
    private static final String NBTTAG_BLOCK_SOUTH = "mGameBlockSouth";
    private static final String NBTTAG_BLOCK_WEST = "mGameBlockEast";
    private static final String NBTTAG_BLOCK_EAST = "mGameBlockWest";
    private static final String NBTTAG_BLOCK_NW = "mGameBlockNW";
    private static final String NBTTAG_BLOCK_NE = "mGameBlockNE";
    private static final String NBTTAG_BLOCK_SW = "mGameBlockSW";
    private static final String NBTTAG_BLOCK_SE = "mGameBlockSE";
    private static final String NBTTAG_BLOCK_MASTER = "mMasterBlock";
    private static final String NBTTAG_BLOCKPOS_X = "posX";
    private static final String NBTTAG_BLOCKPOS_Y = "posY";
    private static final String NBTTAG_BLOCKPOS_Z = "posZ";
    private Vec3 mNorthPos;
    private Vec3 mSouthPos;
    private Vec3 mWestPos;
    private Vec3 mEastPos;
    private Vec3 mNorthWestPos;
    private Vec3 mNorthEastPos;
    private Vec3 mSouthWestPos;
    private Vec3 mSouthEastPos;
    private Vec3 mMasterPos;
    private HashMap<ExtendedDirections, Long> _mActiveBlocks = new HashMap<>();
    private TELightGameBlock _mGameBlockNorth;
    private TELightGameBlock _mGameBlockWest;
    private TELightGameBlock _mGameBlockSouth;
    private TELightGameBlock _mGameBlockEast;
    private TELightGameBlock _mGameBlockNE;
    private TELightGameBlock _mGameBlockNW;
    private TELightGameBlock _mGameBlockSE;
    private TELightGameBlock _mGameBlockSW;
    private TELightGameBlock _mMasterTE;
    private ExtendedDirections mTEDirection = ExtendedDirections.UP;
    private boolean mIsMaster;
    private boolean mIsActive;
    private int mFailedAttempts;
    private int mCurrentLevel = 1;
    private int mMaxLevelReached = 1;
    // List<LinkedTE> mLinkedTEs = new ArrayList<LinkedTE>();
    private ArrayList<Integer> mCurrentGameSequence = new ArrayList<>();
    private ArrayList<Integer> mCurrentEnteredSequence = new ArrayList<>();
    private UUID mCurrentPlayer = null;
    private long mLastGameClickTime = -1;
    private long mLastGameReplayTime = -1;
    private int mLastPlayedDigitIndex = -1;
    private eGameStage mGameStage = eGameStage.UNDEPLOYED;
    private int mNumCheatAttemptsDetected = 0;

    private static <T> String joinList(List<T> pSourceList, String pDelimiter) {
        String tRet = "";

        boolean tIsFirstRun = true;
        for (T element : pSourceList) {
            if (tIsFirstRun)
                tIsFirstRun = false;
            else
                tRet += String.format("%s ", pDelimiter);

            tRet += element.toString();
        }

        return tRet;
    }

    public boolean getIsActive() {
        return mIsActive;
    }

    public ExtendedDirections getDirection() {
        return mTEDirection;
    }

    private TELightGameBlock getBlockNorth() {
        if (_mGameBlockNorth == null && mNorthPos != null)
            _mGameBlockNorth = (TELightGameBlock) worldObj.getTileEntity((int) mNorthPos.xCoord, (int) mNorthPos.yCoord, (int) mNorthPos.zCoord);

        return _mGameBlockNorth;
    }

    private TELightGameBlock getBlockWest() {
        if (_mGameBlockWest == null && mWestPos != null)
            _mGameBlockWest = (TELightGameBlock) worldObj.getTileEntity((int) mWestPos.xCoord, (int) mWestPos.yCoord, (int) mWestPos.zCoord);

        return _mGameBlockWest;
    }

    private TELightGameBlock getBlockSouth() {
        if (_mGameBlockSouth == null && mSouthPos != null)
            _mGameBlockSouth = (TELightGameBlock) worldObj.getTileEntity((int) mSouthPos.xCoord, (int) mSouthPos.yCoord, (int) mSouthPos.zCoord);

        return _mGameBlockSouth;
    }

    private TELightGameBlock getBlockEast() {
        if (_mGameBlockEast == null && mEastPos != null)
            _mGameBlockEast = (TELightGameBlock) worldObj.getTileEntity((int) mEastPos.xCoord, (int) mEastPos.yCoord, (int) mEastPos.zCoord);

        return _mGameBlockEast;
    }

    private TELightGameBlock getBlockMaster() {
        if (_mMasterTE == null && mMasterPos != null)
            _mMasterTE = (TELightGameBlock) worldObj.getTileEntity((int) mMasterPos.xCoord, (int) mMasterPos.yCoord, (int) mMasterPos.zCoord);

        return _mMasterTE;
    }

    private TELightGameBlock getBlockNW() {
        if (_mGameBlockNW == null && mNorthWestPos != null)
            _mGameBlockNW = (TELightGameBlock) worldObj.getTileEntity((int) mNorthWestPos.xCoord, (int) mNorthWestPos.yCoord, (int) mNorthWestPos.zCoord);

        return _mGameBlockNW;
    }

    private TELightGameBlock getBlockNE() {
        if (_mGameBlockNE == null && mNorthEastPos != null)
            _mGameBlockNE = (TELightGameBlock) worldObj.getTileEntity((int) mNorthEastPos.xCoord, (int) mNorthEastPos.yCoord, (int) mNorthEastPos.zCoord);

        return _mGameBlockNE;
    }

    private TELightGameBlock getBlockSW() {
        if (_mGameBlockSW == null && mSouthWestPos != null)
            _mGameBlockSW = (TELightGameBlock) worldObj.getTileEntity((int) mSouthWestPos.xCoord, (int) mSouthWestPos.yCoord, (int) mSouthWestPos.zCoord);

        return _mGameBlockSW;
    }

    private TELightGameBlock getBlockSE() {
        if (_mGameBlockSE == null && mSouthEastPos != null)
            _mGameBlockSE = (TELightGameBlock) worldObj.getTileEntity((int) mSouthEastPos.xCoord, (int) mSouthEastPos.yCoord, (int) mSouthEastPos.zCoord);

        return _mGameBlockSE;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    public void toggleMusicMode(EntityPlayer pPlayer) {
        if (mGameStage == eGameStage.PLAYMUSIC) {
            PlayerChatHelper.SendNotifyNormal(pPlayer, "The Structure has woken up");
            mGameStage = eGameStage.SLEEP;
        } else {
            PlayerChatHelper.SendNotifyNormal(pPlayer, "The Structure has fallen asleep");
            mGameStage = eGameStage.PLAYMUSIC;
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }

    public void onBlockClickedByPlayer(ExtendedDirections pDirection, EntityPlayer pPlayer) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info("TE clicked");

        if (getBlockEast() == null && getBlockNorth() == null && getBlockSouth() == null && getBlockWest() == null && !mIsMaster && getBlockMaster() == null && mGameStage == eGameStage.UNDEPLOYED) {
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info("No master found, using *me*");
            _mMasterTE = this;
            mIsMaster = true;
        }

        if (mIsMaster) {
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info(String.format("...master GameStage is %s", mGameStage.toString()));

            if (!checkAirSpaceAboveBlock()) {
                HandleCheatDetected(pPlayer);
            }

            switch (mGameStage) {
                case UNDEPLOYED:
                    checkOrDeployStructure();
                    resetGame(pPlayer, true);
                    break;
                case SLEEP:
                    resetGame(pPlayer, true);
                    break;
                case ACTIVE_PLAY_SEQUENCE:
                    PlayerChatHelper.SendNotifyNormal(pPlayer, "You feel that the structure is not yet ready...");
                    break;
                case PLAYMUSIC:
                    if (pDirection != null) {
                        sendFeedbackSoundNote(pDirection);
                        _mActiveBlocks.put(pDirection, System.currentTimeMillis());
                        toggleGameBlockActiveState(pDirection, true);
                    }
                    break;
                case ACTIVE_WAIT_FOR_PLAYER:
                    if (pDirection != null) {
                        mLastGameClickTime = System.currentTimeMillis();
                        handleBlockInput(pDirection, pPlayer);
                    } else
                        PlayerChatHelper.SendNotifyNormal(pPlayer, "You should replay the colors now...");
                    break;
                case PENDING_GAME_START:
                    PlayerChatHelper.SendNotifyNormal(pPlayer, "You feel that the structure is not yet ready...");

                    // ignore player input while waiting for slave blocks to send event
            }
        } else {
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info("...slave. Forwarding to master");
            getBlockMaster().onBlockClickedByPlayer(mTEDirection, pPlayer);
        }
    }

    private void HandleCheatDetected(EntityPlayer pPlayer) {
        mNumCheatAttemptsDetected++;
        clearPlayFieldAboveBlock();

        if (mNumCheatAttemptsDetected == 1)
            PlayerChatHelper.SendWarn(pPlayer, "Do not obstruct the playfield!");
        if (mNumCheatAttemptsDetected == 2)
            PlayerChatHelper.SendWarn(pPlayer, "Last warning: Do-not-obstruct-the-playfield!");
        if (mNumCheatAttemptsDetected > 2) {
            PlayerChatHelper.SendError(pPlayer, "Cheater!");
            mGameStage = eGameStage.SLEEP;
            mNumCheatAttemptsDetected = 0;

            LootGamesLegacy.CheatHandler.doRandomCheaterEvent(pPlayer);
        }
    }

    private void clearPlayFieldAboveBlock() {
        for (int offsetX = -3; offsetX <= 3; offsetX++)
            for (int offsetZ = -3; offsetZ <= 3; offsetZ++)
                for (int offsetY = 1; offsetY <= 4; offsetY++) {
                    worldObj.setBlockToAir(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ);
                }
    }

    // Scan the air-space around the master-TE for any block that is not AIR, in order to prevent cheating
    private boolean checkAirSpaceAboveBlock() {
        boolean tState = true;

        for (int offsetX = -3; offsetX <= 3 && tState; offsetX++)
            for (int offsetZ = -3; offsetZ <= 3 && tState; offsetZ++)
                for (int offsetY = 1; offsetY <= 4 && tState; offsetY++)
                    if (worldObj.getBlock(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ).getMaterial() != Material.air)
                        tState = false; // Possible cheat detected

        return tState;
    }

    private void handleBlockInput(ExtendedDirections pDirection, EntityPlayer pPlayer) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("Received input for direction %s", pDirection.toString()));

        // Ignore any input from other players
        if (!(mCurrentPlayer.equals(pPlayer.getUniqueID()))) {
            PlayerChatHelper.SendNotifyNormal(pPlayer, "The structure doesn't seem to recognize you");
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info(String.format("Wrong playerID. ignoring %s", mGameStage.toString()));
            return;
        }

        // Send player feedback
        // sendFeedbackSound( eSoundType.SND_PLAYER_CLICK );
        sendFeedbackSoundNote(pDirection);
        _mActiveBlocks.put(pDirection, System.currentTimeMillis());
        toggleGameBlockActiveState(pDirection, true);

        // Is clicked block the correct one?
        if (mCurrentEnteredSequence.size() < mCurrentGameSequence.size()) {
            int tExpectedOrdinal = mCurrentGameSequence.get(mCurrentEnteredSequence.size());
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info(String.format("tExpectedOrdinal %d gotOrdinal %d", tExpectedOrdinal, pDirection.ordinal()));

            if (tExpectedOrdinal == pDirection.ordinal()) {
                // Current digit is correct, add to history
                mCurrentEnteredSequence.add(pDirection.ordinal());

                if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                    LootGames.LOGGER.info(String.format("CurrSeq  [%s]", joinList(mCurrentGameSequence, ",")));
                if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                    LootGames.LOGGER.info(String.format("EnterSeq [%s]", joinList(mCurrentEnteredSequence, ",")));

                // Check if Player entered all digits correctly for the whole sequence
                if (mCurrentEnteredSequence.size() == mCurrentGameSequence.size()) {
                    // Check if player has played all the way up to the end (Entered sequence is equal or larger than the maximum digits defined
                    if (mCurrentGameSequence.size() >= LootGamesLegacy.ModConfig.GolConfig.GameStageIV.getMinDigitsRequired(worldObj)) {
                        mMaxLevelReached = 5;
                        // Stop him from spending more time here
                        processGameOver(pPlayer);
                    } else {
                        // Game is not yet over.
                        // Play "Sequence correct" sound
                        sendFeedbackSound(eSoundType.SND_GAME_SEQUENCE_CORRECT);

                        // Reset entered digits
                        mCurrentEnteredSequence.clear();

                        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                            LootGames.LOGGER.info(String.format("Checking levelup Current: %d Stage: %d", mCurrentLevel, getCurrentGameStage().LevelID));

                        // Check if the gameStage has changed
                        if (getCurrentGameStage().LevelID > mCurrentLevel) {
                            // Keep track of the highest level the player has reached
                            mMaxLevelReached = getCurrentGameStage().LevelID;

                            // The next digit will be from the next stage, so notify the player about this "levelup"
                            sendFeedbackParticles(eFXType.FX_GAME_WIN, _mMasterTE);
                            sendFeedbackSound(eSoundType.SND_LEVELUP);
                            PlayerChatHelper.SendInfo(pPlayer, "The Structure seems to acknowledge your efforts...");
                            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                                PlayerChatHelper.SendInfo(pPlayer, String.format("LevelUp detected %d > %d", mCurrentLevel, getCurrentGameStage().LevelID));

                            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                                LootGames.LOGGER.info(String.format("Levelup detected %d > %d", mCurrentLevel, getCurrentGameStage().LevelID));
                        }
                        mCurrentLevel = getCurrentGameStage().LevelID;
                        // Add one more random digit to the game-sequence, or randomize it
                        updateGameSequence();

                        // Stop playerinput, set to replay the sequence in 2 seconds
                        // This will automatically happen in onUpdate()
                        mLastGameReplayTime = System.currentTimeMillis() + 2000;
                        mLastPlayedDigitIndex = -1;

                        // Toggle master TE to dark, to indicate that this is our turn now
                        toggleGameBlockActiveState(ExtendedDirections.UP, false);
                        mGameStage = eGameStage.ACTIVE_PLAY_SEQUENCE;
                    }
                }
            } else {
                if (++mFailedAttempts <= LootGamesLegacy.ModConfig.GolConfig.MaxGameTries) {
                    PlayerChatHelper.SendNotifyWarning(pPlayer, "It appears you touched the wrong Block...");
                    sendFeedbackSound(eSoundType.SND_GAME_SEQUENCE_WRONG);
                    resetGame(pPlayer, 2000, false);
                } else
                    processGameOver(pPlayer);
            }
        }
    }

    private LootStageConfig getCurrentGameStage() {
        LootStageConfig tlst;

        // CurrentGameSequence + 1 because the next digit to be taken must already follow the next stage's rules.
        // It is defined as:
        // You need to have AT LEAST "MinDigitsRequired" digits played to get the reward of that stage. Hence the next digit
        // will always be the NEXT stage

        if (mCurrentGameSequence.size() + 1 >= LootGamesLegacy.ModConfig.GolConfig.GameStageIII.getMinDigitsRequired(worldObj))
            tlst = LootGamesLegacy.ModConfig.GolConfig.GameStageIV;

        else if (mCurrentGameSequence.size() + 1 >= LootGamesLegacy.ModConfig.GolConfig.GameStageII.getMinDigitsRequired(worldObj))
            tlst = LootGamesLegacy.ModConfig.GolConfig.GameStageIII;

        else if (mCurrentGameSequence.size() + 1 >= LootGamesLegacy.ModConfig.GolConfig.GameStageI.getMinDigitsRequired(worldObj))
            tlst = LootGamesLegacy.ModConfig.GolConfig.GameStageII;
        else
            tlst = LootGamesLegacy.ModConfig.GolConfig.GameStageI;

        return tlst;
    }

    private boolean doFullColorSet(int pStageID) {
        return (pStageID >= LootGamesLegacy.ModConfig.GolConfig.ExpandPlayFieldAtStage);
        //return ( _mGameBlockNE != null && _mGameBlockNW != null && _mGameBlockSE != null && _mGameBlockSW != null );
    }

    // =============== Lots of NBT Stuff below here ===============

    private void updateGameSequence() {
        LootStageConfig lsc = getCurrentGameStage();

        if (lsc.RandomizeSequence) {
            int tTargetAmount = mCurrentGameSequence.size() + 1;
            mCurrentGameSequence = new ArrayList<>();
            for (int i = 0; i < tTargetAmount; i++)
                mCurrentGameSequence.add(LootGamesLegacy.Rnd.nextInt(doFullColorSet(lsc.LevelID) ? 8 : 4) + 2);
        } else
            mCurrentGameSequence.add(LootGamesLegacy.Rnd.nextInt(doFullColorSet(lsc.LevelID) ? 8 : 4) + 2);
    }

    private void setBlockDirectionTo(ExtendedDirections pDirection, Block pBlock) {
        setBlockDirectionTo(pDirection, pBlock, 0);
    }

    private void setBlockDirectionTo(ExtendedDirections pDirection, Block pBlock, int pMeta) {
        worldObj.setBlock(xCoord + pDirection.offsetX, yCoord + pDirection.offsetY, zCoord + pDirection.offsetZ, pBlock, pMeta, 3);
    }

    private void setBlockDirectionToAir(ExtendedDirections pDirection) {
        setBlockDirectionTo(pDirection, Blocks.air);
    }

    private void processGameOver(EntityPlayer pPlayer) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info("Gameover, destroying TEs and blocks");

        boolean tLostGame = false;


        // If the player has never passed the first level (MaxLevel still 1, AND has NOT reached the MinDigits for round 1, set him to fail
        if (mMaxLevelReached == 1 && (mCurrentGameSequence.size() < LootGamesLegacy.ModConfig.GolConfig.GameStageI.getMinDigitsRequired(worldObj))) {
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info(String.format("Game Lost. Current digits: %d MinRequired for StageI: %d", mCurrentGameSequence.size(), LootGamesLegacy.ModConfig.GolConfig.GameStageI.getMinDigitsRequired(worldObj)));
            tLostGame = true;
            LootGameAchievement.LOOSE_A_GAME.triggerAchievement(pPlayer);
            PlayerChatHelper.SendWarn(pPlayer, "The Structure seems to be offended by your failure");
        } else // Player has either reached MinDigits of stage I, or has reached a higher level before than round
        {
            // Trigger Sound and FX for a successful game
            LootGameAchievement.BEAT_A_GAME.triggerAchievement(pPlayer);
            sendFeedbackSound(eSoundType.SND_GAME_END_WIN);
            sendFeedbackParticles(eFXType.FX_GAME_WIN, getBlockWest());
            sendFeedbackParticles(eFXType.FX_GAME_WIN, getBlockSouth());
            sendFeedbackParticles(eFXType.FX_GAME_WIN, getBlockNorth());
            sendFeedbackParticles(eFXType.FX_GAME_WIN, getBlockEast());
        }

        // Destroy all gameblocks
        setBlockDirectionToAir(ExtendedDirections.NORTH);
        setBlockDirectionToAir(ExtendedDirections.EAST);
        setBlockDirectionToAir(ExtendedDirections.SOUTH);
        setBlockDirectionToAir(ExtendedDirections.WEST);
        setBlockDirectionTo(ExtendedDirections.NORTHEAST, LGBlocks.DUNGEON_LAMP, DungeonLightSource.State.NORMAL.ordinal());
        setBlockDirectionTo(ExtendedDirections.NORTHWEST, LGBlocks.DUNGEON_LAMP, DungeonLightSource.State.NORMAL.ordinal());
        setBlockDirectionTo(ExtendedDirections.SOUTHEAST, LGBlocks.DUNGEON_LAMP, DungeonLightSource.State.NORMAL.ordinal());
        setBlockDirectionTo(ExtendedDirections.SOUTHWEST, LGBlocks.DUNGEON_LAMP, DungeonLightSource.State.NORMAL.ordinal());

        _mGameBlockNorth = null;
        _mGameBlockEast = null;
        _mGameBlockSouth = null;
        _mGameBlockWest = null;
        _mGameBlockNE = null;
        _mGameBlockNW = null;
        _mGameBlockSE = null;
        _mGameBlockSW = null;

        // Destroy master TE
        invalidate();
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);

        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info("Spawn chests");

        // 01.01.2018: Change Loot-Chest spawn to Max-Reached level instead of current correct digits
        if (mMaxLevelReached >= 5) {
            spawnLootChest(ExtendedDirections.NORTH, LootGamesLegacy.ModConfig.GolConfig.GameStageIV);
            LootGameAchievement.GOL_MASTER_LEVEL4.triggerAchievement(pPlayer);
        }
        if (mMaxLevelReached >= 4) {
            spawnLootChest(ExtendedDirections.SOUTH, LootGamesLegacy.ModConfig.GolConfig.GameStageIII);
            LootGameAchievement.GOL_MASTER_LEVEL3.triggerAchievement(pPlayer);
        }
        if (mMaxLevelReached >= 3) {
            spawnLootChest(ExtendedDirections.WEST, LootGamesLegacy.ModConfig.GolConfig.GameStageII);
        }
        if (mMaxLevelReached >= 2) {
            spawnLootChest(ExtendedDirections.EAST, LootGamesLegacy.ModConfig.GolConfig.GameStageI);
        }

        if (tLostGame) {
            sendFeedbackSound(eSoundType.SND_GAME_END_LOOSE);
            processGameLostEvent();
        } else {
            PlayerChatHelper.SendInfo(pPlayer, "The Structure is pleased and rewards you!");
            PlayerChatHelper.SendPlain(pPlayer, String.format("Current Level: %d, Best attempt: %d", mCurrentLevel, mMaxLevelReached - 1));
        }

        StructureGenerator.resetUnbreakablePlayfield(worldObj, xCoord, yCoord, zCoord);
    }

    private void processGameLostEvent() {
        List<String> tFailEffect = new ArrayList<>();

        if (LootGamesLegacy.ModConfig.GolConfig.GameFail_Explode)
            tFailEffect.add("explode");
        if (LootGamesLegacy.ModConfig.GolConfig.GameFail_Lava)
            tFailEffect.add("lava");
        if (LootGamesLegacy.ModConfig.GolConfig.GameFail_Spawn)
            tFailEffect.add("spawn");

        if (tFailEffect.size() == 0)
            return;
        else if (tFailEffect.size() == 1)
            executeFailEvent(tFailEffect.get(0));
        else
            executeFailEvent(tFailEffect.get(LootGamesLegacy.Rnd.nextInt(tFailEffect.size())));
    }

    private void executeFailEvent(String pEffect) {
        if (pEffect.equalsIgnoreCase("explode")) {
            worldObj.createExplosion(null, xCoord, yCoord, zCoord, 5, true);
        } else if (pEffect.equalsIgnoreCase("lava")) {
            for (int tx = -5; tx <= 5; tx++)
                for (int tz = -5; tz <= 5; tz++)
                    for (int ty = 1; ty < 3; ty++)
                        worldObj.setBlock(xCoord + tx, yCoord + ty, zCoord + tz, Blocks.lava);
        } else if (pEffect.equalsIgnoreCase("spawn")) {
            for (int i = 0; i < 6; i++) {
                EntityZombie zombie = new EntityZombie(worldObj);
                zombie.setLocationAndAngles(xCoord, yCoord + 1, zCoord, MathHelper.wrapAngleTo180_float(LootGamesLegacy.Rnd.nextFloat() * 360.0F), 0.0F);
                zombie.rotationYawHead = zombie.rotationYaw;
                zombie.renderYawOffset = zombie.rotationYaw;
                worldObj.spawnEntityInWorld(zombie);
                zombie.playLivingSound();
            }
        }
    }

    private void spawnLootChest(ExtendedDirections pDirection, LootStageConfig pLootStage) {
        int x = xCoord + pDirection.offsetX;
        int y = yCoord + pDirection.offsetY;
        int z = zCoord + pDirection.offsetZ;

        String tLootTable = pLootStage.GetLootTable(worldObj);

        WeightedRandomChestContent[] tRandomLoot = ChestGenHooks.getItems(tLootTable, LootGamesLegacy.Rnd);
        worldObj.setBlock(x, y, z, Blocks.chest);

        // Retrieve the TEs IInventory that should've been created
        IInventory entityChestInventory = (IInventory) worldObj.getTileEntity(x, y, z);
        // If it's not null...
        if (entityChestInventory != null) {
            if (!worldObj.isRemote) {
                if (tRandomLoot.length == 0) {
                    LootGames.LOGGER.error("Received LootTable is empty. Skipping Chest-Gen to avoid NPE Crash");
                    ItemDescriptor tSorryItem = ItemDescriptor.fromString("minecraft:stone");
                    ItemStack tSorryStack = tSorryItem.getItemStackwNBT(1, String.format("{display:{Name:\"The Sorry-Stone\",Lore:[\"The Admin failed to configure the LootTables properly.\",\"Please report that LootList [%s] for GameStageID [%d] is broken, thank you!\"]}}", tLootTable, pLootStage.LevelID));
                    entityChestInventory.setInventorySlotContents(0, tSorryStack);
                } else {
                    int tNumItems = LootGamesLegacy.Rnd.nextInt(pLootStage.MaxItems) + pLootStage.MinItems;
                    WeightedRandomChestContent.generateChestContents(LootGamesLegacy.Rnd, tRandomLoot, entityChestInventory, tNumItems);
                }
            }
        }
    }

    private void resetGame(EntityPlayer pPlayer, boolean pResetMaxLevel) {
        resetGame(pPlayer, 0, pResetMaxLevel);
    }

    private void resetGame(EntityPlayer pPlayer, int pForcedDelay, boolean pResetMaxLevel) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info("resetGame()");

        if (pResetMaxLevel)
            mMaxLevelReached = 1;

        mCurrentPlayer = pPlayer.getUniqueID();
        mCurrentLevel = 1;
        mCurrentGameSequence = new ArrayList<>();
        mCurrentEnteredSequence = new ArrayList<>();
        for (int i = 0; i < LootGamesLegacy.ModConfig.GolConfig.StartDigits; i++)
            mCurrentGameSequence.add(LootGamesLegacy.Rnd.nextInt(4) + 2);

        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("Playback will start soon. Delay: %d", pForcedDelay));

        mLastGameReplayTime = System.currentTimeMillis() + pForcedDelay;
        mGameStage = eGameStage.PENDING_GAME_START;
        mLastPlayedDigitIndex = -1;
        sendFeedbackSound(eSoundType.SND_GAME_START);
    }

    private boolean checkOrDeployStructure() {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info("checkOrDeployStructure()");
        if (mGameStage != eGameStage.SLEEP && mGameStage != eGameStage.UNDEPLOYED)
            return false;

        boolean tSpawnCheck = isValidLocationForDeploy(ExtendedDirections.NORTH);
        if (!isValidLocationForDeploy(ExtendedDirections.NORTHEAST))
            tSpawnCheck = false;
        if (!isValidLocationForDeploy(ExtendedDirections.NORTHWEST))
            tSpawnCheck = false;
        if (!isValidLocationForDeploy(ExtendedDirections.SOUTH))
            tSpawnCheck = false;
        if (!isValidLocationForDeploy(ExtendedDirections.SOUTHEAST))
            tSpawnCheck = false;
        if (!isValidLocationForDeploy(ExtendedDirections.SOUTHWEST))
            tSpawnCheck = false;
        if (!isValidLocationForDeploy(ExtendedDirections.EAST))
            tSpawnCheck = false;
        if (!isValidLocationForDeploy(ExtendedDirections.WEST))
            tSpawnCheck = false;

        if (tSpawnCheck) {
            mNorthPos = setAndReturnGameBlock(ExtendedDirections.NORTH);
            mSouthPos = setAndReturnGameBlock(ExtendedDirections.SOUTH);
            mWestPos = setAndReturnGameBlock(ExtendedDirections.WEST);
            mEastPos = setAndReturnGameBlock(ExtendedDirections.EAST);
            mNorthEastPos = setAndReturnGameBlock(ExtendedDirections.NORTHEAST);
            mNorthWestPos = setAndReturnGameBlock(ExtendedDirections.NORTHWEST);
            mSouthEastPos = setAndReturnGameBlock(ExtendedDirections.SOUTHEAST);
            mSouthWestPos = setAndReturnGameBlock(ExtendedDirections.SOUTHWEST);

            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info("checkOrDeployStructure() > TRUE");
            return true;

        } else {
            if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                LootGames.LOGGER.info("checkOrDeployStructure() > FALSE");
            return false;
        }

    }

    /**
     * Check if given location X/Y/Z is valid for placing our GameBlock
     */
    private boolean isValidLocationForDeploy(ExtendedDirections pDirection) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info("isValidLocationForDeploy()");
        int x = xCoord + pDirection.offsetX;
        int y = yCoord + pDirection.offsetY;
        int z = zCoord + pDirection.offsetZ;

        boolean tRet = true;
        Block atPos = worldObj.getBlock(x, y, z);
        float atPosHardness = atPos.getBlockHardness(worldObj, x, y, z);
        TileEntity atPosTE = worldObj.getTileEntity(x, y, z);

        // Don't replace unbreakable blocks
        if (atPosHardness == -1.0f && atPos != GameOfLightGame.GameBlock && atPos != LGBlocks.DUNGEON_WALL)
            tRet = false;

        // Don't replace TEs
        if (atPosTE != null && !(atPosTE instanceof TELightGameBlock))
            tRet = false;

        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("isValidLocationForDeploy() > %s", tRet));
        return tRet;
    }

    private void initTileEntity(int pMasterX, int pMasterY, int pMasterZ, ExtendedDirections pDirection) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("initTileEntity direction: %s master: %d %d %d", pDirection.toString(), pMasterX, pMasterY, pMasterZ));
        mTEDirection = pDirection;
        mIsActive = false;
        mIsMaster = false;
        mMasterPos = Vec3.createVectorHelper(pMasterX, pMasterY, pMasterZ);
    }

    /**
     * Sets the gameblock at x/y/z, populates the TileEntity with data and returns it
     */
    private Vec3 setAndReturnGameBlock(ExtendedDirections pDirection) {
        int x = xCoord + pDirection.offsetX;
        int y = yCoord + pDirection.offsetY;
        int z = zCoord + pDirection.offsetZ;

        worldObj.setBlock(x, y, z, GameOfLightGame.GameBlock);
        TileEntity tTE = worldObj.getTileEntity(x, y, z);
        if (tTE != null && tTE instanceof TELightGameBlock) {
            TELightGameBlock tOurTile = (TELightGameBlock) tTE;
            tOurTile.initTileEntity(xCoord, yCoord, zCoord, pDirection);

            return Vec3.createVectorHelper(x, y, z);
        } else
            return null;
    }

    private Vec3 readGameBlockFromNBT(NBTTagCompound pTag, String tTagName) {
        NBTTagCompound tSubTag = pTag.getCompoundTag(tTagName);

        int x = tSubTag.getInteger(NBTTAG_BLOCKPOS_X);
        int y = tSubTag.getInteger(NBTTAG_BLOCKPOS_Y);
        int z = tSubTag.getInteger(NBTTAG_BLOCKPOS_Z);

        return Vec3.createVectorHelper(x, y, z);
    }

    private void addGameBlockToNBT(TELightGameBlock pTargetObject, NBTTagCompound pTag, String tTagName) {
        NBTTagCompound tSubTag = new NBTTagCompound();

        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("[%s] Saving bound TE %s at %d %d %d", mTEDirection.toString(), tTagName, pTargetObject.xCoord, pTargetObject.yCoord, pTargetObject.zCoord));
        tSubTag.setInteger(NBTTAG_BLOCKPOS_X, pTargetObject.xCoord);
        tSubTag.setInteger(NBTTAG_BLOCKPOS_Y, pTargetObject.yCoord);
        tSubTag.setInteger(NBTTAG_BLOCKPOS_Z, pTargetObject.zCoord);

        pTag.setTag(tTagName, tSubTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound pNBTTagCompound) {
        super.readFromNBT(pNBTTagCompound);

        mIsMaster = pNBTTagCompound.getBoolean(NBTTAG_ISMASTER);
        mIsActive = pNBTTagCompound.getBoolean(NBTTAG_ISACTIVE);
        mTEDirection = ExtendedDirections.VALID_DIRECTIONS[pNBTTagCompound.getInteger(NBTTAG_DIRECTION)];

        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_NORTH))
            mNorthPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_NORTH);
        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_SOUTH))
            mSouthPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_SOUTH);
        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_WEST))
            mWestPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_WEST);
        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_EAST))
            mEastPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_EAST);

        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_NE))
            mNorthEastPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_NE);
        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_NW))
            mNorthWestPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_NW);
        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_SW))
            mSouthWestPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_SW);
        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_SE))
            mSouthEastPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_SE);

        if (pNBTTagCompound.hasKey(NBTTAG_BLOCK_MASTER))
            mMasterPos = readGameBlockFromNBT(pNBTTagCompound, NBTTAG_BLOCK_MASTER);
    }

    @Override
    public void writeToNBT(NBTTagCompound pNBTTagCompound) {
        super.writeToNBT(pNBTTagCompound);

        pNBTTagCompound.setBoolean(NBTTAG_ISMASTER, mIsMaster);
        pNBTTagCompound.setBoolean(NBTTAG_ISACTIVE, mIsActive);
        pNBTTagCompound.setInteger(NBTTAG_DIRECTION, mTEDirection.ordinal());

        if (getBlockNorth() != null)
            addGameBlockToNBT(getBlockNorth(), pNBTTagCompound, NBTTAG_BLOCK_NORTH);
        if (getBlockSouth() != null)
            addGameBlockToNBT(getBlockSouth(), pNBTTagCompound, NBTTAG_BLOCK_SOUTH);
        if (getBlockWest() != null)
            addGameBlockToNBT(getBlockWest(), pNBTTagCompound, NBTTAG_BLOCK_WEST);
        if (getBlockEast() != null)
            addGameBlockToNBT(getBlockEast(), pNBTTagCompound, NBTTAG_BLOCK_EAST);

        if (getBlockNW() != null)
            addGameBlockToNBT(getBlockNW(), pNBTTagCompound, NBTTAG_BLOCK_NW);
        if (getBlockNE() != null)
            addGameBlockToNBT(getBlockNE(), pNBTTagCompound, NBTTAG_BLOCK_NE);
        if (getBlockSW() != null)
            addGameBlockToNBT(getBlockSW(), pNBTTagCompound, NBTTAG_BLOCK_SW);
        if (getBlockSE() != null)
            addGameBlockToNBT(getBlockSE(), pNBTTagCompound, NBTTAG_BLOCK_SE);

        if (getBlockMaster() != null)
            addGameBlockToNBT(getBlockMaster(), pNBTTagCompound, NBTTAG_BLOCK_MASTER);
    }

    private void setActiveState(boolean pActiveState) {
        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("Setactive state %s for %s", pActiveState, mTEDirection.toString()));
        mIsActive = pActiveState;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    // =============== End NBT Stuff ===============

    public boolean isActive() {
        return mIsActive;
    }

    private void toggleGameBlockActiveState(ExtendedDirections pDirectionTarget, boolean pActiveState) {
        toggleGameBlockActiveState(pDirectionTarget, pActiveState, true);
    }

    private void toggleGameBlockActiveState(ExtendedDirections pDirectionTarget, boolean pActiveState, boolean pAutoTimeout) {
        TELightGameBlock tTargetBlock = null;

        switch (pDirectionTarget) {
            case UP:
                tTargetBlock = _mMasterTE;
                break;
            case NORTH:
                tTargetBlock = getBlockNorth();
                break;
            case SOUTH:
                tTargetBlock = getBlockSouth();
                break;
            case EAST:
                tTargetBlock = getBlockEast();
                break;
            case WEST:
                tTargetBlock = getBlockWest();
                break;
            case NORTHWEST:
                tTargetBlock = getBlockNW();
                break;
            case NORTHEAST:
                tTargetBlock = getBlockNE();
                break;
            case SOUTHWEST:
                tTargetBlock = getBlockSW();
                break;
            case SOUTHEAST:
                tTargetBlock = getBlockSE();
                break;

        }
        if (tTargetBlock != null) {
            if (pAutoTimeout && pActiveState)
                _mActiveBlocks.put(pDirectionTarget, System.currentTimeMillis());

            tTargetBlock.setActiveState(pActiveState);
            if (pActiveState && pDirectionTarget != ExtendedDirections.UP)
                sendFeedbackParticles(eFXType.FX_PLAYBACK, tTargetBlock.xCoord, tTargetBlock.yCoord, tTargetBlock.zCoord);
        }
    }

    private float getPitchForNote(Note pNote, Octave pOctave) {
        int tNoteID = pNote.ordinal() + pOctave.ordinal() * 12;
        return (float) Math.pow(2.0D, (double) (tNoteID - 12) / 12.0D);
    }

    private void sendFeedbackSound(eSoundType pSound) {
        worldObj.playSoundEffect(xCoord, yCoord, zCoord, String.format("%s:%s", pSound.getModName(), pSound.getSoundName()), 0.75F, 1.0F);
    }

    private void sendFeedbackSoundNote(ExtendedDirections pDirection) {
        Note tNote = Note.G_SHARP;
        Octave tOct = Octave.LOW;
        switch (pDirection) {
            case UNKNOWN:
            case DOWN:
            case UP:
                break;
            case NORTHWEST:
                tNote = Note.G;
                break;
            case NORTH:
                tNote = Note.A;
                break;
            case NORTHEAST:
                tNote = Note.B;
                break;
            case EAST:
                tNote = Note.C;
                break;
            case SOUTHEAST:
                tNote = Note.D;
                break;
            case SOUTH:
                tNote = Note.E;
                break;
            case SOUTHWEST:
                tNote = Note.F;
                break;
            case WEST:
                tNote = Note.G;
                tOct = Octave.MID;
                break;
        }

        worldObj.playSoundEffect((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D, "note.harp", 3.0F, getPitchForNote(tNote, tOct));
    }

    private void sendFeedbackParticles(eFXType pFX, TELightGameBlock pSourceBlock) {
        sendFeedbackParticles(pFX, pSourceBlock.xCoord, pSourceBlock.yCoord, pSourceBlock.zCoord);
    }

    private void sendFeedbackParticles(eFXType pFX, int pX, int pY, int pZ) {
        double motionX = 0; // GameOfLights.Rnd.nextGaussian() * 0.02D;
        double motionY = 0.02D; // GameOfLights.Rnd.nextGaussian() * 0.02D;
        double motionZ = 0; // GameOfLights.Rnd.nextGaussian() * 0.02D;
        double fxPosX = pX; // + GameOfLights.Rnd.nextFloat();
        double fxPosY = pY + 0.5D; // + 0.5D * GameOfLights.Rnd.nextFloat();
        double fxPosZ = pZ; // + GameOfLights.Rnd.nextFloat();

        if (LootGamesLegacy.ModConfig.GolConfig.Debug)
            LootGames.LOGGER.info(String.format("FX at %.2f %.2f %.2f", fxPosX, fxPosY, fxPosZ));

        TargetPoint tp = new TargetPoint(this.worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 100);
        NetDispatcher.INSTANCE.sendToAllAround(new SpawnParticleFXMessage(pFX.getFXName(), fxPosX, fxPosY, fxPosZ, motionX, motionY, motionZ), tp);
    }

    // State-machine style main loop for TE update
    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!mIsMaster)
            return;

        if (worldObj.isRemote)
            return;

        if (isInvalid())
            return;

        if (LootGamesLegacy.Rnd.nextInt(5) > 0) // Slow down TE updates
            return;

        // Toggle gameblocks to "off" state after some time (Make this configurable..?)
        HashMap<ExtendedDirections, Long> tNewHM = new HashMap<>();
        if (!_mActiveBlocks.isEmpty()) {
            for (Map.Entry<ExtendedDirections, Long> entry : _mActiveBlocks.entrySet()) {
                if (entry.getValue() + 500 < System.currentTimeMillis())
                    toggleGameBlockActiveState(entry.getKey(), false);
                else
                    tNewHM.put(entry.getKey(), entry.getValue());
            }
            _mActiveBlocks = tNewHM;
        }

        LootStageConfig lsc = getCurrentGameStage();

        if (mGameStage == eGameStage.ACTIVE_PLAY_SEQUENCE) {
            if (mLastGameReplayTime + lsc.DisplayTime < System.currentTimeMillis()) {
                mLastGameReplayTime = System.currentTimeMillis();
                mLastPlayedDigitIndex++;

                if (mLastPlayedDigitIndex < mCurrentGameSequence.size()) {
                    ExtendedDirections newBlock = ExtendedDirections.VALID_DIRECTIONS[mCurrentGameSequence.get(mLastPlayedDigitIndex)];
                    sendFeedbackSoundNote(newBlock);
                    toggleGameBlockActiveState(newBlock, true);
                } else {
                    if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                        LootGames.LOGGER.info("Done replaying, waiting for player input");
                    mLastGameClickTime = System.currentTimeMillis();
                    mGameStage = eGameStage.ACTIVE_WAIT_FOR_PLAYER;
                    toggleGameBlockActiveState(ExtendedDirections.UP, true, false);
                }
            }
        } else if (mGameStage == eGameStage.ACTIVE_WAIT_FOR_PLAYER) {
            if (mLastGameClickTime + (LootGamesLegacy.ModConfig.GolConfig.Timeout * 1000) <= System.currentTimeMillis()) {
                if (LootGamesLegacy.ModConfig.GolConfig.Debug)
                    LootGames.LOGGER.info(String.format("Switching gamestage to SLEEP, as no user input happend for %d seconds", LootGamesLegacy.ModConfig.GolConfig.Timeout));
                mGameStage = eGameStage.SLEEP;
            }
        } else if (mGameStage == eGameStage.PENDING_GAME_START) {
            if (mLastGameReplayTime < System.currentTimeMillis()) {
                mGameStage = eGameStage.ACTIVE_PLAY_SEQUENCE;
                // Visual change of the master block to indicate that it's not ready for player input
                toggleGameBlockActiveState(ExtendedDirections.UP, false);
            }
        }
    }

    public enum eGameStage {
        UNDEPLOYED, SLEEP, ACTIVE_PLAY_SEQUENCE, ACTIVE_WAIT_FOR_PLAYER, PENDING_GAME_START, PLAYMUSIC
    }

    private enum eSoundType {
        SND_GAME_START(unwrap(LGSounds.GOL_START_GAME)), SND_GAME_END_LOOSE(unwrap(LGSounds.GAME_LOSE)), SND_GAME_END_WIN(unwrap(LGSounds.GAME_WIN)), SND_GAME_SEQUENCE_CORRECT(unwrap(LGSounds.GOL_SEQUENCE_COMPLETE)), SND_GAME_SEQUENCE_WRONG(unwrap(LGSounds.GOL_SEQUENCE_WRONG)), SND_LEVELUP("minecraft", "random.levelup");

        String _mSoundName;
        String _mModName;

        eSoundType(String pModName, String pSoundName) {
            _mSoundName = pSoundName;
            _mModName = pModName;
        }

        eSoundType(String pSoundName) {
            this(LootGames.MODID, pSoundName);
        }

        public String getSoundName() {
            return _mSoundName;
        }

        public String getModName() {
            return _mModName;
        }

        private static String unwrap(String name) {
            return name.substring((LootGames.MODID + ":").length());
        }
    }

    private enum eFXType {
        FX_PLAYBACK("spell"), FX_GAME_WIN("happyVillager");

        String _mFxName;

        eFXType(String pParticleEffectName) {
            _mFxName = pParticleEffectName;
        }

        public String getFXName() {
            return _mFxName;
        }
    }
}
