package me.softik.nerochat.modules.spam.checks;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import me.softik.nerochat.utils.NeroStringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BannedWordsCheck implements SpamCheck, Listener {

    private final Set<String> bannedWords;
    private final double violationIncrement;
    private final boolean logIsEnabled, isCaseSensitive;

    protected BannedWordsCheck() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.master().addComment("anti-spam.checks.banned-words.enable", "Configure words list in banned-words.yml!");
        this.violationIncrement = config.getDouble("anti-spam.checks.banned-words.violations-per-detect", 10.0);
        this.logIsEnabled = config.getBoolean("anti-spam.checks.banned-words.log-detect", true);
        this.isCaseSensitive = config.getBoolean("anti-spam.checks.banned-words.case-sensitive", false);
        this.bannedWords = config.getListFile("banned-words.yml", "words-or-phrases",
                Collections.singletonList("what's 2+2"),
                "These are checked using a simple String#contains method")
                .stream()
                .map(words -> isCaseSensitive ? words : words.toLowerCase())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("anti-spam.checks.banned-words.enable", false);
    }

    @Override
    public double getViolationIncrement(String plainTextMessage, Player player) {
        final String message = isCaseSensitive ? plainTextMessage : plainTextMessage.toLowerCase();
        for (String bannedWord : bannedWords) {
            if (message.contains(bannedWord)) {
                if (logIsEnabled)
                    NeroChat.getLog().info("Player "+player.getName()+" sent a message containing banned word: '"+bannedWord+"'");

                return violationIncrement;
            } else if (NeroStringUtil.revertLeet(message).contains(bannedWord)) {
                if (logIsEnabled)
                    NeroChat.getLog().info("Player "+player.getName()+" sent a message containing banned word: '"+bannedWord+"' (l33t)");

                return violationIncrement;
            } else if (NeroStringUtil.stripAccents(message).contains(bannedWord)) {
                if (logIsEnabled)
                    NeroChat.getLog().info("Player "+player.getName()+" sent a message containing banned word: '"+bannedWord+"' (accents)");

                return violationIncrement;
            } else if (NeroStringUtil.translateUnicode(message).contains(bannedWord)) {
                if (logIsEnabled)
                    NeroChat.getLog().info("Player "+player.getName()+" sent a message containing banned word: '"+bannedWord+"' (unicode)");

                return violationIncrement;
            } else if (NeroStringUtil.translateLatin(message).contains(bannedWord)) {
                if (logIsEnabled)
                    NeroChat.getLog().info("Player "+player.getName()+" sent a message containing banned word: '"+bannedWord+"' (latin)");

                return violationIncrement;
            }
        }
        return 0;
    }
}
