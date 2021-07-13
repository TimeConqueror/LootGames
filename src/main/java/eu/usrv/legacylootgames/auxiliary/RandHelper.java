package eu.usrv.legacylootgames.auxiliary;


import eu.usrv.legacylootgames.LootGamesLegacy;


public class RandHelper {
    /**
     * Returns ValueA with 50% chance
     */
    public static <T> T flipCoin(T pValueA, T pValueB) {
        return chance(50, pValueA, pValueB);
    }

    /**
     * Returns ValueA with pChance % chance
     */
    public static <T> T chance(int pChance, T pValueA, T pValueB) {
        return LootGamesLegacy.Rnd.nextInt(100) < pChance ? pValueA : pValueB;
    }
}
