package me.softik.nerochat.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import me.softik.nerochat.NeroChat;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UniqueSender {
    private static final Map<CommandSender, UUID> customUUID = new HashMap<>();
    private final CommandSender sender;

    public static CommandSender byUUID(UUID uuid) {
        for (Map.Entry<CommandSender, UUID> entry : customUUID.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public UUID getUniqueId() {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        } else {
            customUUID.computeIfAbsent(sender, sender2 -> UUID.randomUUID());

            return customUUID.get(sender);
        }
    }

    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    public String getDisplayName() {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (NeroChat.getConfiguration().getBoolean("Main.display-nickname-color", true)) {
                return ChatColor.stripColor(player.getDisplayName());
            } else {
                return player.getDisplayName();
            }
        } else if (sender instanceof ConsoleCommandSender) {
            return ChatColor.translateAlternateColorCodes('&', NeroChat.getConfiguration().getString("Main.console-name", "[console]"));
        } else {
            return sender.getName();
        }
    }
}
