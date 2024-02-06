package me.softik.nerochat.commands.nerochat;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.commands.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NeroChatCmd implements NeroChatCommand {

    private final NeroChat plugin;
    private final List<SubCommand> subCommands;
    private final List<String> tabCompleter;

    public NeroChatCmd(NeroChat plugin) {
        this.plugin = plugin;
        this.subCommands = Arrays.asList(new ReloadSubCmd(), new VersionSubCmd());
        this.tabCompleter = subCommands.stream().map(SubCommand::label).sorted().collect(Collectors.toList());
    }

    @Override
    public String label() {
        return "nerochat";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? tabCompleter : NO_COMPLETIONS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendCommandOverview(sender);
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.label())) {
                subCommand.perform(sender, args);
                return true;
            }
        }

        sendCommandOverview(sender);
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        if (!sender.hasPermission("nerochat.*")) return;

        sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
        sender.sendMessage(ChatColor.GOLD + plugin.getDescription().getName() + " Commands");
        sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
        subCommands.forEach(subCommand -> sender.sendMessage(subCommand.syntax() + " - " + subCommand.description()));
        sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    }
}
