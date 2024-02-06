package me.softik.nerochat.modules.spam;

import me.softik.nerochat.NeroChat;
import me.softik.nerochat.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class URLCheck implements SpamCheck, Listener {

    private final Set<Pattern> linkRegexes;
    private final double violationIncrement;
    private final int radius;
    private final boolean logIsEnabled, preventOnlyAtSpawn;

    protected URLCheck() {
        shouldEnable();
        Config config = NeroChat.getConfiguration();
        this.violationIncrement = config.getDouble("checks.links.violations-per-detect", 5.0);
        this.logIsEnabled = config.getBoolean("checks.links.log", true);
        this.preventOnlyAtSpawn = config.getBoolean("checks.links.only-check-players-around-spawn", true);
        this.radius = config.getInt("checks.links.spawn-radius", 1000);
        this.linkRegexes = config.getList("checks.links.link-regex-list", Arrays.asList(
                "(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
                "[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z()]{1,6}\\b([-a-zA-Z()@:%_+.~#?&/=]*)"))
                .stream()
                .map(Pattern::compile)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean shouldEnable() {
        return NeroChat.getConfiguration().getBoolean("checks.links.enable", false);
    }

    @Override
    public double getViolationIncrement(String message, Player player) {
        if (preventOnlyAtSpawn && player.getLocation().distance(player.getWorld().getSpawnLocation()) > radius) return 0;

        double vl = 0;

        for (String word : message.split(" ")) {
            for (Pattern regex : linkRegexes) {
                if (regex.matcher(word).find()) {
                    if (logIsEnabled)
                        NeroChat.getLog().info("Player "+player.getName()+" sent a message containing a link.");
                    vl += violationIncrement;
                }
            }
        }

        return vl;
    }
}
