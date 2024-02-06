package me.softik.nerochat.commands.nerochat;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class VersionSubCmd extends SubCommand {

    @Override
    public String label() {
        return "version";
    }

    @Override
    public String description() {
        return "Show the plugin version.";
    }

    @Override
    public String syntax() {
        return "/nerochat "+label();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nerochat.version")) {
            sender.sendMessage(NeroChat.getLang(sender).no_permissions);
            return;
        }

        final PluginDescriptionFile description = NeroChat.getInstance().getDescription();
        final List<String> authors = description.getAuthors();
        final String authorsString = authors.size() > 1 ? String.join(", ", authors) : authors.get(0);

        sender.sendMessage(ChatColor.GOLD + description.getFullName() + " by " + authorsString);
    }
}