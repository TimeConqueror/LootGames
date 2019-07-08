package ru.timeconqueror.lootgames.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.util.ConfigReloader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandMain extends CommandBase {

    @Override
    public String getName() {
        return "lootgames";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command." + LootGames.MODID + ".main.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException("command." + LootGames.MODID + ".main.usage");
        } else {
            if (args[0].equals(Commands.RELOAD.getName())) {
                reloadConfigs(server, sender, args);
            } else if (args[0].equals(Commands.HELP.getName())) {
                sender.sendMessage(new TextComponentTranslation("command." + LootGames.MODID + ".help.msg"));
                for (Commands value : Commands.values()) {
                    sender.sendMessage(new TextComponentTranslation(value.getUsage()));
                }
            } else {
                throw new WrongUsageException("command." + LootGames.MODID + ".main.usage");
            }
        }
    }

    private void reloadConfigs(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ConfigReloader.reloadConfigsFromFile(LootGames.MODID, LootGames.MODID);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Commands.getCommands());
        }

        return Collections.emptyList();
    }

    private enum Commands {
        RELOAD("reload"),
        HELP("help");

        private String name;
        private String usage;

        Commands(String name) {
            this.name = name;
            this.usage = "command." + LootGames.MODID + "." + name + ".usage";
        }

        public static String[] getCommands() {
            String[] commands = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                commands[i] = values()[i].name;
            }

            return commands;
        }

        public String getName() {
            return name;
        }

        public String getUsage() {
            return usage;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
