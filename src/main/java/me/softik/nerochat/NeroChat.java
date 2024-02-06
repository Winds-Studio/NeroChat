package me.softik.nerochat;

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
import me.softik.nerochat.config.Config;
import me.softik.nerochat.config.LanguageCache;
import me.softik.nerochat.events.ChatEvent;
import me.softik.nerochat.events.QuitEvent;
import me.softik.nerochat.modules.NeroChatModule;
import me.softik.nerochat.tools.*;
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

    private final TempDataTool tempDataTool = new TempDataTool();
    private final SoftIgnoreTool softignoreTool = new SoftIgnoreTool();
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
        Server server = getServer();
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

        if (config.bstats_metrics) {
            logger.info("Enabling metrics");
            new Metrics(this, 18215);
        } else {
            logger.info("Not enabling metrics");
        }

        logger.info("ready!");
    }

    public void reloadNeroChat() {
        reloadLang();
        reloadConfiguration();
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