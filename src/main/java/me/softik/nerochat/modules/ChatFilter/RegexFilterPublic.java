package me.softik.nerochat.modules.ChatFilter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.ConfigCache;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.utils.CommonTool;
import me.softik.nerochat.utils.LogUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexFilterPublic implements NeroChatModule, Listener {

    private final Set<Pattern> bannedRegex;
    private final boolean logIsEnabled, notifyPlayer, silent, caseInsensitive;

    public RegexFilterPublic() {
        shouldEnable();
        ConfigCache config = NeroChat.getConfiguration();
        this.logIsEnabled = config.getBoolean("RegexFilter.PublicChat.Logs-Enabled", false);
        this.notifyPlayer = config.getBoolean("RegexFilter.PublicChat.Player-Notify", true);
        this.silent = config.getBoolean("RegexFilter.PublicChat.Silent-Mode", true);
        this.caseInsensitive = config.getBoolean("RegexFilter.PublicChat.Case-Insensitive", true);
        this.bannedRegex = config.getList("RegexFilter.PublicChat.Banned-Regex", Collections.singletonList("^This is a(.*)banned message"),
                        "Prevents any message that starts with \"This is a\" and ends with \"banned message\"")
                .stream()
                .map(regex -> caseInsensitive ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public String name() {
        return "banned-regex-public";
    }

    @Override
    public String category() {
        return "chat";
    }

    @Override
    public void enable() {
        NeroChat plugin = NeroChat.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("RegexFilter.PublicChat.Enabled", false);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nerochat.RegexFilterBypass")) return;

        final String message = event.getMessage();

        for (final Pattern bannedRegex : bannedRegex) {
            if (!bannedRegex.matcher(caseInsensitive ? message.toLowerCase(Locale.ROOT) : message).find()) {
                continue;
            }

            event.setCancelled(true);

            if (notifyPlayer && !silent) {
                player.sendMessage(NeroChat.getLang(player).player_notify);
            }

            if (silent) {
                CommonTool.sendChatMessage(player, message, player);
            }

            if (logIsEnabled) {
                StringBuilder sb = new StringBuilder();
                for (String word : message.split(" ")) {
                    if (bannedRegex.matcher(caseInsensitive ? word.toLowerCase(Locale.ROOT) : word).find()) {
                        sb.append(ChatColor.RED).append(word).append(ChatColor.RESET).append(" ");
                    } else {
                        sb.append(ChatColor.YELLOW).append(word).append(ChatColor.RESET).append(" ");
                    }
                }
                LogUtils.moduleLog(Level.WARNING, name(), String.format("Prevented %s from saying: %s",
                        player.getName(), sb.toString().trim()));
                LogUtils.moduleLog(Level.WARNING, name(), "Regex by which the message was cancelled: '" +
                        (bannedRegex.pattern().length() > 100 ? bannedRegex.pattern().substring(0, 100) + "...(" +
                        (bannedRegex.pattern().length() - 100) + " more characters)" : bannedRegex.pattern()) + "'");
            }

            break;
        }
    }
}
