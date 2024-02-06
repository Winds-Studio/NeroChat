package me.softik.nerochat.modules.spam.checks;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public interface SpamCheck {

    boolean shouldEnable();
    double getViolationIncrement(String message, Player player);

    Set<SpamCheck> activeChecks = new HashSet<>();

    static void reloadChecks() {
        activeChecks.clear();
        for (SpamCheck check : Sets.newHashSet(
                new RegexCheck(),
                new MessageRegexMatchCountCheck(),
                new BannedWordsCheck(),
                new MessageLengthCheck(),
                new WordLengthCheck(),
                new URLCheck()
        )) {
            if (check.shouldEnable()) {
                activeChecks.add(check);
            }
        }
    }

    static double getViolations(String message, Player player) {
        double violations = 0;
        for (SpamCheck check : activeChecks) {
            violations += check.getViolationIncrement(message, player);
        }
        return violations;
    }
}
