package me.softik.nerochat.commands.whisper;

import lombok.RequiredArgsConstructor;
import me.softik.nerochat.NeroChat;
import me.softik.nerochat.utils.CommonTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WhisperCommand implements CommandExecutor, TabExecutor {
    private final NeroChat plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Optional<Player> receiver = CommonTool.getPlayer(args[0]);

            if (receiver.isPresent()) {
                if (plugin.getIgnoreTool().isIgnored(sender, receiver.get())) {
                    sender.sendMessage(CommonTool.getPrefix() + "This person ignores you!");
                } else if (plugin.getIgnoreTool().isIgnored(receiver.get(), sender)) {
                    sender.sendMessage(CommonTool.getPrefix() + "You ignore this person!");
                } else {
                    if (args.length > 1) {
                        CommonTool.sendWhisperTo(sender, CommonTool.mergeArgs(args, 1), receiver.get());
                    } else {
                        return false;
                    }
                }
            } else {
                sender.sendMessage("notonline");
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
