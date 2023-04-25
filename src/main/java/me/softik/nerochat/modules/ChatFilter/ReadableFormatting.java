package me.softik.nerochat.modules.ChatFilter;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroWhisperEvent;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.utils.ConfigCache;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ReadableFormatting implements NeroChatModule, Listener {

    public ReadableFormatting() {
        shouldEnable();
        ConfigCache config = NeroChat.getConfiguration();
    }

    @Override
    public String name() {
        return "AutoCaps";
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
        return NeroChat.getConfiguration().getBoolean("ReadableFormatting.Enable", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (NeroChat.getConfiguration().Readable_Formatting_Public_Chat_Auto_Caps) {
            String message = event.getMessage();
            if (!message.isEmpty()) {
                char[] chars = message.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (Character.isLetter(chars[i])) {
                        chars[i] = Character.toUpperCase(chars[i]);
                        break;
                    }
                }
                String newMessage = new String(chars);
                event.setMessage(newMessage);
            }
        }
        if (NeroChat.getConfiguration().Readable_Formatting_Public_Chat_Auto_Dot) {
            String message = event.getMessage();
            if (!message.isEmpty()) {
                char lastChar = message.charAt(message.length() - 1);
                String endSentenceChars = NeroChat.getConfiguration().getString("ReadableFormatting.End-Sentence-Chars", ".?!");
                if (endSentenceChars.indexOf(lastChar) == -1) {
                    message += ".";
                    event.setMessage(message);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerWhisper(NeroWhisperEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender) return;
        if (NeroChat.getConfiguration().Readable_Formatting_Whisper_Auto_Caps) {
            String message = event.getMessage();
            if (!message.isEmpty()) {
                char[] chars = message.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (Character.isLetter(chars[i])) {
                        chars[i] = Character.toUpperCase(chars[i]);
                        break;
                    }
                }
                String newMessage = new String(chars);
                event.setMessage(newMessage);
            }
        }
        if (NeroChat.getConfiguration().Readable_Formatting_Whisper_Auto_Dot) {
            String message = event.getMessage();
            if (!message.isEmpty()) {
                char lastChar = message.charAt(message.length() - 1);
                String endSentenceChars = NeroChat.getConfiguration().getString("ReadableFormatting.End-Sentence-Chars", ".?!");
                if (endSentenceChars.indexOf(lastChar) == -1) {
                    message += ".";
                    event.setMessage(message);
                }
            }
        }
    }
}
