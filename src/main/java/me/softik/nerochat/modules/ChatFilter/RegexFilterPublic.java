package me.softik.nerochat.modules.ChatFilter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.utils.CommonTool;
import me.softik.nerochat.utils.ConfigCache;
import me.softik.nerochat.utils.LogUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class RegexFilterPublic implements NeroChatModule, Listener {

    private final boolean logIsEnabled;
    private final boolean Player_Notify;
    private final boolean Silent_Mode;
    private final boolean useCaseInsensitive;
    private final HashSet<Pattern> bannedRegex = new HashSet<>();

    public RegexFilterPublic() {
        shouldEnable();
        ConfigCache config = NeroChat.getConfiguration();
        this.logIsEnabled = config.getBoolean("RegexFilter.PublicChat.Logs-Enabled", true);
        this.Player_Notify = config.getBoolean("RegexFilter.PublicChat.Player-Notify", true);
        this.Silent_Mode = config.getBoolean("RegexFilter.PublicChat.Silent-Mode", true);
        this.useCaseInsensitive = config.getBoolean("RegexFilter.PublicChat.Case-Insensitive", true);
        List<String> uncompiledRegexes = config.getList("RegexFilter.PublicChat.Banned-Regex", Collections.singletonList("^This is a(.*)banned message"), "Prevents any message that starts with \"This is a\" and ends with \"banned message\"");
        for (String regex : uncompiledRegexes) {
            if (useCaseInsensitive) {
                bannedRegex.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
            } else {
                bannedRegex.add(Pattern.compile(regex));
            }
        }

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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nerochat.RegexFilterBypass")) return;

        String message = event.getMessage();
        String lowerCaseMessage = message.toLowerCase();

        for (Pattern bannedRegex : bannedRegex) {
            if (bannedRegex.matcher(lowerCaseMessage).find()) {
                event.setCancelled(true);
                String displayMessage = message;
                if (useCaseInsensitive) {
                    displayMessage = lowerCaseMessage;
                }
                if (Player_Notify && !Silent_Mode) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', NeroChat.getLang(player).player_notify));
                }
                if (Silent_Mode) {
                    CommonTool.sendChatMessage(player, displayMessage, player);
                }
                if (logIsEnabled) {
                    String[] words = displayMessage.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for (String word : words) {
                        String originalWord = word;
                        if (bannedRegex.matcher(word.toLowerCase()).find()) {
                            sb.append(ChatColor.RED).append(originalWord).append(ChatColor.RESET).append(" ");
                        } else {
                            sb.append(ChatColor.YELLOW).append(word).append(ChatColor.RESET).append(" ");
                        }
                    }
                    String highlightedMessage = sb.toString().trim();
                    String logMessage = String.format("Prevented %s from saying: %s", player.getName(), highlightedMessage);
                    LogUtils.moduleLog(Level.WARNING, name(), logMessage);
                    LogUtils.moduleLog(Level.WARNING, name(), "Regex by which the message was cancelled: '" + bannedRegex.pattern().substring(0, 100) + "...'");
                }
                break;
            }
        }
    }
}
