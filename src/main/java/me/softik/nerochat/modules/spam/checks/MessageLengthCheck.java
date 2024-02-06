package me.softik.nerochat.modules.spam.checks;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class MessageLengthCheck implements SpamCheck, Listener {

    private final double violationIncrement;
    private final int characterLimit;
    private final boolean logIsEnabled;

    protected MessageLengthCheck() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        this.characterLimit = config.getInt("anti-spam.checks.character-limit.per-message.char-limit", 128);
        this.violationIncrement = config.getDouble("anti-spam.checks.character-limit.per-message.violations-per-detect", 5.0);
        this.logIsEnabled = config.getBoolean("anti-spam.checks.character-limit.per-message.log", true);
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("anti-spam.checks.character-limit.per-message.enable", false);
    }

    @Override
    public double getViolationIncrement(String message, Player player) {
        if (message.length() > characterLimit) {
            if (logIsEnabled)
                NeroChat.getLog().info(player.getName()+" sent a message that exceeded the limit by " +(message.length()-characterLimit)+" character(s).");
            return violationIncrement;
        }
        return 0;
    }
}
