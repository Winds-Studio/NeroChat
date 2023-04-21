package me.softik.nerochat.commands.toggle;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ToggleChatCommand implements CommandExecutor, TabExecutor {
    private final NeroChat plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            plugin.getTempDataTool().setChatEnabled(player, !plugin.getTempDataTool().isChatEnabled(player));

            if (plugin.getTempDataTool().isChatEnabled(player)) {
                player.sendMessage(NeroChat.getLang(player).chat_on);
            } else {
                player.sendMessage(NeroChat.getLang(player).chat_off);
            }
        } else {
            sender.sendMessage(NeroChat.getLang(sender).player_only);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
