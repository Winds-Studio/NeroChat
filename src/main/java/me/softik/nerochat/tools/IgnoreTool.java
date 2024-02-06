package me.softik.nerochat.tools;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class IgnoreTool {
    private final NeroChat plugin;

    public boolean isIgnored(CommandSender chatter, CommandSender receiver) {
        return plugin.getConfigTool().isHardIgnored(chatter, receiver);
    }

    public Map<OfflinePlayer, IgnoreType> getIgnoredPlayers(Player player) {
        Map<OfflinePlayer, IgnoreType> map = new HashMap<>();

        for (OfflinePlayer ignoredPlayer : plugin.getConfigTool().getHardIgnoredPlayers(player)) {
            map.put(ignoredPlayer, IgnoreType.HARD);
        }

        return map;
    }

    public enum IgnoreType {
        HARD
    }
}