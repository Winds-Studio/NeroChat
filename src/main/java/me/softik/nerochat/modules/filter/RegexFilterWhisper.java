package me.softik.nerochat.modules.filter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroWhisperEvent;
import me.softik.nerochat.config.Config;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.tools.CommonTool;
import me.softik.nerochat.utils.NeroLogUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexFilterWhisper implements NeroChatModule, Listener {

    private final Set<Pattern> bannedRegex;
    private final boolean logIsEnabled, notifyPlayer, silent, caseInsensitive;

    public RegexFilterWhisper() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.master().addComment("RegexFilter.Enabled",
                "Filtering chat messages using regular expressions.\n" +
                "If you don't know how to create them, you can use ChatGPT");
        this.logIsEnabled = config.getBoolean("RegexFilter.Whisper.Logs-Enabled", false);
        this.notifyPlayer = config.getBoolean("RegexFilter.Whisper.Player-Notify", true);
        this.silent = config.getBoolean("RegexFilter.Whisper.Silent-Mode", true);
        this.caseInsensitive = config.getBoolean("RegexFilter.Whisper.Case-Insensitive", true);
        this.bannedRegex = config.getList("RegexFilter.Whisper.Banned-Regex", Collections.singletonList("^This is a(.*)banned message"),
                "Prevents any message that starts with \"This is a\" and ends with \"banned message\"")
                .stream()
                .map(regex -> caseInsensitive ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public String name() {
        return "banned-regex-whisper";
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
    public void onWhisper(NeroWhisperEvent event) {
        if (event.getSender().hasPermission("nerochat.RegexFilterBypass")) return;
        if (!(event.getSender() instanceof Player)) return;

        final Player player = (Player) event.getSender();
        final String message = event.getMessage();

        for (final Pattern bannedRegex : bannedRegex) {
            if (!bannedRegex.matcher(caseInsensitive ? message.toLowerCase(Locale.ROOT) : message).find()) {
                continue;
            }

            event.setCancelled(true);

            if (notifyPlayer && !silent) {
                player.sendMessage(NeroChat.getLang(player).player_notify);
            }

            final CommandSender receiver = event.getReceiver();

            if (silent) {
                CommonTool.sendSender(player, message, receiver);
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
                NeroLogUtil.moduleLog(Level.WARNING, name(), String.format("Prevented %s from whispering to %s: %s",
                        player.getName(), receiver.getName(), sb.toString().trim()));
                NeroLogUtil.moduleLog(Level.WARNING, name(), "Regex by which the message was cancelled: '" +
                        (bannedRegex.pattern().length() > 100 ? bannedRegex.pattern().substring(0, 100) + "...(" +
                        (bannedRegex.pattern().length() - 100) + " more characters)" : bannedRegex.pattern()) + "'");
            }

            break;
        }
    }
}
