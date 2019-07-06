package eu.usrv.lootgames.achievements;


import eu.usrv.lootgames.LootGames;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

import java.util.HashMap;


public enum LootGameAchievement {
    FIND_MINIDUNGEON("lootgames.find_dungeon", LootGames.MasterBlock, 0, 0),
    BEAT_A_GAME("lootgames.win", Items.nether_star, -1, -2, LootGameAchievement.FIND_MINIDUNGEON),
    LOOSE_A_GAME("lootgames.fail", Items.skull, 1, -2, LootGameAchievement.FIND_MINIDUNGEON),
    GOL_MASTER_LEVEL3("lootgames.gol.level3", Items.diamond, -2, -4, true, LootGameAchievement.BEAT_A_GAME),
    GOL_MASTER_LEVEL4("lootgames.gol.level4", Items.emerald, 0, -4, true, LootGameAchievement.BEAT_A_GAME);

    private String _mName;
    private ItemStack _mItemStack;
    private Achievement _mAchievement;


    LootGameAchievement(String pName, ItemStack pItem, int pPosX, int pPosY, boolean pSpecial, LootGameAchievement pPreReq) {
        _mName = pName;
        _mItemStack = pItem;
        if (StaticFields.AchievementList.containsKey(pName))
            throw new IllegalArgumentException("You derp! Achievement %s already exists!");
        else {
            _mAchievement = new Achievement(_mName, _mName, pPosX, pPosY, _mItemStack, pPreReq != null ? pPreReq.getAchievement() : null).registerStat();
            if (pPreReq == null)
                _mAchievement.initIndependentStat();

            if (pSpecial)
                _mAchievement.setSpecial();

            StaticFields.AchievementList.put(_mName, _mAchievement);
        }
    }

    LootGameAchievement(String pName, Item pItem, int pPosX, int pPosY, boolean pSpecial) {
        this(pName, new ItemStack(pItem), pPosX, pPosY, pSpecial, null);
    }

    LootGameAchievement(String pName, Item pItem, int pPosX, int pPosY, boolean pSpecial, LootGameAchievement pPreReq) {
        this(pName, new ItemStack(pItem), pPosX, pPosY, pSpecial, pPreReq);
    }

    LootGameAchievement(String pName, Block pBlock, int pPosX, int pPosY, boolean pSpecial, LootGameAchievement pPreReq) {
        this(pName, new ItemStack(pBlock), pPosX, pPosY, pSpecial, pPreReq);
    }

    LootGameAchievement(String pName, Item pItem, int pPosX, int pPosY, LootGameAchievement pPreReq) {
        this(pName, new ItemStack(pItem), pPosX, pPosY, false, pPreReq);
    }

    LootGameAchievement(String pName, Item pItem, int pPosX, int pPosY) {
        this(pName, new ItemStack(pItem), pPosX, pPosY, false, null);
    }

    LootGameAchievement(String pName, Block pBlock, int pPosX, int pPosY, boolean pSpecial) {
        this(pName, new ItemStack(pBlock), pPosX, pPosY, pSpecial, null);
    }

    LootGameAchievement(String pName, Block pBlock, int pPosX, int pPosY, LootGameAchievement pPreReq) {
        this(pName, new ItemStack(pBlock), pPosX, pPosY, false, pPreReq);
    }

    LootGameAchievement(String pName, Block pBlock, int pPosX, int pPosY) {
        this(pName, new ItemStack(pBlock), pPosX, pPosY, false, null);
    }

    public static void registerAchievementPage() {
        StaticFields.AchievementsPage = new AchievementPage(StatCollector.translateToLocal("lootgames.achievementPage.name"), StaticFields.AchievementList.values().toArray(new Achievement[0]));
        AchievementPage.registerAchievementPage(StaticFields.AchievementsPage);
    }

    public Achievement getAchievement() {
        return _mAchievement;
    }

    public String getName() {
        return _mName;
    }

    public ItemStack getItemStack() {
        return _mItemStack;
    }

    public void triggerAchievement(EntityPlayer pPlayer) {
        if (_mAchievement != null && pPlayer != null)
            pPlayer.triggerAchievement(_mAchievement);
    }

    private static final class StaticFields {
        private static AchievementPage AchievementsPage;
        private static HashMap<String, Achievement> AchievementList = new HashMap<String, Achievement>();
    }
}
