package me.softik.nerochat.modules.ChatFilter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.utils.ConfigCache;
import me.softik.nerochat.utils.LogUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class PreventChatSpam implements NeroChatModule, Listener {

    private final NeroChat plugin;
    private final HashSet<UUID> playersWritingInChat = new HashSet<>();
    private final HashSet<String> previouslySentMessages = new HashSet<>();
    private final HashMap<UUID, Integer> playersAndTheirMessageCounts = new HashMap<>();

    public PreventChatSpam() {
        shouldEnable();
        this.plugin = NeroChat.getInstance();
        ConfigCache config = NeroChat.getConfiguration();
    }

    @Override
    public String name() {
        return "prevent-spam";
    }

    @Override
    public String category() {
        return "chat";
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return ConfigCache.chat_prevent_spam_enable;
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("anarchyexploitfixes.chatbypass")) return;

        String message = event.getMessage();

        final UUID playerUniqueId = player.getUniqueId();
        if (!playersWritingInChat.contains(playerUniqueId)) {
            playersWritingInChat.add(playerUniqueId);
            plugin.getServer().getGlobalRegionScheduler().runDelayed(
                    plugin, task -> playersWritingInChat.remove(playerUniqueId), ConfigCache.antiSpamTime
            );
            if (!isSimilarToPreviouslySentMessages(message)) {
                if (ConfigCache.lenientWordCheckIsEnabled) {
                    if (message.length() > ConfigCache.lenientWordCharacterLimit) {
                        previouslySentMessages.add(message);
                    }
                } else {
                    previouslySentMessages.add(message);
                }
                plugin.getServer().getGlobalRegionScheduler().runDelayed(
                        plugin, task -> previouslySentMessages.remove(message), ConfigCache.antiSpamWordTime
                );

                if (!playersAndTheirMessageCounts.containsKey(playerUniqueId)) {
                    playersAndTheirMessageCounts.put(playerUniqueId, 1);
                } else {
                    if (playersAndTheirMessageCounts.get(playerUniqueId) > ConfigCache.messagesPerTime) {
                        event.setCancelled(true);
                        if (ConfigCache.logIsEnabled) LogUtils.moduleLog(Level.INFO, name(),
                                player.getName() + " FAILED to send message due to too many messages in time period."
                        );
                    } else {
                        playersAndTheirMessageCounts.merge(playerUniqueId, 1, Integer::sum);
                        plugin.getServer().getGlobalRegionScheduler().runDelayed(
                                plugin, task -> playersAndTheirMessageCounts.put(playerUniqueId, playersAndTheirMessageCounts.get(playerUniqueId) - 1), ConfigCache.antiSpamCheckTime
                        );
                    }
                }

            } else {
                event.setCancelled(true);
                if (ConfigCache.logIsEnabled) LogUtils.moduleLog(Level.INFO, name(),
                        player.getName() + " FAILED to send message due to suspected duplicate messages."
                );
            }
        } else {
            event.setCancelled(true);
            if (ConfigCache.logIsEnabled) LogUtils.moduleLog(Level.INFO, name(),
                    player.getName() + " FAILED to send message due to slowmode: '"+message+"'"
            );
        }
    }

    private boolean isSimilarToPreviouslySentMessages(String newMessage) {
        for (String oldMessage : previouslySentMessages) {
            final double similarity = stringSimilarityInPercent(newMessage, oldMessage);
            if (similarity >= ConfigCache.antiSpamWordSimilarityPercentage) {
                if (ConfigCache.logIsEnabled) {
                    LogUtils.moduleLog(Level.WARNING, name(), "Message exceeded similarity limit: " + similarity + "% similar to previously sent message:");
                    LogUtils.moduleLog(Level.WARNING, name(), "'" + oldMessage + "'");
                }
                return true;
            }
        }
        return false;
    }

    private static double stringSimilarityInPercent(String s1, String s2) {
        // Get the longest String for correct percentage values
        String longer = s1;
        String shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        final double longerLength = longer.length();

        if (longerLength == 0) return 100;
        return ((longerLength - new LevenshteinDistance().apply(longer, shorter)) / longerLength) * 100;
    }
}