package me.softik.nerochat.commands;

import me.softik.nerochat.NeroChat;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements CommandExecutor, TabExecutor {
    private final NeroChat plugin;
    private FileConfiguration config;

    public MainCommand(NeroChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "version":
                    if (sender.hasPermission("nerochat.version")) {
                        sender.sendMessage(ChatColor.GOLD + "Currently running: " + plugin.getDescription().getFullName());
                    } else {
                        sender.sendMessage(NeroChat.getLang(sender).no_permissions);
                    }

                    break;
                case "reload":
                    if (sender.hasPermission("nerochat.reload")) {
                        NeroChat.getInstance().reloadNeroChat();
                        sender.sendMessage("Reloaded the config!");
                    } else {
                        sender.sendMessage(NeroChat.getLang(sender).no_permissions);
                    }

                    break;
                default:
                    return false;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> possibleCommands = new ArrayList<>();
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("nerochat.reload")) {
                possibleCommands.add("reload");
            }

            StringUtil.copyPartialMatches(args[0], possibleCommands, completions);
            Collections.sort(completions);

            return completions;
        } else {
            return new ArrayList<>();
        }
    }
}
