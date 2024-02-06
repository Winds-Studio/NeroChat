package me.softik.nerochat.listener;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroChatEvent;
import me.softik.nerochat.api.NeroChatReceiveEvent;
import me.softik.nerochat.tools.CommonTool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final NeroChat plugin;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player chatter = event.getPlayer();

        NeroChatEvent neroChatEvent = new NeroChatEvent(chatter, event.getMessage(), event.isAsynchronous());

        if (!neroChatEvent.callEvent()) {
            event.setCancelled(true);
            return;
        }

        event.getRecipients().clear();

        if (!plugin.getTempDataTool().isChatEnabled(chatter)) {
            chatter.sendMessage(NeroChat.getLang(chatter).chat_is_off);
            event.setCancelled(true);
            return;
        }

        for (Player receiver : plugin.getServer().getOnlinePlayers()) {
            if (!plugin.getIgnoreTool().isIgnored(chatter, receiver) && plugin.getTempDataTool().isChatEnabled(receiver)) {
                NeroChatReceiveEvent perPlayerEvent = new NeroChatReceiveEvent(chatter, receiver, neroChatEvent.getMessage());
                if (perPlayerEvent.callEvent()) {
                    CommonTool.sendChatMessage(chatter, perPlayerEvent.getMessage(), receiver);
                }
            }
        }
    }
}
