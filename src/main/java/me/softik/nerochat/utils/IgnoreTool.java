package me.softik.nerochat.utils;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Parent for both soft and hard banning!
 */
@RequiredArgsConstructor
public class IgnoreTool {
    private final NeroChat plugin;
    public boolean isIgnored(CommandSender chatter, CommandSender receiver) {
        if (plugin.getSoftignoreTool().isSoftIgnored(chatter, receiver)) {
            return true;
        //} else return plugin.getConfigTool().isHardIgnored(chatter, receiver);
        } else return false;
    }

    public Map<OfflinePlayer, IgnoreType> getIgnoredPlayers(Player player) {
        Map<OfflinePlayer, IgnoreType> map = new HashMap<>();

        for (OfflinePlayer ignoredPlayer : plugin.getSoftignoreTool().getSoftIgnoredPlayers(player)) {
            map.put(ignoredPlayer, IgnoreType.SOFT);
        }

        //for (OfflinePlayer ignoredPlayer : plugin.getConfigTool().getHardIgnoredPlayers(player)) {
            //map.put(ignoredPlayer, IgnoreType.HARD);
        //}

        return map;
    }

    public enum IgnoreType {
        SOFT, HARD
    }
}
