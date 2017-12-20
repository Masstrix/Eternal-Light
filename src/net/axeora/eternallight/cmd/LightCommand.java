package net.axeora.eternallight.cmd;

import net.axeora.eternallight.EternalLight;
import net.axeora.eternallight.PluginData;
import net.axeora.eternallight.handle.LightVisual;
import net.axeora.eternallight.handle.Projector;
import net.axeora.eternallight.util.EternalCommand;
import net.axeora.eternallight.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class LightCommand extends EternalCommand {

    public LightCommand() {
        super("lightlevels");
        setAliases(Arrays.asList("showlight", "ll"));
        setDescription("toggle the light levels projector to show or high the light levels of blocks.");
        setPermission("eternallight.use");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            Bukkit.getConsoleSender().sendMessage(StringUtil.color(
                    "[" + PluginData.NAME + "] &cOnly players can use this command!"));
            return false;
        }
        Player player = (Player) sender;
        Projector projector = EternalLight.getInstance().getProjector();
        if (!projector.contains(player.getUniqueId())) projector.add(player);
        if (args.length > 0 && args[0].equalsIgnoreCase("mode")) {
            LightVisual visual = projector.getVisual(player);
            visual.toggleType();
            msg(sender, PluginData.PREFIX + "Toggled display mode to " + visual.getType());
            return false;
        }
        boolean wasShown = projector.getVisual(player).toggle();
        player.sendMessage(StringUtil.color(String.format(
                PluginData.PREFIX + "%s light level projector.%s",
                wasShown ? "Disabled" : "Enabled", wasShown ? ""
                        : " &7Use /" + s + " mode to toggle between display modes.")));
        return false;
    }
}
