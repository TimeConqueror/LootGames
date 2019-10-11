package ru.timeconqueror.lootgames.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.minesweeper.tileentity.TileEntityMSMaster;

public class SMessageMSUpdateBoard implements IMessage {
    private BlockPos pos;
    private int type;
    private int mark;
    private Pos2i relatedSubPos;

    @SideOnly(Side.CLIENT)//TODO check at starting Minecraft Server
    public SMessageMSUpdateBoard() {
    }

    public SMessageMSUpdateBoard(BlockPos pos, Pos2i relatedSubPos, int type, int mark) {
        this.pos = pos;
        this.type = type;
        this.mark = mark;
        this.relatedSubPos = relatedSubPos;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buff = new PacketBuffer(buf);
        pos = buff.readBlockPos();
        type = buff.readInt();
        mark = buff.readInt();

        int subX = buff.readInt();
        int subY = buff.readInt();
        relatedSubPos = new Pos2i(subX, subY);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buff = new PacketBuffer(buf);
        buff.writeBlockPos(pos);
        buff.writeInt(type);
        buff.writeInt(mark);
        buff.writeInt(relatedSubPos.getX());
        buff.writeInt(relatedSubPos.getY());
    }

    public Pos2i getRelatedSubPos() {
        return relatedSubPos;
    }

    public int getType() {
        return type;
    }

    public int getMark() {
        return mark;
    }

    public static class Handler implements IMessageHandler<SMessageMSUpdateBoard, IMessage> {

        public Handler() {
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SMessageMSUpdateBoard msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                TileEntityMSMaster master = (TileEntityMSMaster) world.getTileEntity(msg.pos);
                if (master != null) {
                    master.getGame().onUpdateMessageReceived(msg);
                } else {
                    LootGames.logHelper.error("Something went wrong. Can't find TileEntityMSMaster on pos: {}", msg.pos);
                }
            });

            return null;
        }
    }
}
