package me.softik.nerochat.modules.filter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroWhisperEvent;
import me.softik.nerochat.config.Config;
import me.softik.nerochat.modules.NeroChatModule;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ReadableFormatting implements NeroChatModule, Listener {

    private final String valid_end_chars;
    private final boolean public_auto_caps, public_auto_dot, whisper_auto_dot, whisper_auto_caps;

    public ReadableFormatting() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.getMaster().addDefault("audit.auto-format.enable", null,
                "Automatically puts a period at the end of a sentence and a capital letter at the beginning of a sentence.");
        this.valid_end_chars = config.getString("audit.auto-format.end-sentence-chars", ".?!",
                "If there are these characters at the end of the sentence, the plugin will not automatically put a period.");
        this.public_auto_caps = config.getBoolean("audit.auto-format.public-chat.auto-caps", true);
        this.public_auto_dot = config.getBoolean("audit.auto-format.public-chat.auto-dot", true);
        this.whisper_auto_dot = config.getBoolean("audit.auto-format.whisper.auto-dot", true);
        this.whisper_auto_caps = config.getBoolean("audit.auto-format.whisper.auto-caps", true);
    }

    @Override
    public String name() {
        return "AutoCaps";
    }

    @Override
    public void enable() {
        NeroChat plugin = NeroChat.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("audit.auto-format.enable", false);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!public_auto_dot && !public_auto_caps) return;

        String message = event.getMessage();
        if (message.isEmpty()) return;

        if (public_auto_caps) {
            char[] chars = message.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    break;
                }
            }
            message = new String(chars);
        }

        if (public_auto_dot) {
            if (valid_end_chars.indexOf(message.charAt(message.length() - 1)) == -1) {
                message += ".";
            }
        }

        event.setMessage(message);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerWhisper(NeroWhisperEvent event) {
        if (!whisper_auto_dot && !whisper_auto_caps) return;

        if (event.getSender() instanceof ConsoleCommandSender) return;
        String message = event.getMessage();
        if (message.isEmpty()) return;

        if (whisper_auto_caps) {
            char[] chars = message.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    break;
                }
            }
            message = new String(chars);
        }

        if (whisper_auto_dot) {
            if (valid_end_chars.indexOf(message.charAt(message.length() - 1)) == -1) {
                message += ".";
            }
        }

        event.setMessage(message);
    }
}
