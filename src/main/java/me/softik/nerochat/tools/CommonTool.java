package me.softik.nerochat.tools;

import me.clip.placeholderapi.PlaceholderAPI;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroWhisperEvent;
import me.softik.nerochat.models.ColoredPrefix;
import me.softik.nerochat.models.UniqueSender;
import me.softik.nerochat.utils.KyoriUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;
import java.util.Optional;

public class CommonTool {

    public static Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(Bukkit.getPlayer(name));
    }

    public static void sendWhisperTo(CommandSender sender, String message, CommandSender receiver) {
        if (sender == receiver && !sender.hasPermission("nerochat.whisper.self")) {
            sender.sendMessage(NeroChat.getLang(sender).pm_yourself);
            return;
        }

        if (!sender.hasPermission("nerochat.bypass")) {
            if (!NeroChat.getTempDataTool().isWhisperingEnabled(receiver)) {
                sender.sendMessage(NeroChat.getLang(sender).player_pm_off);
                return;
            }

            if (receiver instanceof Player && isVanished((Player) receiver)) {
                sender.sendMessage(NeroChat.getLang(sender).not_online);
                return;
            }
        }

        NeroWhisperEvent pistonWhisperEvent = new NeroWhisperEvent(sender, receiver, message);
        if (!pistonWhisperEvent.callEvent()) return;

        message = pistonWhisperEvent.getMessage();
        sendSender(sender, message, receiver);
        sendReceiver(sender, message, receiver);

        NeroChat.getCacheTool().cacheLastSenderReceiver(sender, receiver);
    }

    public static void sendSender(CommandSender sender, String message, CommandSender receiver) {
        String senderString = NeroChat.getLang(sender).whisper_to
                .replace("%player%", ChatColor.stripColor(new UniqueSender(receiver).getDisplayName()))
                .replace("%message%", message);

        KyoriUtil.sendMessage(sender, new TextComponent(TextComponent.fromLegacyText(senderString)));
    }

    private static void sendReceiver(CommandSender sender, String message, CommandSender receiver) {
        String receiverString = ChatColor.translateAlternateColorCodes('&', NeroChat.getLang(receiver).whisper_from)
                .replace("%player%", ChatColor.stripColor(new UniqueSender(sender).getDisplayName()))
                .replace("%message%", message);

        KyoriUtil.sendMessage(receiver, new TextComponent(TextComponent.fromLegacyText(receiverString)));
    }

    public static String mergeArgs(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }

    public static String getPrefix() {
        return NeroChat.getConfiguration().plugin_prefix;
    }

    public static ChatColor getChatColorFor(String message, Player player) {
        ChatColor color = ChatColor.WHITE;

        for (ColoredPrefix coloredPrefix : NeroChat.getConfiguration().color_prefixes) {
            if (message.toLowerCase().startsWith(coloredPrefix.chat_prefix) && player.hasPermission(coloredPrefix.permission)) {
                color = coloredPrefix.chat_color;
            }
        }

        return color;
    }

    public static String getFormat(CommandSender sender) {
        String str = NeroChat.getConfiguration().chat_format.replace("%player%", getName(sender));

        if (sender instanceof Player && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            str = parse((OfflinePlayer) sender, str);
        }

        return str;
    }

    public static void sendChatMessage(Player chatter, String message, Player receiver) {
        ComponentBuilder builder = new ComponentBuilder(CommonTool.getFormat(chatter));

        if (receiver.hasPermission("nerochat.playernamereply")) {
            builder.event(new ClickEvent(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    "/w " + ChatColor.stripColor(chatter.getDisplayName()) + " "
            ));

            builder.event(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(
                            NeroChat.getLang(receiver).hover_text.replace("%player%", ChatColor.stripColor(chatter.getDisplayName()))
                    ).create()
            ));
        }

        builder.append(" ").reset();

        builder.append(new TextComponent(TextComponent.fromLegacyText(message)));

        builder.color(CommonTool.getChatColorFor(message, chatter));

        KyoriUtil.sendMessage(receiver, builder.create());
    }

    private static String getName(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (NeroChat.getConfiguration().display_nickname_color) {
                return player.getDisplayName();
            } else {
                return ChatColor.stripColor(player.getDisplayName());
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            return NeroChat.getConfiguration().console_name;
        }

        return sender.getName();
    }

    public static String parse(OfflinePlayer player, String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    private static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
