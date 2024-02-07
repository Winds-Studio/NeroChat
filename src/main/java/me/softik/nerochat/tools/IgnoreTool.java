package me.softik.nerochat.tools;

import me.softik.nerochat.NeroChat;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class IgnoreTool {

    public boolean isIgnored(CommandSender chatter, CommandSender receiver) {
        return NeroChat.getConfigTool().isHardIgnored(chatter, receiver);
    }

    public Map<OfflinePlayer, IgnoreType> getIgnoredPlayers(Player player) {
        Map<OfflinePlayer, IgnoreType> map = new HashMap<>();

        for (OfflinePlayer ignoredPlayer : NeroChat.getConfigTool().getHardIgnoredPlayers(player)) {
            map.put(ignoredPlayer, IgnoreType.HARD);
        }

        return map;
    }

    public enum IgnoreType {
        HARD
    }
}