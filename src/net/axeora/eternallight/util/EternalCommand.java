package net.axeora.eternallight.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class EternalCommand extends Command {

    public EternalCommand(String name) {
        super(name);
    }

    public void msg(CommandSender sender, String msg) {
        sender.sendMessage(StringUtil.color(msg));
    }
}
