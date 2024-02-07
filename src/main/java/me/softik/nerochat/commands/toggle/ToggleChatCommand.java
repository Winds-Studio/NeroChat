package me.softik.nerochat.commands.toggle;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.tools.TempDataTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleChatCommand implements NeroChatCommand {

    private final TempDataTool tempDataTool;

    public ToggleChatCommand() {
        this.tempDataTool = NeroChat.getTempDataTool();
    }

    @Override
    public String label() {
        return "togglechat";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return NO_COMPLETIONS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(NeroChat.getLang(sender).player_only);
            return true;
        }

        final Player player = (Player) sender;

        if (tempDataTool.isChatEnabled(player)) {
            tempDataTool.setChatEnabled(player, false);
            player.sendMessage(NeroChat.getLang(player).chat_off);
        } else {
            tempDataTool.setChatEnabled(player, true);
            player.sendMessage(NeroChat.getLang(player).chat_on);
        }

        return true;
    }
}
