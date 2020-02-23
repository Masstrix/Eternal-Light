package me.masstrix.eternallight.cmd;

import me.masstrix.eternallight.EternalLightConfig;
import me.masstrix.eternallight.handle.DisplayMethod;
import me.masstrix.eternallight.handle.LightVisual;
import me.masstrix.eternallight.handle.Projector;
import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.util.EternalCommand;
import me.masstrix.eternallight.util.Perm;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LightCommand extends EternalCommand {

    private EternalLight plugin;

    public LightCommand(EternalLight plugin) {
        super("lightlevels");
        this.plugin = plugin;
    }

    @Override
    public void execute(String[] args) {
        if (!wasPlayer()) {
            plugin.getLogger().info("Only players can use this command!");
            return;
        }
        Player player = (Player) getSender();
        Projector projector = plugin.getProjector();
        EternalLightConfig config = plugin.getPluginConfig();

        // Check if player has permission to use command
        if (!player.hasPermission(Perm.USE)) {
            msg(config.getMessage(EternalLightConfig.ConfigMessage.NO_PERMISSION));
            return;
        }

        if (!projector.contains(player.getUniqueId())) projector.add(player);
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help") && player.hasPermission(Perm.MODE)
                    && config.isMessagesEnabled()) {
                msg("");
                msg("&e/lightlevels &7-&f Toggle light indicators.");
                msg("&e/lightlevels mode [mode] &7-&f Change display mode.");
                msg("");
            }
            else if (args[0].equalsIgnoreCase("mode")) {
                if (!player.hasPermission(Perm.MODE)) {
                    msg(player, config.getPrefix() + config.getMessage(EternalLightConfig.ConfigMessage.NO_PERMISSION));
                    return;
                }
                LightVisual visual = projector.getVisual(player);
                if (args.length > 1) {
                    DisplayMethod method = DisplayMethod.find(args[1]);
                    if (method == null) {
                        msg(config.getPrefix() + config.getMessage(EternalLightConfig.ConfigMessage.INVALID_MODE));
                        return;
                    }
                    visual.setMethod(method);
                } else {
                    visual.toggleType();
                }
                if (config.isMessagesEnabled())
                    msg(config.getPrefix() + config.getMessage(EternalLightConfig.ConfigMessage.CHANGE_MODE)
                            .replaceAll("%mode%", visual.getMethod().toString()));
            }
            return;
        }
        if (config.isMessagesEnabled()) {
            boolean wasShown = projector.getVisual(player).toggle();
            String msg;
            if (wasShown) msg = config.getMessage(EternalLightConfig.ConfigMessage.DISABLE);
            else msg = config.getMessage(EternalLightConfig.ConfigMessage.ENABLE);
            msg(config.getPrefix() + msg);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Perm.MODE)) return Collections.emptyList();
        if (args.length == 1) {
            return Arrays.asList("mode", "help");
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {
            return DisplayMethod.getOptions();
        }
        return Collections.emptyList();
    }
}
