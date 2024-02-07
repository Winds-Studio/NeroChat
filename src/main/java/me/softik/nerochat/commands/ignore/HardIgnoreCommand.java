package me.softik.nerochat.commands.ignore;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.tools.CommonTool;
import me.softik.nerochat.tools.ConfigTool;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HardIgnoreCommand implements NeroChatCommand {

    private final NeroChat plugin;
    private final ConfigTool configTool;

    public HardIgnoreCommand() {
        this.plugin = NeroChat.getInstance();
        this.configTool = NeroChat.getConfigTool();
    }

    @Override
    public String label() {
        return "ignore";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).distinct().collect(Collectors.toList());
        } else {
            return NO_COMPLETIONS;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(NeroChat.getLang(sender).player_only);
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(NeroChat.getLang(sender).usage + " "+label()+" " + NeroChat.getLang(sender).player_argument);
            return true;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(NeroChat.getLang(player).ignore_yourself);
            return true;
        }

        Optional<Player> ignored = CommonTool.getPlayer(args[0]);

        if (!ignored.isPresent()) {
            player.sendMessage(NeroChat.getLang(sender).not_online);
            return true;
        }

        if (ignored.get() == sender) {
            player.sendMessage(NeroChat.getLang(player).ignore_yourself);
            return true;
        }

        switch (configTool.hardIgnorePlayer(player, ignored.get())) {
            case IGNORE:
                player.sendMessage(NeroChat.getLang(sender).ignore
                        .replace("%player%", ChatColor.stripColor(ignored.get().getDisplayName())));
                break;
            case UN_IGNORE:
                player.sendMessage(NeroChat.getLang(sender).un_ignore
                        .replace("%player%", ChatColor.stripColor(ignored.get().getDisplayName())));
                break;
        }

        return true;
    }
}
