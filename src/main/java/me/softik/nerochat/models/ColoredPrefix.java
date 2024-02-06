package me.softik.nerochat.models;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;

public class ColoredPrefix {

    public final String chat_prefix;
    public final Permission permission;
    public final ChatColor chat_color;

    public ColoredPrefix(String chat_prefix, Permission permission, ChatColor chat_color) {
        this.chat_prefix = chat_prefix;
        this.permission = permission;
        this.chat_color = chat_color;
    }
}
