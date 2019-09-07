package me.masstrix.eternallight.cmd;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.util.EternalCommand;
import me.masstrix.eternallight.util.VersionChecker;
import me.masstrix.eternallight.PluginData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ELCommand extends EternalCommand {

    private EternalLight plugin;

    public ELCommand(EternalLight plugin) {
        super("eternallight");
        this.plugin = plugin;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            msg("");
            msg("   &e&lEternal Light &7" + plugin.getVersion());
            if (plugin.getVersionMeta().getState() == VersionChecker.PluginVersionState.BEHIND) {
                msg(" &bA newer version is available. " +
                        "Update now to get new features and bug patch's.");
                msg("");
            }
            msg("&e/" + getLabelUsed() + " reload &7-&f Reloads the plugins config file.");
            msg("&e/" + getLabelUsed() + " version &7-&f Checks for any updates.");
            msg("&e/" + getLabelUsed() + " renderdistance <distance> &7-&f Changes the render distance.");
            msg("");
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.getPluginConfig().reload();
                msg(PluginData.PREFIX + "&7Reloaded config.");
                return;
            }
            else if (args[0].equalsIgnoreCase("renderdistance")) {
                if (args.length == 1) {
                    msg("&fUsage: &7/" + getLabelUsed() + " renderdistance &c<distance>");
                    return;
                }
                String str = args[1];
                if (str.matches("\\d.*")) {
                    plugin.getPluginConfig().setRadius(Integer.parseInt(str));
                    plugin.getPluginConfig().save();
                    msg(PluginData.PREFIX + "set render distance to " + plugin.getPluginConfig().getRadius());
                }
                return;
            }
            else if (args[0].equalsIgnoreCase("version")) {
                msg(PluginData.PREFIX + "&7Checking version...");
                VersionChecker checker = new VersionChecker(PluginData.RESOURCE_ID, plugin.getVersion());
                checker.run(s -> {
                    String message;
                    if (s.getState() == VersionChecker.PluginVersionState.UNKNOWN) {
                        message = "&cThere was an error in checking for updates.";
                    }
                    else if (s.getState() == VersionChecker.PluginVersionState.BEHIND) {
                        message = "&bYou are running behind. Update to get bug patch's and new features.";
                    } else if (s.getState() == VersionChecker.PluginVersionState.LATEST) {
                        message = "&aAll up to date!";
                    } else {
                        message = "&eYou are running a development build! Expect bugs.";
                    }
                    if (getSender() != null) {
                        msg(PluginData.PREFIX + message);
                    }
                });
                return;
            }
            msg(PluginData.PREFIX + "&cUnknown command. Use /" + getLabelUsed() + " for help.");
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "version", "renderdistance");
        }
        return Collections.emptyList();
    }
}
