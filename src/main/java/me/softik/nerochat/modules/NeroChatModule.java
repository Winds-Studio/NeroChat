package me.softik.nerochat.modules;

import me.softik.nerochat.NeroChat;

import java.util.HashSet;

import me.softik.nerochat.modules.ChatFilter.CapsFilter;
import me.softik.nerochat.modules.ChatFilter.ReadableFormatting;
import me.softik.nerochat.modules.ChatFilter.RegexFilterPublic;
import me.softik.nerochat.modules.ChatFilter.RegexFilterWhisper;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public interface NeroChatModule {

    String name();
    String category();
    void enable();
    boolean shouldEnable();

    HashSet<NeroChatModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.clear();
        NeroChat plugin = NeroChat.getInstance();
        plugin.enabledModules.clear();
        plugin.getServer().getScheduler().cancelTasks(plugin);
        HandlerList.unregisterAll((Plugin) plugin);
        modules.add(new RegexFilterPublic());
        modules.add(new RegexFilterWhisper());
        modules.add(new ReadableFormatting());
        modules.add(new CapsFilter());

        for (NeroChatModule module : modules) {
            if (module.shouldEnable()) module.enable();
            if (module.name() != null) plugin.enabledModules.put("<" + module.category() + "> " + module.name(), module.shouldEnable());
        }
    }
}


