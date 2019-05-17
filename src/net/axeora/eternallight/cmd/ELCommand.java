package net.axeora.eternallight.cmd;

import net.axeora.eternallight.EternalLight;
import net.axeora.eternallight.PluginData;
import net.axeora.eternallight.util.EternalCommand;
import net.axeora.eternallight.util.VersionChecker;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ELCommand extends EternalCommand {

    public ELCommand() {
        super("eternallight");
        setAliases(Collections.singletonList("el"));
        setDescription("");
        setPermission("eternallight.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        // Check permissions
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length == 0) {
            msg(sender, "");
            msg(sender, "   &e&lEternal Light &7" + PluginData.VERSION);
            msg(sender, " &bA newer version is available. " +
                    "Update now to get new features and bug patch's.");
            msg(sender, "&e/" + label + " reload &7-&f Reloads the plugins config file.");
            msg(sender, "&e/" + label + " version &7-&f Checks for any updates.");
            msg(sender, "");
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                EternalLight.getInstance().getPluginConfig().reload();
                msg(sender, PluginData.PREFIX + "&7Reloaded config.");
                return false;
            }
            if (args[0].equalsIgnoreCase("version")) {
                msg(sender, PluginData.PREFIX + "&7Checking version...");
                VersionChecker checker = new VersionChecker(PluginData.RESOURCE_ID, PluginData.VERSION);
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
                    if (sender != null) {
                        msg(sender, PluginData.PREFIX + message);
                    }
                });
                return false;
            }
            msg(sender, PluginData.PREFIX + "&cUnknown command. Use /" + label + " for help.");
        }
        return false;
    }
}
