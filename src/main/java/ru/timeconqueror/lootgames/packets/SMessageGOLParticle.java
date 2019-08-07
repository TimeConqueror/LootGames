package ru.timeconqueror.lootgames.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;

public class SMessageGOLParticle implements IMessage {
    private int x;
    private int y;
    private int z;
    private int particleID;

    public SMessageGOLParticle() {
    }

    @SideOnly(Side.CLIENT)
    public SMessageGOLParticle(BlockPos pos, int particleID) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.particleID = particleID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        particleID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);

        buf.writeInt(particleID);
    }

    public static class Handler implements IMessageHandler<SMessageGOLParticle, IMessage> {

        public Handler() {
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SMessageGOLParticle msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;

                spawnFeedbackParticles(world, EnumParticleTypes.getParticleFromId(msg.particleID), new BlockPos(msg.x, msg.y, msg.z));
            });


            return null;
        }

        private void spawnFeedbackParticles(World world, EnumParticleTypes particle, BlockPos pos) {
            for (int i = 0; i < 20; i++) {
                world.spawnParticle(particle, pos.getX() + LootGames.rand.nextFloat(), pos.getY() + 0.5F + LootGames.rand.nextFloat(), pos.getZ() + LootGames.rand.nextFloat(), LootGames.rand.nextGaussian() * 0.02D, (0.02D + LootGames.rand.nextGaussian()) * 0.02D, LootGames.rand.nextGaussian() * 0.02D);
            }
        }
    }
}
