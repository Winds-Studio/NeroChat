package me.softik.nerochat.modules.spam;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class WordLengthCheck implements SpamCheck, Listener {

    private final double violationIncrement;
    private final boolean logIsEnabled;
    private final int characterLimit;

    protected WordLengthCheck() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        this.violationIncrement = config.getDouble("checks.character-limit.per-word.violations-per-detect", 5.0);
        this.logIsEnabled = config.getBoolean("checks.character-limit.per-word.log", true);
        this.characterLimit = config.getInt("checks.character-limit.per-word.char-limit", 128);
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("checks.character-limit.per-word.enable", true);
    }

    @Override
    public double getViolationIncrement(String message, Player player) {
        double vl = 0;

        for (String word : message.split(" ")) {
            final int charLength = word.length();
            if (charLength > characterLimit) {
                if (logIsEnabled)
                    NeroChat.getLog().info(player.getName() + " sent a word that exceeded the limit by " + (charLength - characterLimit) + " character(s).");
                vl += violationIncrement;
            }
        }

        return vl;
    }
}
