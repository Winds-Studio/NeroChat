package me.softik.nerochat.tools;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.models.UniqueSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class SoftIgnoreTool {
    private final NeroChat plugin;
    private final Map<UUID, List<UUID>> map = new HashMap<>();

    public SoftReturn softIgnorePlayer(Player player, Player ignored) {
        map.putIfAbsent(player.getUniqueId(), new ArrayList<>());

        List<UUID> list = map.get(player.getUniqueId());

        if (list.contains(ignored.getUniqueId())) {
            list.remove(ignored.getUniqueId());

            return SoftReturn.UN_IGNORE;
        } else {
            list.add(ignored.getUniqueId());

            return SoftReturn.IGNORE;
        }
    }

    protected boolean isSoftIgnored(CommandSender chatter, CommandSender receiver) {
        return map.containsKey(new UniqueSender(receiver).getUniqueId()) && map.get(new UniqueSender(receiver).getUniqueId()).contains(new UniqueSender(chatter).getUniqueId());
    }

    protected List<OfflinePlayer> getSoftIgnoredPlayers(Player player) {
        List<OfflinePlayer> players = new ArrayList<>();
        List<UUID> rawIgnorePlayers = map.getOrDefault(player.getUniqueId(), Collections.emptyList());

        for (UUID uuid : rawIgnorePlayers) {
            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);

            if (offlinePlayer != null) {
                players.add(offlinePlayer);
            }
        }

        return players;
    }

    public enum SoftReturn {
        IGNORE, UN_IGNORE
    }
}
