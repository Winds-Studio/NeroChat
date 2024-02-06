package me.softik.nerochat.commands.toggle;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class ToggleWhisperingCommand implements NeroChatCommand {

    private final NeroChat plugin;

    @Override
    public String label() {
        return "togglewhispering";
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

        Player player = (Player) sender;

        if (plugin.getTempDataTool().isWhisperingEnabled(player)) {
            plugin.getTempDataTool().setWhisperingEnabled(player, false);
            player.sendMessage(NeroChat.getLang(player).pm_off);
        } else {
            plugin.getTempDataTool().setWhisperingEnabled(player, true);
            player.sendMessage(NeroChat.getLang(player).pm_on);
        }

        return true;
    }
}
