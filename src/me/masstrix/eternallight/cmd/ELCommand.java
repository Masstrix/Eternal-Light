package me.masstrix.eternallight.cmd;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.handle.SpawnValue;
import me.masstrix.eternallight.util.EternalCommand;
import me.masstrix.eternallight.util.VersionChecker;

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
            if (plugin.getVersionMeta().getState() == VersionChecker.VersionState.BEHIND) {
                msg(" &bA newer version is available. " +
                        "Update now to get new features and bug patch's.");
                msg("");
            }
            msg("&e/" + getLabelUsed() + " reload &7-&f Reloads the plugins config file.");
            msg("&e/" + getLabelUsed() + " reloadMappings &7-&f Reloads and applies the mappings.");
            msg("&e/" + getLabelUsed() + " version &7-&f Checks for any updates.");
            msg("&e/" + getLabelUsed() + " renderdistance <distance> &7-&f Changes the render distance.");
            msg("");
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.getPluginConfig().reload();
                plugin.getProjector().start();
                msg(plugin.getPluginConfig().getPrefix() + "&aReloaded config.");
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
                    msg(plugin.getPluginConfig().getPrefix() + "set render distance to " + plugin.getPluginConfig().getRadius());
                }
                return;
            }
            else if (args[0].equalsIgnoreCase("reloadMappings")) {
                boolean success = SpawnValue.loadMappings(plugin);
                if (success)
                    msg(plugin.getPluginConfig().getPrefix() + "&aReloaded mappings.");
                else msg(plugin.getPluginConfig().getPrefix() + "&cThere was an error while reloading the mappings. " +
                        "Please make sure your mappings are valid.");
                return;
            }
            else if (args[0].equalsIgnoreCase("version")) {
                msg(plugin.getPluginConfig().getPrefix() + "&7Checking version...");
                VersionChecker.VersionMeta meta = plugin.getVersionMeta();
                if (meta == null) {
                    msg(plugin.getPluginConfig().getPrefix() + "&cUnable to check version.");
                    return;
                }
                String msg = "&cUnexpected error.";
                switch (meta.getState()) {
                    case BEHIND: {
                        msg = "&cYou are running an outdated version.";
                        break;
                    }
                    case UNKNOWN: {
                        msg = "Latest version is unknown.";
                        break;
                    }
                    case LATEST: {
                        msg = "&aUp to date! &7You are running the latest version.";
                        break;
                    }
                    case DEV_BUILD: {
                        msg = "&6Dev Build! Expect bugs, this version is not officially released yet.";
                        break;
                    }
                }
                msg(plugin.getPluginConfig().getPrefix() + msg);
                return;
            }
            msg(plugin.getPluginConfig().getPrefix() + "&cUnknown command. Use /" + getLabelUsed() + " for help.");
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "version", "renderDistance", "reloadMappings");
        }
        return Collections.emptyList();
    }
}
