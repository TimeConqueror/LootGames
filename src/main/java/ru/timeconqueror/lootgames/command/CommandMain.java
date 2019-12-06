package ru.timeconqueror.lootgames.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.auxiliary.ConfigReloader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandMain extends CommandBase {

    @Override
    public String getName() {
        return "lootgames";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command." + LootGames.MOD_ID + ".main.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException("command." + LootGames.MOD_ID + ".main.usage");
        } else {
            if (args[0].equals(Commands.PROFILE.getName()) && LootGames.logHelper.isInDev()) {
                printProfilingResults(sender);
            } else if (args[0].equals(Commands.HELP.getName())) {
                sender.sendMessage(new TextComponentTranslation("command." + LootGames.MOD_ID + ".help.msg"));
                for (Commands value : Commands.values()) {
                    sender.sendMessage(new TextComponentTranslation(value.getUsage()));
                }
            } else {
                throw new WrongUsageException("command." + LootGames.MOD_ID + ".main.usage");
            }
        }
    }

    private void reloadConfigs() {
        ConfigReloader.reloadConfigsFromFile(LootGames.MOD_ID, LootGames.MOD_ID);
    }

    private void printProfilingResults(ICommandSender sender) {
        sender.sendMessage(new TextComponentTranslation("command." + LootGames.MOD_ID + "." + Commands.PROFILE + ".msg"));

        for (String identifier : LootGames.profiler.getIdentifiers()) {
            long averageTime = LootGames.profiler.getAverageTime(identifier);
            String result = averageTime == -1 ? "N/A" : String.format("%d ms", averageTime);
            sender.sendMessage(new TextComponentString(String.format("%s : %s", identifier, result)));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Commands.getCommands());
        }

        return Collections.emptyList();
    }

    private enum Commands {
        PROFILE("profile"),
        HELP("help");

        private String name;
        private String usage;

        Commands(String name) {
            this.name = name;
            this.usage = "command." + LootGames.MOD_ID + "." + name + ".usage";
        }

        public static ArrayList<String> getCommands() {
            ArrayList<String> commands = new ArrayList<>();
            for (int i = 0; i < values().length; i++) {
                if (values()[i] == PROFILE && !LootGames.logHelper.isInDev()) {
                    continue;
                }

                commands.add(values()[i].name);
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
