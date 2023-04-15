package me.softik.nerochat.utils;

import net.md_5.bungee.api.ChatColor;
import me.softik.nerochat.NeroChat;

public class LanguageTool {
    private LanguageTool() {
    }

    public static String getMessage(String property) {
        return ChatColor.translateAlternateColorCodes('&', CommonTool.getPrefix() + NeroChat.getPlugin(NeroChat.class).getLanguage().getString(property));
    }
}
