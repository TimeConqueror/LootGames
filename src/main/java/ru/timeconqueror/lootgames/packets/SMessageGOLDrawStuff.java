package ru.timeconqueror.lootgames.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.minigame.gameoflight.TileEntityGOLMaster;

public class SMessageGOLDrawStuff implements IMessage {
    private int x;
    private int y;
    private int z;
    private int stuffIndex;

    public SMessageGOLDrawStuff() {
    }

    @SideOnly(Side.CLIENT)
    public SMessageGOLDrawStuff(BlockPos pos, int stuffIndex) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.stuffIndex = stuffIndex;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        stuffIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);

        buf.writeInt(stuffIndex);
    }

    public static class Handler implements IMessageHandler<SMessageGOLDrawStuff, IMessage> {

        public Handler() {
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SMessageGOLDrawStuff msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                TileEntity te = world.getTileEntity(new BlockPos(msg.x, msg.y, msg.z));
                if (te instanceof TileEntityGOLMaster) {
                    if (msg.stuffIndex < TileEntityGOLMaster.EnumDrawStuff.values().length) {
                        ((TileEntityGOLMaster) te).addStuffToDraw(new TileEntityGOLMaster.DrawInfo(System.currentTimeMillis(), TileEntityGOLMaster.EnumDrawStuff.values()[msg.stuffIndex]));
                    }
                }
            });

            return null;
        }
    }
}
