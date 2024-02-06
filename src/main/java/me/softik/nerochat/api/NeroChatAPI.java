package me.softik.nerochat.api;

import com.google.common.base.Preconditions;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.tools.CommonTool;
import me.softik.nerochat.tools.IgnoreTool;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * API for interacting with NeroChat!
 */
@SuppressWarnings({"unused"})
public final class NeroChatAPI {

    private static NeroChat plugin;

    private NeroChatAPI() {}

    public static void setInstance(NeroChat plugin) {
        if (plugin != null && NeroChatAPI.plugin == null)
            NeroChatAPI.plugin = plugin;
    }

    /**
     * Ignore players! (Can also unignore)
     *
     * @param ignorer The person that ignores someone!
     * @param ignored The person to ignore!
     */
    public static void ignorePlayer(@Nonnull Player ignorer, @Nonnull Player ignored) {
        Preconditions.checkNotNull(ignorer, "Ignorer can not be null!");
        Preconditions.checkNotNull(ignored, "Ignored can not be null!");

        plugin.getConfigTool().hardIgnorePlayer(ignorer, ignored);
    }

    /**
     * Get a list of all players a player ignores!
     *
     * @param player The person who ignores players!
     * @return A list of all players this players ignored!
     */
    @Nonnull
    public static Map<OfflinePlayer, IgnoreTool.IgnoreType> getIgnoreList(@Nonnull Player player) {
        Preconditions.checkNotNull(player, "Player can not be null!");

        return plugin.getIgnoreTool().getIgnoredPlayers(player);
    }

    /**
     * Send whispers!
     *
     * @param sender   The player who sends the whisper!
     * @param message  Whisper to send!
     * @param receiver The person who receives the whisper!
     */
    public static void whisperPlayer(@Nonnull CommandSender sender, @Nonnull String message, @Nonnull CommandSender receiver) {
        Preconditions.checkNotNull(sender, "Sender can not be null!");
        Preconditions.checkNotNull(receiver, "Receiver can not be null!");

        CommonTool.sendWhisperTo(sender, message, receiver);
    }
}
