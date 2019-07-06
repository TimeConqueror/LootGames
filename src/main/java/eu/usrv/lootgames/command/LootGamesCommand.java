package eu.usrv.lootgames.command;


import eu.usrv.lootgames.LootGames;
import eu.usrv.lootgames.StructureGenerator;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;


public class LootGamesCommand implements ICommand {
    private List aliases;

    public LootGamesCommand() {
        this.aliases = new ArrayList();
        this.aliases.add("lg");
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "lootgames";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "Check the readme for usage";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender pCmdSender, String[] pArgs) {
        try {
            if (pArgs.length < 1) {
                SendHelpToPlayer(pCmdSender);
                return;
            }
            String tSubCommand = pArgs[0];

            if (tSubCommand.equalsIgnoreCase("spawn")) {
                StructureGenerator sGen = new StructureGenerator();
                sGen.generatePuzzleMicroDungeon(((EntityPlayer) pCmdSender).worldObj, (int) ((EntityPlayer) pCmdSender).posX, (int) ((EntityPlayer) pCmdSender).posZ);
            } else if (tSubCommand.equalsIgnoreCase("reload")) {
                LootGames.ModConfig.reload();
                PlayerChatHelper.SendInfo(pCmdSender, "Config reloaded. Note that Retrogen needs a restart!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            PlayerChatHelper.SendError(pCmdSender, "Unknown error occoured");
        }
    }

    private boolean InGame(ICommandSender pCmdSender) {
        return pCmdSender instanceof EntityPlayer;
    }

    private void SendHelpToPlayer(ICommandSender pCmdSender) {
        if (!InGame(pCmdSender)) {
            PlayerChatHelper.SendPlain(pCmdSender, "Command can only be executed ingame");
        } else {
            PlayerChatHelper.SendInfo(pCmdSender, "Check the readme for usage");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender pCommandSender) {
        if (pCommandSender instanceof EntityPlayerMP) {
            EntityPlayerMP tEP = (EntityPlayerMP) pCommandSender;
            boolean tPlayerOpped = MinecraftServer.getServer().getConfigurationManager().func_152596_g(tEP.getGameProfile());
            boolean tIncreative = tEP.capabilities.isCreativeMode;
            return tPlayerOpped && tIncreative;
        } else return pCommandSender instanceof MinecraftServer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }
}
