package me.softik.nerochat.modules;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.modules.ChatFilter.*;

import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

public interface NeroChatModule {

    String name();
    String category();
    void enable();
    boolean shouldEnable();
    void disable();

    HashSet<NeroChatModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(NeroChatModule::disable);
        modules.clear();
        modules.add(new RegexFilterPublic());
        modules.add(new RegexFilterWhisper());
        modules.add(new ReadableFormatting());
        modules.add(new CapsFilter());
        modules.add(new PreventChatSpam());
        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }

    static SortedMap<String, Boolean> getModuleConfig() {
        SortedMap<String, Boolean> enabledModules = new TreeMap<>();
        for (NeroChatModule module : modules) {
            if (module.name() != null)
                enabledModules.put("<" + module.category() + "> " + module.name(), module.shouldEnable());
        }
        return enabledModules;
    }
}
