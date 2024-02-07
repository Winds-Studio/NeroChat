package me.softik.nerochat.commands.whisper;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.commands.NeroChatCommand;
import me.softik.nerochat.tools.CommonTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WhisperCommand implements NeroChatCommand {

    private final NeroChat plugin;

    @Override
    public String label() {
        return "whisper";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).distinct().collect(Collectors.toList());
        } else {
            return NO_COMPLETIONS;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(NeroChat.getLang(sender).usage + " /"+label()+" "+ NeroChat.getLang(sender).player_argument + " " + NeroChat.getLang(sender).message_argument);
            return false;
        }

        Optional<Player> receiver = CommonTool.getPlayer(args[0]);

        if (!receiver.isPresent()) {
            sender.sendMessage(NeroChat.getLang(sender).not_online);
            return true;
        }

        if (NeroChat.getIgnoreTool().isIgnored(sender, receiver.get())) {
            sender.sendMessage(CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_me);
        } else if (NeroChat.getIgnoreTool().isIgnored(receiver.get(), sender)) {
            sender.sendMessage(CommonTool.getPrefix() + NeroChat.getLang(sender).ignore_you);
        } else {
            CommonTool.sendWhisperTo(sender, CommonTool.mergeArgs(args, 1), receiver.get());
        }

        return true;
    }
}
