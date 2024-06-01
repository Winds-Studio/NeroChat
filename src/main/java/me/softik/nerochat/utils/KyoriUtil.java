package me.softik.nerochat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

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
}