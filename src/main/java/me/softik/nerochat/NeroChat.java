package me.softik.nerochat;

import lombok.Getter;
import me.softik.nerochat.api.NeroChatAPI;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.config.Config;
import me.softik.nerochat.config.LanguageCache;
import me.softik.nerochat.listener.ChatListener;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.tools.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

@Getter
public final class NeroChat extends JavaPlugin implements Listener {

    @Getter
    private static NeroChat instance;
    private static Config config;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Logger logger;
    private static Metrics metrics;

    private final TempDataTool tempDataTool = new TempDataTool(this);
    private final SoftIgnoreTool softignoreTool = new SoftIgnoreTool(this);
    private final CacheTool cacheTool = new CacheTool(this);
    private final IgnoreTool ignoreTool = new IgnoreTool(this);
    private final ConfigTool configTool = new ConfigTool(this);

    @Override
    public void onEnable() {
        instance = this;
        NeroChatAPI.setInstance(instance);
        logger = getLogger();

        logger.info("                                                             ");
        logger.info("███╗░░██╗███████╗██████╗░░█████╗░░█████╗░██╗░░██╗░█████╗░████████╗");
        logger.info("████╗░██║██╔════╝██╔══██╗██╔══██╗██╔══██╗██║░░██║██╔══██╗╚══██╔══╝");
        logger.info("██╔██╗██║█████╗░░██████╔╝██║░░██║██║░░╚═╝███████║███████║░░░██║░░░");
        logger.info("██║╚████║██╔══╝░░██╔══██╗██║░░██║██║░░██╗██╔══██║██╔══██║░░░██║░░░");
        logger.info("██║░╚███║███████╗██║░░██║╚█████╔╝╚█████╔╝██║░░██║██║░░██║░░░██║░░░");
        logger.info("╚═╝░░╚══╝╚══════╝╚═╝░░╚═╝░╚════╝░░╚════╝░╚═╝░░╚═╝╚═╝░░╚═╝░░░╚═╝░░░");
        logger.info("                                                             ");

        logger.info("Loading translations");
        reloadLang();

        logger.info("Loading config");
        reloadConfiguration();

        logger.info("Registering commands");
        NeroChatCommand.reloadCommands();

        logger.info("Registering listeners");
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        reloadMetrics();

        logger.info("Ready!");
    }

    public void reloadNeroChat() {
        reloadLang();
        reloadConfiguration();
        NeroChatCommand.reloadCommands();
        reloadMetrics();
    }

    public void reloadMetrics() {
        if (metrics == null) {
            if (config.bstats_metrics) {
                logger.info("Enabling metrics");
                metrics = new Metrics(this, 18215);
            }
        } else {
            if (!config.bstats_metrics) {
                logger.info("Disabling metrics");
                metrics.shutdown();
                metrics = null;
            }
        }
    }

    public void reloadConfiguration() {
        try {
            config = new Config();
            NeroChatModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Error loading config! - "+e.getLocalizedMessage());
        }
    }

    private void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(getDataFolder() + File.separator + "lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                final String localeString = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf('.'));
                logger.info("Found language file for " + localeString);
                languageCacheMap.put(localeString, new LanguageCache(localeString));
            }
            final Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                final Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    final String localeString = langMatcher.group(1).toLowerCase();
                    if (!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        logger.info("Found language file for " + localeString);
                        languageCacheMap.put(localeString, new LanguageCache(localeString));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles() {
        try (final JarFile pluginJarFile = new JarFile(this.getFile())) {
            return pluginJarFile.stream()
                    .map(ZipEntry::getName)
                    .filter(name -> name.startsWith("lang" + File.separator) && name.endsWith(".yml"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.severe("Failed getting default lang files! - "+e.getLocalizedMessage());
            return Collections.emptySet();
        }
    }

    public static LanguageCache getLang(String lang) {
        if (config.auto_lang) {
            return languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(config.default_lang));
        } else {
            return languageCacheMap.get(config.default_lang);
        }
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player ? getLang(((Player) commandSender).getLocale()) : getLang(config.default_lang);
    }

    public static Config getConfiguration() {
        return config;
    }

    public static Logger getLog() {
        return logger;
    }
}