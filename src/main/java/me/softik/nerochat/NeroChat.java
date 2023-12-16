package me.softik.nerochat;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import lombok.Getter;
import me.softik.nerochat.api.NeroChatAPI;
import me.softik.nerochat.commands.MainCommand;
import me.softik.nerochat.commands.ignore.HardIgnoreCommand;
import me.softik.nerochat.commands.ignore.IgnoreListCommand;
import me.softik.nerochat.commands.toggle.ToggleChatCommand;
import me.softik.nerochat.commands.toggle.ToggleWhisperingCommand;
import me.softik.nerochat.commands.whisper.LastCommand;
import me.softik.nerochat.commands.whisper.ReplyCommand;
import me.softik.nerochat.commands.whisper.WhisperCommand;
import me.softik.nerochat.events.ChatEvent;
import me.softik.nerochat.events.QuitEvent;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.utils.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public final class NeroChat extends JavaPlugin implements Listener {

    private final TempDataTool tempDataTool = new TempDataTool();
    private final SoftIgnoreTool softignoreTool = new SoftIgnoreTool();
    private final CacheTool cacheTool = new CacheTool(this);
    private final IgnoreTool ignoreTool = new IgnoreTool(this);
    private final ConfigTool configTool = new ConfigTool(this);
    @Getter
    private static NeroChat instance;
    private static ConfigCache configCache;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Logger logger;
    private ConfigFile configFile;
    public final SortedMap<String, Boolean> enabledModules = new TreeMap<>();

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        NeroChatAPI.setInstance(this);
        Server server = getServer();

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
        PluginCommand ignore = server.getPluginCommand("ignore");
        PluginCommand whisper = server.getPluginCommand("whisper");
        PluginCommand reply = server.getPluginCommand("reply");
        PluginCommand last = server.getPluginCommand("last");
        PluginCommand ignorelist = server.getPluginCommand("ignorelist");
        PluginCommand toggleWhispering = server.getPluginCommand("togglewhispering");
        PluginCommand toggleChat = server.getPluginCommand("togglechat");
        PluginCommand main = server.getPluginCommand("nerochat");

        ignore.setExecutor(new HardIgnoreCommand(this));
        ignore.setTabCompleter(new HardIgnoreCommand(this));

        whisper.setExecutor(new WhisperCommand(this));
        whisper.setTabCompleter(new WhisperCommand(this));

        reply.setExecutor(new ReplyCommand(this));
        reply.setTabCompleter(new ReplyCommand(this));

        last.setExecutor(new LastCommand(this));
        last.setTabCompleter(new LastCommand(this));

        ignorelist.setExecutor(new IgnoreListCommand(this));
        ignorelist.setTabCompleter(new IgnoreListCommand(this));

        toggleWhispering.setExecutor(new ToggleWhisperingCommand(this));
        toggleWhispering.setTabCompleter(new ToggleWhisperingCommand(this));

        toggleChat.setExecutor(new ToggleChatCommand(this));
        toggleChat.setTabCompleter(new ToggleChatCommand(this));

        main.setExecutor(new MainCommand(this));
        main.setTabCompleter(new MainCommand(this));

        logger.info("Register Events!");
        server.getPluginManager().registerEvents(new ChatEvent(this), this);
        server.getPluginManager().registerEvents(new QuitEvent(this), this);

        if (NeroChat.getConfiguration().getBoolean("Main.bstats-metrics", true)) {
            logger.info("Loading metrics");
            new Metrics(this, 18215);
        } else {
            logger.info("Metrics are disabled in the config");
        }
        reloadConfiguration();
        logger.info("The plugin is ready to work!");
    }

    public void reloadNeroChat() {
        reloadLang();
        reloadConfiguration();
    }

    // Because the config handler is used wrongly in various methods, this change will break
    // the plugin until its correctly used. Its called config CACHE for a reason
    public void reloadConfiguration() {
        configCache = new ConfigCache();
        NeroChatModule.reloadModules();
        configCache.saveConfig();
    }

    private void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(this.getDataFolder() + "/lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                logger.info("Found language file for " + localeString);
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if (!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        logger.info("Found language file for " + localeString);
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles() {
        Set<String> languageFiles = new HashSet<>();
        try (JarFile jarFile = new JarFile(this.getFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String path = entries.nextElement().getName();
                if (path.startsWith("lang/") && path.endsWith(".yml"))
                    languageFiles.add(path);
            }
        } catch (IOException e) {
            logger.severe("Error while getting default language file names! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return languageFiles;
    }

    public static LanguageCache getLang(String lang) {
        if (configCache.auto_lang) {
            return languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(configCache.default_lang));
        } else {
            return languageCacheMap.get(configCache.default_lang);
        }
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player ? getLang(((Player) commandSender).getLocale()) : getLang(configCache.default_lang);
    }

    public static ConfigCache getConfiguration() {
        return configCache;
    }
    public static Logger getLog() {
        return logger;
    }
}