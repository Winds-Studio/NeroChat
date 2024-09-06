package me.softik.nerochat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KyoriUtil {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Parse a MiniMessage string into a BungeeCord BaseComponent[].
     *
     * @param message The message to parse.
     * @return The parsed message.
     */
    public static BaseComponent[] parseMiniMessage(String message) {
        Component component = miniMessage.deserialize(message);
        return BungeeComponentSerializer.get().serialize(component);
    }

    /**
     * Parse a MiniMessage string into a legacy string.
     *
     * @param message The message to parse.
     * @return The parsed message.
     */
    public static String parseMiniMessageToLegacy(String message) {
        Component component = miniMessage.deserialize(message);
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    /**
     * Send message to player
     *
     * @param player The player send to.
     * @param message The message to send.
     */
    public static void sendMessage(Player player, BaseComponent[] message) {
        player.spigot().sendMessage(message);
    }

    /**
     * Send message to player
     *
     * @param player The player send to.
     * @param message The message to send.
     */
    public static void sendMessage(Player player, BaseComponent message) {
        player.spigot().sendMessage(message);
    }

    /**
     * Send message to player
     *
     * @param player The player send to.
     * @param message The message to send.
     */
    public static void sendMessage(Player player, TextComponent message) {
        player.spigot().sendMessage(message);
    }

    /**
     * Send message to sender
     *
     * @param sender The sender send to.
     * @param message The message to send.
     */
    public static void sendMessage(CommandSender sender, BaseComponent[] message) {
        sender.spigot().sendMessage(message);
    }

    /**
     * Send message to sender
     *
     * @param sender The sender send to.
     * @param message The message to send.
     */
    public static void sendMessage(CommandSender sender, BaseComponent message) {
        sender.spigot().sendMessage(message);
    }

    /**
     * Send message to sender
     *
     * @param sender The sender send to.
     * @param message The message to send.
     */
    public static void sendMessage(CommandSender sender, TextComponent message) {
        sender.spigot().sendMessage(message);
    }
}