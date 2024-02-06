package me.softik.nerochat.modules.spam.checks;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexCheck implements SpamCheck, Listener {

    private final Set<Pattern> bannedRegex;
    private final double violationIncrement;
    private final boolean logIsEnabled;

    protected RegexCheck() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        config.getMaster().addComment("anti-spam.checks.banned-regex.enable", "Configure regex list in banned-regex.yml!");
        this.violationIncrement = config.getDouble("anti-spam.checks.regex.violations-per-detect", 10.0);
        this.logIsEnabled = config.getBoolean("anti-spam.checks.regex.log", true);
        this.bannedRegex = config.getListFile("spam-regex.yml", "regex-list",
                        Collections.singletonList("^This is a(.*)banned message"))
                .stream()
                .map(regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("anti-spam.checks.banned-regex.enable", false);
    }

    @Override
    public double getViolationIncrement(String plainTextMessage, Player player) {
        final String messageLowerCase = plainTextMessage.toLowerCase(); // Prevents a bypass with capitalized characters
        for (Pattern bannedRegex : bannedRegex) {
            if (bannedRegex.matcher(messageLowerCase).find()) {
                if (logIsEnabled) NeroChat.getLog().info("Player "+player.getName()+" sent a message matching regex: '"+bannedRegex+"'");
                return violationIncrement;
            }
        }
        return 0;
    }
}
