package me.softik.nerochat.modules.spam;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroChatEvent;
import me.softik.nerochat.api.NeroWhisperEvent;
import me.softik.nerochat.config.Config;
import me.softik.nerochat.models.ChatMessageData;
import me.softik.nerochat.models.ExpiringSet;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.modules.spam.checks.SpamCheck;
import me.softik.nerochat.utils.NeroStringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AntiSpamModule implements NeroChatModule, Listener {

    private final NeroChat plugin;
    private final Cache<ChatMessageData, Boolean> detectionCache;
    private final Map<UUID, ExpiringSet<Long>> playersAndTheirMessageCounts;
    private final ExpiringSet<UUID> playersWritingInChatCache;
    private final ExpiringSet<String> previouslySentMessages;
    private final Set<Character.UnicodeBlock> allowedUnicode;
    private final long antiSpamCheckTime;
    private final int messagesPerTime, lenientWordCharacterLimit, antiSpamWordSimilarityPercentage, maxViolations;
    private final boolean logIsEnabled, shouldNotifyPlayer, lenientWordCheckIsEnabled, blockUnicode,
            playersWritingInChatCacheBump, stripSpaces;

    public AntiSpamModule() {
        shouldEnable();
        this.plugin = NeroChat.getInstance();
        this.detectionCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();
        this.playersAndTheirMessageCounts = new ConcurrentHashMap<>();
        Config config = NeroChat.getConfiguration();
        this.logIsEnabled = config.getBoolean("anti-spam.log", false,
                "Bit spammy, intended for debug");
        this.shouldNotifyPlayer = config.getBoolean("anti-spam.notify-player", false,
                "Notifies players to slow down when spamming");
        this.playersWritingInChatCache = new ExpiringSet<>(config.getInt("anti-spam.time", 1,
                "Global limit (Messages per X seconds)"), TimeUnit.SECONDS);
        this.playersWritingInChatCacheBump = config.getBoolean("anti-spam.time-message-bump", true,
                "If a person tries to send a message that gets blocked with slowmode, should we count the message as sent to increase their slowmode?");
        this.previouslySentMessages = new ExpiringSet<>(config.getInt("anti-spam.word-time", 60,
                "How long a player should wait before being able to send another exact same message"), TimeUnit.SECONDS);
        this.antiSpamCheckTime = config.getInt("anti-spam.check-time", 30,
                "How many messages should we allow to send in X seconds");
        this.messagesPerTime = config.getInt("anti-spam.messages-per-time", 10,
                "~1 message every 3 seconds if constantly spamming");
        this.antiSpamWordSimilarityPercentage = config.getInt("anti-spam.word-similarity-percentage", 95,
                "The percentage of similarity to previously sent messages for it to count as spam");
        this.lenientWordCheckIsEnabled = config.getBoolean("anti-spam.lenient-length-check.enable", true,
                "Will not count messages as word spam that are shorter than or equal to the length of the configured limit.");
        this.lenientWordCharacterLimit = config.getInt("anti-spam.lenient-length-check.message-character-limit", 10);
        this.maxViolations = config.getInt("anti-spam.max-violations", 3,
                "How many violations should a player be allowed to have before being blocked");
        this.blockUnicode = config.getBoolean("anti-spam.block-unicode.enabled", true,
                "Limit range of allowed unicode character blocks");
        this.allowedUnicode = config.getList("anti-spam.block-unicode.list", Arrays.asList("BASIC_LATIN", "LATIN_1_SUPPLEMENT"))
                .stream()
                .map(block -> {
                    try {
                        return Character.UnicodeBlock.forName(block);
                    } catch (IllegalArgumentException e) {
                        NeroChat.getLog().warning("Invalid unicode block: " + block);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        this.stripSpaces = config.getBoolean("anti-spam.strip-spaces", true,
                "Strip spaces from start and end of messages");
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("anti-spam.enable", false);
    }

    @Override
    public String name() {
        return "Anti-Spam";
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onChat(NeroChatEvent event) {
        event.setMessage(stripSpaces ? event.getMessage().trim() : event.getMessage());

        Player sender = event.getPlayer();
        ChatMessageData chatMessageData = new ChatMessageData(sender.getUniqueId(), event.getMessage());

        Boolean isSpammingCache = detectionCache.getIfPresent(chatMessageData);
        if (isSpammingCache != null) {
            // We already checked this message, no need to check again
            if (isSpammingCache) {
                event.setCancelled(true);
            }
            return;
        }

        boolean isSpamming = isConsideredSpamming(sender, event.getMessage());
        detectionCache.put(chatMessageData, isSpamming);
        if (isSpamming) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onCommand(NeroWhisperEvent event) {
        if (!(event.getSender() instanceof Player)) return;

        final Player sender = (Player) event.getSender();

        if (isConsideredSpamming(sender, event.getMessage())) {
            if (logIsEnabled) NeroChat.getLog().warning("The message by " + sender.getName() + " is considered spam.");
            event.setCancelled(true);
        }
    }

    private boolean isConsideredSpamming(Player player, final String message) {
        if (player.hasPermission("nerochat.bypass")) return false;

        final UUID playerUniqueId = player.getUniqueId();

        if (playersWritingInChatCache.contains(playerUniqueId)) {
            if (shouldNotifyPlayer)
                player.sendActionBar(NeroChat.getLang(player.getLocale()).slowmode_notification);

            if (logIsEnabled)
                NeroChat.getLog().info(player.getName() + " is considered spamming due to slowmode.");

            if (playersWritingInChatCacheBump)
                playersWritingInChatCache.add(playerUniqueId);

            return true;
        }

        playersWritingInChatCache.add(playerUniqueId);

        if (isSimilarToPreviouslySentMessages(message)) {
            if (shouldNotifyPlayer)
                player.sendActionBar(NeroChat.getLang(player.getLocale()).too_many_similar_messages);

            if (logIsEnabled)
                NeroChat.getLog().info(player.getName() + " is considered spamming due to suspected duplicate messages.");

            return true;
        }

        if (lenientWordCheckIsEnabled) {
            if (message.length() > lenientWordCharacterLimit) {
                previouslySentMessages.add(message);
            }
        } else {
            previouslySentMessages.add(message);
        }

        ExpiringSet<Long> messages = playersAndTheirMessageCounts.compute(
                playerUniqueId, (k, v) -> v == null ? new ExpiringSet<>(antiSpamCheckTime, TimeUnit.SECONDS) : v
        );

        messages.add(System.currentTimeMillis());

        if (messages.getSize() > messagesPerTime) {
            if (shouldNotifyPlayer)
                player.sendActionBar(NeroChat.getLang(player.getLocale()).too_many_messages);

            if (logIsEnabled)
                NeroChat.getLog().info(player.getName() + " is considered spamming due to too many messages in time period.");

            return true;
        }

        if (SpamCheck.getViolations(message, player) > maxViolations) {
            if (shouldNotifyPlayer)
                player.sendActionBar(NeroChat.getLang(player.getLocale()).too_many_violations);
            if (logIsEnabled)
                NeroChat.getLog().info(player.getName() + " is considered spamming due to too many violations.");
            return true;
        }

        if (blockUnicode && isBlockedUnicode(message)) {
            if (shouldNotifyPlayer)
                player.sendActionBar(NeroChat.getLang(player.getLocale()).blocked_unicode);
            if (logIsEnabled)
                NeroChat.getLog().info(player.getName() + " used blocked unicode characters.");
            return true;
        }

        return false;
    }

    private boolean isSimilarToPreviouslySentMessages(String newMessage) {
        for (String oldMessage : previouslySentMessages.getValues()) {
            if (NeroStringUtil.stringSimilarityInPercent(newMessage, oldMessage) >= antiSpamWordSimilarityPercentage) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlockedUnicode(String message) {
        for (char c : message.toCharArray()) {
            if (!allowedUnicode.contains(Character.UnicodeBlock.of(c))) {
                return true;
            }
        }
        return false;
    }
}
