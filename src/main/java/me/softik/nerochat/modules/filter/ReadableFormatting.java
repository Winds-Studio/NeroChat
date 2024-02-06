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

    private final String end_Chars;
    private final boolean public_Chat_Auto_Caps, public_Chat_Auto_Dot, whisper_Auto_Dot, whisper_Auto_Caps;

    public ReadableFormatting() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.master().addSection("ReadableFormatting");
        config.master().addDefault("ReadableFormatting", null,
                "Automatically puts a period at the end of a sentence and a capital letter at the beginning of a sentence.");
        this.end_Chars = config.getString("ReadableFormatting.End-Sentence-Chars", ".?!",
                "If there are these characters at the end of the sentence, the plugin will not automatically put a period.");
        this.public_Chat_Auto_Caps = config.getBoolean("ReadableFormatting.PublicChat.Auto-Caps", true);
        this.public_Chat_Auto_Dot = config.getBoolean("ReadableFormatting.PublicChat.Auto-Dot", true);
        this.whisper_Auto_Dot = config.getBoolean("ReadableFormatting.Whisper.Auto-Dot", true);
        this.whisper_Auto_Caps = config.getBoolean("ReadableFormatting.Whisper.Auto-Caps", true);
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
        return NeroChat.getConfiguration().getBoolean("ReadableFormatting.Enable", false);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!public_Chat_Auto_Dot && !public_Chat_Auto_Caps) return;

        String message = event.getMessage();
        if (message.isEmpty()) return;

        if (public_Chat_Auto_Caps) {
            char[] chars = message.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    break;
                }
            }
            message = new String(chars);
        }

        if (public_Chat_Auto_Dot) {
            if (end_Chars.indexOf(message.charAt(message.length() - 1)) == -1) {
                message += ".";
            }
        }

        event.setMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerWhisper(NeroWhisperEvent event) {
        if (!whisper_Auto_Dot && !whisper_Auto_Caps) return;

        if (event.getSender() instanceof ConsoleCommandSender) return;
        String message = event.getMessage();
        if (message.isEmpty()) return;

        if (whisper_Auto_Caps) {
            char[] chars = message.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isLetter(chars[i])) {
                    chars[i] = Character.toUpperCase(chars[i]);
                    break;
                }
            }
            message = new String(chars);
        }

        if (whisper_Auto_Dot) {
            if (end_Chars.indexOf(message.charAt(message.length() - 1)) == -1) {
                message += ".";
            }
        }

        event.setMessage(message);
    }
}
