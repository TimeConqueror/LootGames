package eu.usrv.lootgames.auxiliary;

import eu.usrv.lootgames.LootGames;
import eu.usrv.yamcore.YAMCore;
import eu.usrv.yamcore.auxiliary.IntHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class LGDonorController {
    private final String donorSourceURL;
    private ArrayList<Donor> donorList = new ArrayList<>();
    private final CompletableFuture<ArrayList<Donor>> donorListFuture = new CompletableFuture<>();

    public LGDonorController(String pDonorSourceURL) {
        donorSourceURL = pDonorSourceURL;
    }

    public void loadDonors() {
        Thread thread = new Thread(this::loadDonorsInternally);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadDonorsInternally() {
        ArrayList<Donor> donorList = new ArrayList<>();
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(500).setConnectTimeout(2000).setSocketTimeout(7000).build();

            CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            try (CloseableHttpResponse response = httpClient.execute(new HttpGet(new URI(donorSourceURL)))) {
                if (response.getEntity() != null) {
                    InputStream content = response.getEntity().getContent();

                    String tDonorDefinition = IOUtils.toString(content);

                    String[] lines = tDonorDefinition.split("\\r?\\n");
                    for (String line : lines) {
                        Donor tDonor = Donor.tryLoad(line);
                        if (tDonor != null)
                            donorList.add(tDonor);
                    }
                }
            }

            donorListFuture.complete(donorList);
        } catch (Exception e) {
            donorListFuture.completeExceptionally(e);
        }
    }

    public void fetchDonors() {
        try {
            donorList = donorListFuture.get(7, TimeUnit.SECONDS);
        } catch (Throwable e) {
            String error = String.format("Unable to connect to %s. DonorController will not do anything!", donorSourceURL);
            if (!YAMCore.isDebug()) {
                LootGames.mLog.warn(error);
            } else {
                LootGames.mLog.warn(error, e);
            }
        }
    }

    public boolean isDonor(EntityPlayer pPlayer) {
        return isDonor(pPlayer.getUniqueID());
    }

    public boolean isDonor(EntityPlayerMP pPlayer) {
        return isDonor(pPlayer.getUniqueID());
    }

    public int getLevel(EntityPlayer pPlayer) {
        return getLevel(pPlayer.getUniqueID());
    }

    public int getLevel(EntityPlayerMP pPlayer) {
        return getLevel(pPlayer.getUniqueID());
    }

    public boolean hasExtraArg(EntityPlayer pPlayer, String pDonorArg) {
        return hasExtraArg(pPlayer.getUniqueID(), pDonorArg);
    }

    public boolean hasExtraArg(EntityPlayerMP pPlayer, String pDonorArg) {
        return hasExtraArg(pPlayer.getUniqueID(), pDonorArg);
    }

    private Donor getDonor(UUID pPlayerUUID) {
        for (Donor d : donorList)
            if (d.getUUID().equals(pPlayerUUID))
                return d;

        return null;
    }

    public boolean isDonor(UUID pPlayerUUID) {
        return (getDonor(pPlayerUUID) != null);
    }

    public boolean hasExtraArg(UUID pPlayerUUID, String pDonorArg) {
        Donor d = getDonor(pPlayerUUID);
        return (d != null && d._mDonorExtraArgs.contains(pDonorArg));
    }

    public int getLevel(UUID pPlayerUUID) {
        Donor d = getDonor(pPlayerUUID);
        return (d != null ? d.getLevel() : -1);
    }

    private static final class Donor {
        private final UUID _mUUID;
        private final int _mLevel;
        private final List<String> _mDonorExtraArgs;

        public UUID getUUID() {
            return _mUUID;
        }

        public int getLevel() {
            return _mLevel;
        }

        public List<String> getDonorArgs() {
            return Collections.unmodifiableList(_mDonorExtraArgs);
        }

        public static Donor tryLoad(String pDonorLine) {
            String[] lineArgs = pDonorLine.split("#");
            UUID tUUID = null;
            int tLevel = 0;
            ArrayList<String> tArgs;
      /*
      YAMCore.instance.getLogger().info( String.format( "RawLine: %s", pDonorLine ) );
      if( lineArgs.length > 0 )
        YAMCore.instance.getLogger().info( String.format( "LineArg[0]: %s", lineArgs[0] ) );
      if( lineArgs.length > 1 )
        YAMCore.instance.getLogger().info( String.format( "LineArg[1]: %s", lineArgs[1] ) );
      if( lineArgs.length > 2 )
        YAMCore.instance.getLogger().info( String.format( "LineArg[2]: %s", lineArgs[2] ) );
        */
            try {
                if (lineArgs.length > 0)
                    tUUID = UUID.fromString(lineArgs[0]);
            } catch (Exception e) {
                YAMCore.instance.getLogger().error(String.format("Invalid PlayerUUID found in DonorFile: %s", lineArgs[0]));
            }

            try {
                if (lineArgs.length > 1) {
                    if (!IntHelper.tryParse(lineArgs[1]))
                        YAMCore.instance.getLogger().error(String.format("Second argument in DonorLine is not an integer: %s DonorLevel will default to 0", lineArgs[1]));
                    else
                        tLevel = Integer.parseInt(lineArgs[1]);
                }
            } catch (Exception e) {
                tLevel = 0;
            }

            try {
                if (lineArgs.length > 2)
                    tArgs = new ArrayList<>(Arrays.asList(lineArgs[2].split("!")));
                else
                    tArgs = new ArrayList<>();
            } catch (Exception e) {
                tArgs = new ArrayList<>();
            }

            if (tUUID != null)
                return new Donor(tUUID, tLevel, tArgs);
            else
                return null;
        }

        private Donor(UUID pUserUUID, int pDonationLevel, ArrayList<String> pExtraArgs) {
            _mUUID = pUserUUID;
            _mLevel = pDonationLevel;
            _mDonorExtraArgs = pExtraArgs;
        }
    }
}