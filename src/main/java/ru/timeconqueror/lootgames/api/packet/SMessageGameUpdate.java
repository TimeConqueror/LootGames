package ru.timeconqueror.lootgames.api.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.tileentity.TileEntityGameMaster;
import ru.timeconqueror.lootgames.minigame.minesweeper.tileentity.TileEntityMSMaster;

import java.io.IOException;

public class SMessageGameUpdate implements IMessage {
    private BlockPos pos;
    private NBTTagCompound compound;
    private String key;

    @ApiStatus.Internal
    public SMessageGameUpdate() {
    }

    public SMessageGameUpdate(BlockPos masterPos, String key, @Nullable NBTTagCompound compound) {
        this.pos = masterPos;
        this.compound = compound;
        this.key = key;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buff = new PacketBuffer(buf);
        pos = buff.readBlockPos();
        try {
            compound = buff.readCompoundTag();
        } catch (IOException e) {
            e.printStackTrace();
        }
        key = ByteBufUtils.readUTF8String(buff);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buff = new PacketBuffer(buf);
        buff.writeBlockPos(pos);
        buff.writeCompoundTag(compound == null ? new NBTTagCompound() : compound);
        ByteBufUtils.writeUTF8String(buff, key);
    }

    public static class Handler implements IMessageHandler<SMessageGameUpdate, IMessage> {

        public Handler() {
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SMessageGameUpdate msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                TileEntity te = world.getTileEntity(msg.pos);
                if (te instanceof TileEntityGameMaster<?>) {
                    TileEntityGameMaster<?> master = (TileEntityMSMaster) te;
                    master.getGame().onUpdatePacket(msg.key, msg.compound);

                } else {
                    LootGames.logHelper.error("Something went wrong. Can't find TileEntityMaster on pos: {}", msg.pos);
                }
            });

            return null;
        }
    }
}
