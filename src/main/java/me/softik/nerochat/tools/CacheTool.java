package me.softik.nerochat.tools;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.models.UniqueSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CacheTool {
    private final HashMap<UUID, PlayerData> map = new HashMap<>();
    private final NeroChat plugin;

    public void cacheLastSenderReceiver(CommandSender sender, CommandSender receiver) {
        final UUID senderUUID = getUUIDThenIndex(sender);
        final UUID receiverUUID = getUUIDThenIndex(receiver);
        map.get(senderUUID).sentTo = receiverUUID;
        map.get(receiverUUID).messagedOf = senderUUID;
    }

    /**
     * Get the last person a player sent a message to.
     *
     * @param sender The player to get data from.
     * @return The last person the player sent a message to.
     */
    public Optional<CommandSender> getLastSentTo(CommandSender sender) {
        final UUID sentTo = map.get(getUUIDThenIndex(sender)).sentTo;
        if (sentTo == null) return Optional.empty();
        final Player nullablePlayer = plugin.getServer().getPlayer(sentTo);
        if (nullablePlayer == null) {
            return Optional.ofNullable(UniqueSender.byUUID(sentTo));
        } else {
            return Optional.of(nullablePlayer);
        }
    }

    /**
     * Get the last person a player was messaged from.
     *
     * @param sender The player to get data from.
     * @return The last person the player was messaged from.
     */
    public Optional<CommandSender> getLastMessagedOf(CommandSender sender) {
        final UUID messagedOf = map.get(getUUIDThenIndex(sender)).messagedOf;
        if (messagedOf == null) return Optional.empty();
        final Player nullablePlayer = plugin.getServer().getPlayer(messagedOf);
        if (nullablePlayer == null) {
            return Optional.ofNullable(UniqueSender.byUUID(messagedOf));
        } else {
            return Optional.of(nullablePlayer);
        }
    }

    private UUID getUUIDThenIndex(CommandSender sender) {
        final UUID uuid = new UniqueSender(sender).getUniqueId();
        if (!map.containsKey(uuid)) {
            map.put(uuid, new PlayerData());
        }
        return uuid;
    }

    private static class PlayerData {
        @Nullable
        public UUID sentTo = null;
        @Nullable
        public UUID messagedOf = null;
    }
}
