package me.masstrix.eternallight.cmd;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.handle.SpawnValue;
import me.masstrix.eternallight.util.EternalCommand;
import me.masstrix.eternallight.version.checker.VersionCheckInfo;

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
            VersionCheckInfo vm = plugin.getUpdateInfo();
            if (vm != null && vm.isBehind()) {
                msg(" &bA newer version is available. " +
                        "It's recommended to update if possible.");
                msg("");
            }
            msg("&e/" + getLabelUsed() + " reload &7-&f Reloads the plugins config file.");
            msg("&e/" + getLabelUsed() + " reloadMappings &7-&f Reloads and applies the mappings.");
            msg("&e/" + getLabelUsed() + " resetMappings &7-&f Resets the mappings to default.");
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
                    plugin.getPluginConfig().setScannerRadius(Integer.parseInt(str));
                    plugin.getPluginConfig().save();
                    msg(plugin.getPluginConfig().getPrefix() + "set render distance to " + plugin.getPluginConfig().getScannerRadius());
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
            else if (args[0].equalsIgnoreCase("resetMappings")) {
                msg(plugin.getPluginConfig().getPrefix() + "&7Resetting material mappings...");
                boolean success = SpawnValue.resetMappings(plugin);
                if (success)
                    msg(plugin.getPluginConfig().getPrefix() + "&aReset mappings to default.");
                else msg(plugin.getPluginConfig().getPrefix() + "&cThere was an error while resetting the mappings.");
                return;
            }
            else if (args[0].equalsIgnoreCase("version")) {
                msg(plugin.getPluginConfig().getPrefix() + "&7Current Version: &f" + plugin.getVersion());
                msg(plugin.getPluginConfig().getPrefix() + "&7Checking for updates...");
                plugin.getVersionChecker().run(info -> {
                    String msg = "&cFailed to check version.";
                    if (info.isUnknown()) {
                        msg = "Unable to check version.";
                    }
                    else if (info.isDev()) {
                        msg = "&6Dev Build! Expect bugs, this version is not officially released yet.";
                    }
                    else if (info.isLatest()) {
                        msg = "&aUp to date! &7You are running the latest version.";
                    }
                    else if (info.isBehind()) {
                        msg = "&cYou are running an outdated version.";
                    }
                    msg(plugin.getPluginConfig().getPrefix() + msg);
                });
                return;
            }
            msg(plugin.getPluginConfig().getPrefix() + "&cUnknown command. Use /" + getLabelUsed() + " for help.");
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "version", "renderDistance", "reloadMappings", "resetMappings");
        }
        return Collections.emptyList();
    }
}
