package me.softik.nerochat.events;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroChatEvent;
import me.softik.nerochat.api.NeroChatReceiveEvent;
import me.softik.nerochat.utils.CommonTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ChatEvent implements Listener {
    private final NeroChat plugin;

    // Mute plugins should have a lower priority to work!
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player chatter = event.getPlayer();
        NeroChatEvent neroChatEvent = new NeroChatEvent(chatter, event.getMessage(), event.isAsynchronous());

        event.getRecipients().clear();

        Bukkit.getPluginManager().callEvent(neroChatEvent);

        event.setCancelled(neroChatEvent.isCancelled());

        if (!neroChatEvent.isCancelled()) {
            String message = neroChatEvent.getMessage();

            if (plugin.getTempDataTool().isChatEnabled(chatter)) {
                for (Player receiver : Bukkit.getOnlinePlayers()) {
                    if (!plugin.getIgnoreTool().isIgnored(chatter, receiver) && plugin.getTempDataTool().isChatEnabled(receiver)) {
                        NeroChatReceiveEvent perPlayerEvent = new NeroChatReceiveEvent(chatter, receiver, message);

                        Bukkit.getPluginManager().callEvent(perPlayerEvent);

                        if (perPlayerEvent.isCancelled())
                            continue;

                        message = perPlayerEvent.getMessage();

                        List<String> regexList = plugin.getConfig().getStringList("RegexFilter.Chat.Allowed-Regex");
                        try {
                            boolean useCaseInsensitive = plugin.getConfig().getBoolean("RegexFilter.Chat.CaseInsensitive", true);
                            for (String regex : regexList) {
                                Pattern pattern;
                                if (useCaseInsensitive) {
                                    pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                                } else {
                                    pattern = Pattern.compile(regex);
                                }
                                Matcher matcher = pattern.matcher(message);
                                if (matcher.find()) {
                                    // The message contains an illegal pattern, so cancel the event
                                    if (!plugin.getConfig().getBoolean("RegexFilter.Chat.SilentMode", true) && plugin.getConfig().getBoolean("RegexFilter.Chat.PlayerNotify", true)) {
                                        chatter.sendMessage("PlayerNotify");
                                    }
                                    if (plugin.getConfig().getBoolean("RegexFilter.Chat.ConsoleNotify", true)) {
                                        plugin.getLogger().warning(chatter.getName() + " tried to send a message that didn't match the regex: " + message);
                                    }
                                    if (plugin.getConfig().getBoolean("RegexFilter.Chat.SilentMode", false)) {
                                        CommonTool.sendChatMessage(chatter, message, chatter);
                                    }
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            boolean addPeriod = false;
                            boolean capitalize = false;
                            String lastChar = message.substring(message.length() - 1);
                            if (!lastChar.matches("[.!?]")) {
                                addPeriod = true;
                            }
                            if (Character.isLowerCase(message.charAt(0))) {
                                capitalize = true;
                            }

                            if (plugin.getConfig().getBoolean("ReadableFormatting.PublicChat", false)) {
                                if (addPeriod) {
                                    message += ".";
                                }
                                if (capitalize) {
                                    message = message.substring(0, 1).toUpperCase() + message.substring(1);
                                }
                            }

                            CommonTool.sendChatMessage(chatter, message, receiver);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                chatter.sendMessage(("chatisoff"));
                event.setCancelled(true);
            }
        }
    }
}
