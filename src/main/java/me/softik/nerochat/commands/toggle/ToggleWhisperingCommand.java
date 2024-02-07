package me.softik.nerochat.commands.toggle;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.tools.TempDataTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleWhisperingCommand implements NeroChatCommand {

    private final TempDataTool tempDataTool;

    public ToggleWhisperingCommand() {
        this.tempDataTool = NeroChat.getTempDataTool();
    }

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

        final Player player = (Player) sender;

        if (tempDataTool.isWhisperingEnabled(player)) {
            tempDataTool.setWhisperingEnabled(player, false);
            player.sendMessage(NeroChat.getLang(player).pm_off);
        } else {
            tempDataTool.setWhisperingEnabled(player, true);
            player.sendMessage(NeroChat.getLang(player).pm_on);
        }

        return true;
    }
}
