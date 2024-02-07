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
    private final boolean do_logging, notify_player, be_silent, case_sensitive;

    public RegexFilterWhisper() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.getMaster().addComment("audit.regex-filter.enable",
                "Filtering chat messages using regular expressions.\n" +
                "If you don't know how to create them, you can use ChatGPT");
        this.do_logging = config.getBoolean("audit.regex-filter.whisper.logging", false);
        this.notify_player = config.getBoolean("audit.regex-filter.whisper.notify-player", true);
        this.be_silent = config.getBoolean("audit.regex-filter.whisper.silent-mode", true);
        this.case_sensitive = config.getBoolean("audit.regex-filter.whisper.case-sensitive", false);
        this.bannedRegex = config.getList("audit.regex-filter.whisper.banned-regex", Collections.singletonList("^This is a(.*)banned message"),
                "Prevents any message that starts with \"This is a\" and ends with \"banned message\"")
                .stream()
                .map(regex -> case_sensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
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
        return NeroChat.getConfiguration().getBoolean("audit.regex-filter.whisper.enable", false);
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
            if (!bannedRegex.matcher(case_sensitive ? message : message.toLowerCase(Locale.ROOT)).find()) {
                continue;
            }

            event.setCancelled(true);

            if (!be_silent && notify_player) {
                player.sendMessage(NeroChat.getLang(player).player_notify);
            }

            final CommandSender receiver = event.getReceiver();

            if (be_silent) {
                CommonTool.sendSender(player, message, receiver);
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
