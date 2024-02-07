package me.softik.nerochat.modules;

import me.softik.nerochat.modules.filter.CapsFilter;
import me.softik.nerochat.modules.filter.ReadableFormatting;
import me.softik.nerochat.modules.filter.RegexFilterPublic;
import me.softik.nerochat.modules.filter.RegexFilterWhisper;
import me.softik.nerochat.modules.spam.AntiSpamModule;
import me.softik.nerochat.modules.spam.checks.SpamCheck;

import java.util.HashSet;

public interface NeroChatModule {

    String name();
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

        modules.add(new AntiSpamModule());
        SpamCheck.reloadChecks();

        for (NeroChatModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
