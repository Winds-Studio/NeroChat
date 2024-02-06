package me.softik.nerochat.modules.spam;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageRegexMatchCountCheck implements SpamCheck, Listener {

    private final Set<Pattern> bannedRegexes;
    private final double violationIncrement;
    private final int maxMatchesPerMessage;
    private final boolean logIsEnabled;

    protected MessageRegexMatchCountCheck() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        this.maxMatchesPerMessage = config.getInt("checks.regex-match-count.max-matches-per-message", 4);
        this.violationIncrement = config.getDouble("checks.regex-match-count.violations-on-exceed", 10.0);
        this.logIsEnabled = config.getBoolean("checks.regex-match-count.log", true);
        this.bannedRegexes = config.getList("checks.regex-match-count.regex-list", Collections.singletonList("[!@#$%^&*(),.?\":{}|<>]"))
                .stream()
                .map(regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("checks.regex-match-count.enable", true);
    }

    @Override
    public double getViolationIncrement(String plainTextMessage, Player player) {
        final String messageLowerCase = plainTextMessage.toLowerCase(); // Prevents a bypass with capitalized characters

        int count = 0;
        for (Pattern bannedRegex : bannedRegexes) {
            if (bannedRegex.matcher(messageLowerCase).find()) {
                count++;
            }
        }

        if (count >= maxMatchesPerMessage) {
            if (logIsEnabled)
                NeroChat.getLog().info("Player " + player.getName() + " sent a message matching one or more regexes " + (maxMatchesPerMessage-count) + " times more than allowed.");
            return violationIncrement;
        }

        return 0;
    }
}
