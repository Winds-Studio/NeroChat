package me.softik.nerochat.modules.filter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroWhisperEvent;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CapsFilter implements NeroChatModule, Listener {

    private final double maxCapsPercentage;

    public CapsFilter() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.master().addComment("CapsFilter.Enabled",
                "Automatic message formatting with a large number of capital letters.");
        this.maxCapsPercentage = config.getDouble("CapsFilter.Percentage", 50.0);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String name() {
        return "caps-filter";
    }

    @Override
    public void enable() {
        NeroChat plugin = NeroChat.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("CapsFilter.Enabled", false);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nerochat.CapsFilterBypass")) return;

        String message = event.getMessage();
        int capsCount = countCaps(message);
        int messageLength = message.replaceAll("\\s+", "").length();
        int capsPercentage = (int) Math.round((capsCount * 100.0) / messageLength);

        if (capsPercentage > maxCapsPercentage) {
            String newMessage = formatMessage(message);
            event.setMessage(newMessage);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onWhisper(NeroWhisperEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender) return;

        Player player = (Player) event.getSender();
        if (player.hasPermission("nerochat.CapsFilterBypass")) return;

        String message = event.getMessage();
        int capsCount = countCaps(message);
        int messageLength = message.replaceAll("\\s+", "").length();
        int capsPercentage = (int) Math.round((capsCount * 100.0) / messageLength);

        if (capsPercentage > maxCapsPercentage) {
            event.setMessage(formatMessage(message));
        }
    }

    private int countCaps(String message) {
        int capsCount = 0;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c) && Character.isUpperCase(c)) {
                capsCount++;
            }
        }
        return capsCount;
    }

    private String formatMessage(String message) {
        StringBuilder newMessage = new StringBuilder();
        boolean lastCharWasCaps;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                lastCharWasCaps = Character.isUpperCase(c);
                newMessage.append(lastCharWasCaps ? Character.toLowerCase(c) : c);
            } else {
                newMessage.append(c);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', newMessage.toString());
    }
}
