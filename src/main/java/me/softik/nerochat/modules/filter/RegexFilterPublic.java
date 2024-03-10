package me.softik.nerochat.modules.filter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.tools.CommonTool;
import me.softik.nerochat.utils.NeroLogUtil;
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

    private final Set<Pattern> banned_regex;
    private final boolean do_logging, notify_player, silent_mode, case_sensitive;

    public RegexFilterPublic() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        this.do_logging = config.getBoolean("audit.regex-filter.public-chat.logging", false);
        this.notify_player = config.getBoolean("audit.regex-filter.public-chat.notify-player", true);
        this.silent_mode = config.getBoolean("audit.regex-filter.public-chat.silent-mode", true);
        this.case_sensitive = config.getBoolean("audit.regex-filter.public-chat.case-sensitive", false);
        this.banned_regex = config.getList("audit.regex-filter.public-chat.banned-regex", Collections.singletonList("^This is a(.*)banned message"),
                        "Prevents any message that starts with \"This is a\" and ends with \"banned message\"")
                .stream()
                .map(regex -> case_sensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public String name() {
        return "banned-regex-public";
    }

    @Override
    public void enable() {
        NeroChat plugin = NeroChat.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("audit.regex-filter.public-chat.enable", false);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nerochat.RegexFilterBypass")) return;

        final String message = event.getMessage();

        for (final Pattern bannedRegex : banned_regex) {
            if (!bannedRegex.matcher(case_sensitive ? message : message.toLowerCase(Locale.ROOT)).find()) {
                continue;
            }

            event.setCancelled(true);

            if (notify_player && !silent_mode) {
                player.sendMessage(NeroChat.getLang(player).player_notify);
            }

            if (silent_mode) {
                CommonTool.sendChatMessage(player, message, player);
            }

            if (do_logging) {
                StringBuilder sb = new StringBuilder();
                for (String word : message.split(" ")) {
                    if (bannedRegex.matcher(case_sensitive ? word : word.toLowerCase(Locale.ROOT)).find()) {
                        sb.append(ChatColor.RED).append(word).append(ChatColor.RESET).append(" ");
                    } else {
                        sb.append(ChatColor.YELLOW).append(word).append(ChatColor.RESET).append(" ");
                    }
                }
                NeroLogUtil.moduleLog(Level.WARNING, name(), String.format("Prevented %s from saying: %s",
                        player.getName(), sb.toString().trim()));
                NeroLogUtil.moduleLog(Level.WARNING, name(), "Regex by which the message was cancelled: '" +
                        (bannedRegex.pattern().length() > 100 ? bannedRegex.pattern().substring(0, 100) + "...(" +
                        (bannedRegex.pattern().length() - 100) + " more characters)" : bannedRegex.pattern()) + "'");
            }

            break;
        }
    }
}
