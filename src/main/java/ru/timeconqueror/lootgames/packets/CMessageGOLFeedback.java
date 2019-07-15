package ru.timeconqueror.lootgames.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.tileentity.TileEntityGOLMaster;

public class CMessageGOLFeedback implements IMessage {

    private int x;
    private int y;
    private int z;

    public CMessageGOLFeedback() {
    }

    @SideOnly(Side.CLIENT)
    public CMessageGOLFeedback(BlockPos pos) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<CMessageGOLFeedback, IMessage> {

        public Handler() {
        }

        @Override
        public IMessage onMessage(CMessageGOLFeedback msg, MessageContext ctx) {
            IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
            mainThread.addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getServerWorld();
                BlockPos pos = new BlockPos(msg.x, msg.y, msg.z);
                TileEntity te = world.getTileEntity(pos);

                if (te instanceof TileEntityGOLMaster) {
                    ((TileEntityGOLMaster) te).onClientThingsDone();
                } else {
                    LootGames.logHelper.error("Something sent packet to wrong tileentity {}!", pos);
                }
            });
            return null;
        }
    }
}