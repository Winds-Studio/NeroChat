package me.softik.nerochat.tools;

import me.softik.nerochat.NeroChat;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class TempDataTool implements Listener {

    private final Map<CommandSender, TempData> map = new HashMap<>();

    public TempDataTool(NeroChat plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        map.remove(event.getPlayer());
    }

    public void setWhisperingEnabled(CommandSender player, boolean value) {
        map.putIfAbsent(player, new TempData());

        map.get(player).whispering = value;
    }

    public void setChatEnabled(CommandSender player, boolean value) {
        map.putIfAbsent(player, new TempData());

        map.get(player).chat = value;
    }

    public boolean isWhisperingEnabled(CommandSender player) {
        return !map.containsKey(player) || map.get(player).whispering;
    }

    public boolean isChatEnabled(CommandSender player) {
        return !map.containsKey(player) || map.get(player).chat;
    }

    private static class TempData {
        private boolean whispering = true;
        private boolean chat = true;
    }
}
