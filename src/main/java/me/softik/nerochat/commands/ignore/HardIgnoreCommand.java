package me.softik.nerochat.commands.ignore;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.utils.CommonTool;
import me.softik.nerochat.utils.ConfigTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class HardIgnoreCommand implements CommandExecutor, TabExecutor {
    private final NeroChat plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                Optional<Player> ignored = CommonTool.getPlayer(args[0]);

                if (ignored.isPresent()) {
                    ConfigTool.HardReturn type = plugin.getConfigTool().hardIgnorePlayer(player, ignored.get());

                    if (type == ConfigTool.HardReturn.IGNORE) {
                        player.sendMessage(plugin.getConfigTool().getPreparedString("ignore", ignored.get()));
                    } else if (type == ConfigTool.HardReturn.UN_IGNORE) {
                        player.sendMessage(plugin.getConfigTool().getPreparedString("un-ignore", ignored.get()));
                    }
                } else {
                    player.sendMessage(NeroChat.getLang(player).not_online);
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(NeroChat.getLang(sender).player_only);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
