
package eu.usrv.legacylootgames.network.msg;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.yamcore.network.client.AbstractClientMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;


public class SpawnParticleFXMessage implements IMessage {
    protected String mParticleName;
    protected double mPosX;
    protected double mPosY;
    protected double mPosZ;
    protected double mVelX;
    protected double mVelY;
    protected double mVelZ;

    public SpawnParticleFXMessage() {
    }

    public SpawnParticleFXMessage(String pParticleName, double pPosX, double pPosY, double pPosZ, double pVelocityX, double pVelocityY, double pVelocityZ) {
        mParticleName = pParticleName;
        mPosX = pPosX;
        mPosY = pPosY;
        mPosZ = pPosZ;
        mVelX = pVelocityX;
        mVelY = pVelocityY;
        mVelZ = pVelocityZ;
    }

    @Override
    public void fromBytes(ByteBuf pBuffer) {
        NBTTagCompound tTag = ByteBufUtils.readTag(pBuffer);
        mParticleName = tTag.getString("particles");
        mPosX = tTag.getDouble("posx");
        mPosY = tTag.getDouble("posy");
        mPosZ = tTag.getDouble("posz");
        mVelX = tTag.getDouble("velx");
        mVelY = tTag.getDouble("vely");
        mVelZ = tTag.getDouble("velz");
    }

    @Override
    public void toBytes(ByteBuf pBuffer) {
        NBTTagCompound tFXTag = new NBTTagCompound();
        tFXTag.setString("particles", mParticleName);
        tFXTag.setDouble("posx", mPosX);
        tFXTag.setDouble("posy", mPosY);
        tFXTag.setDouble("posz", mPosZ);
        tFXTag.setDouble("velx", mVelX);
        tFXTag.setDouble("vely", mVelY);
        tFXTag.setDouble("velz", mVelZ);

        ByteBufUtils.writeTag(pBuffer, tFXTag);
    }

    public static class SpawnParticleFXMessageHandler extends AbstractClientMessageHandler<SpawnParticleFXMessage> {
        @Override
        public IMessage handleClientMessage(EntityPlayer pPlayer, SpawnParticleFXMessage pMessage, MessageContext pCtx) {
            for (int i = 0; i < 30; i++) {
                pPlayer.worldObj.spawnParticle(pMessage.mParticleName, pMessage.mPosX + LootGamesLegacy.Rnd.nextFloat(), pMessage.mPosY + LootGamesLegacy.Rnd.nextFloat(), pMessage.mPosZ + LootGamesLegacy.Rnd.nextFloat(), pMessage.mVelX + LootGamesLegacy.Rnd.nextGaussian() * 0.02D, pMessage.mVelY + LootGamesLegacy.Rnd.nextGaussian() * 0.02D, pMessage.mVelZ + LootGamesLegacy.Rnd.nextGaussian() * 0.02D);
            }
            return null;
        }
    }
}
