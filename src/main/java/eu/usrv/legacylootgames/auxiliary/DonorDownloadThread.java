package eu.usrv.legacylootgames.auxiliary;

import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.yamcore.YAMCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.lootgames.LootGames;

public class DonorDownloadThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger("LootGames Downloader");

    private final LGDonorController donorController;

    public DonorDownloadThread(LGDonorController donorController) {
        setDaemon(true);
        this.donorController = donorController;
    }

    @Override
    public void run() {
        try {
            LootGames.LOGGER.debug("Attempt to load donors...");
            LootGamesLegacy.DONOR_CONTROLLER.loadDonors();
            LootGames.LOGGER.debug("Donor controller creation was ended.");
        } catch (Throwable e) {
            String error = "Unable to access the Special People List. No special features will be available.";
            if(!YAMCore.isDebug()) {
                LOGGER.error(error);
            } else {
                LOGGER.error(error, e);
            }
        }
    }
}
