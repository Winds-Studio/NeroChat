package me.softik.nerochat.tools;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.models.UniqueSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConfigTool {
    private final NeroChat plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public ConfigTool(NeroChat plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");

        loadData();
    }

    public HardReturn hardIgnorePlayer(Player player, Player ignored) {
        List<String> list = dataConfig.getStringList(player.getUniqueId().toString());

        if (list.contains(ignored.getUniqueId().toString())) {
            list.remove(ignored.getUniqueId().toString());

            dataConfig.set(player.getUniqueId().toString(), list);

            saveData();

            return HardReturn.UN_IGNORE;
        } else {
            list.add(ignored.getUniqueId().toString());

            dataConfig.set(player.getUniqueId().toString(), list);

            saveData();

            return HardReturn.IGNORE;
        }
    }

    protected boolean isHardIgnored(CommandSender chatter, CommandSender receiver) {
        return dataConfig.getStringList(new UniqueSender(receiver).getUniqueId().toString()).contains(new UniqueSender(chatter).getUniqueId().toString());
    }

    protected List<OfflinePlayer> getHardIgnoredPlayers(Player player) {
        return dataConfig.getStringList(player.getUniqueId().toString())
                .stream()
                .map(uuid -> plugin.getServer().getOfflinePlayer(UUID.fromString(uuid)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void loadData() {
        generateFile();

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveData() {
        generateFile();

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateFile() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdir())
            return;

        if (!dataFile.exists()) {
            try {
                if (!dataFile.createNewFile())
                    throw new IOException("Couldn't create file " + dataFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum HardReturn {
        IGNORE, UN_IGNORE
    }
}
