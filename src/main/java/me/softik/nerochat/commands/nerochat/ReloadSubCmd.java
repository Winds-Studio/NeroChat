package me.softik.nerochat.commands.nerochat;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String label() {
        return "reload";
    }

    @Override
    public String description() {
        return "Reload the plugin configuration.";
    }

    @Override
    public String syntax() {
        return "/nerochat "+label();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nerochat.reload")) {
            sender.sendMessage(NeroChat.getLang(sender).no_permissions);
            return;
        }

        sender.sendMessage(ChatColor.AQUA + "Reloading "+ NeroChat.getInstance().getDescription().getName()+"...");
        NeroChat.getInstance().reloadNeroChat();
        sender.sendMessage(ChatColor.GREEN + "Reload complete.");
    }
}