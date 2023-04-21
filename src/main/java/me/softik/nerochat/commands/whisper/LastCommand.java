package me.softik.nerochat.commands.whisper;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.utils.CommonTool;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class LastCommand implements CommandExecutor, TabExecutor {
    private final NeroChat plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Optional<CommandSender> lastSentTo = plugin.getCacheTool().getLastSentTo(sender);
        Optional<CommandSender> lastMessagedOf = plugin.getCacheTool().getLastMessagedOf(sender);
        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NeroChat.getLang(sender).usage + " " + "/reply " + " " + NeroChat.getLang(sender).message_argument));
            return false;
        }

        if (lastSentTo.isPresent()) {
            if (plugin.getIgnoreTool().isIgnored(sender, lastSentTo.get())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_me));
            } else if (plugin.getIgnoreTool().isIgnored(lastSentTo.get(), sender)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_you));
            } else {
                if (args.length > 0) {
                    CommonTool.sendWhisperTo(sender, CommonTool.mergeArgs(args, 0), lastSentTo.get());
                } else {
                    return false;
                }
            }
        } else if (lastMessagedOf.isPresent()) {
            if (plugin.getIgnoreTool().isIgnored(sender, lastMessagedOf.get())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_me));
            } else if (plugin.getIgnoreTool().isIgnored(lastMessagedOf.get(), sender)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_you));
            } else {
                if (args.length > 0) {
                    CommonTool.sendWhisperTo(sender, CommonTool.mergeArgs(args, 0), lastMessagedOf.get());
                } else {
                    return false;
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NeroChat.getLang(sender).not_online));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}