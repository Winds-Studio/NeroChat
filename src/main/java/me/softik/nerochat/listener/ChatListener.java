package me.softik.nerochat.listener;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.api.NeroChatEvent;
import me.softik.nerochat.api.NeroChatReceiveEvent;
import me.softik.nerochat.tools.CommonTool;
import me.softik.nerochat.tools.IgnoreTool;
import me.softik.nerochat.tools.TempDataTool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
public class ChatListener implements Listener {

    private final NeroChat plugin;
    private final IgnoreTool ignoreTool;
    private final TempDataTool tempDataTool;

    public ChatListener() {
        this.plugin = NeroChat.getInstance();
        this.ignoreTool = NeroChat.getIgnoreTool();
        this.tempDataTool = NeroChat.getTempDataTool();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player chatter = event.getPlayer();

        NeroChatEvent neroChatEvent = new NeroChatEvent(chatter, event.getMessage(), event.isAsynchronous());

        if (!neroChatEvent.callEvent()) {
            event.setCancelled(true);
            return;
        }

        if (!tempDataTool.isChatEnabled(chatter)) {
            chatter.sendMessage(NeroChat.getLang(chatter).chat_is_off);
            event.setCancelled(true);
            return;
        }

        event.getRecipients().clear();

        for (Player receiver : plugin.getServer().getOnlinePlayers()) {
            if (!ignoreTool.isIgnored(chatter, receiver) && tempDataTool.isChatEnabled(receiver)) {
                NeroChatReceiveEvent perPlayerEvent = new NeroChatReceiveEvent(chatter, receiver, neroChatEvent.getMessage());
                if (perPlayerEvent.callEvent()) {
                    CommonTool.sendChatMessage(chatter, perPlayerEvent.getMessage(), receiver);
                }
            }
        }
    }
}
