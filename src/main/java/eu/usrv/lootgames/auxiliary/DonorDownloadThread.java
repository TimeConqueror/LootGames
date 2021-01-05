package eu.usrv.lootgames.auxiliary;

import eu.usrv.lootgames.LootGames;
import eu.usrv.yamcore.YAMCore;
import eu.usrv.yamcore.auxiliary.DonorController;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            LootGames.mLog.debug("Attempt to load donors...");
            LootGames.DONOR_CONTROLLER.loadDonors();
            LootGames.mLog.debug("Donor controller creation was ended.");
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
