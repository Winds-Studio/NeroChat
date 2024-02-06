package me.softik.nerochat.listener;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QuitListener implements Listener {

    private final NeroChat plugin;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTempDataTool().onQuit(event.getPlayer());
    }
}
