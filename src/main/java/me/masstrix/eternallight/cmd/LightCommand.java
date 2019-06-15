package me.masstrix.eternallight.cmd;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.PluginData;
import me.masstrix.eternallight.handle.DisplayMethod;
import me.masstrix.eternallight.handle.LightVisual;
import me.masstrix.eternallight.handle.Projector;
import me.masstrix.eternallight.util.EternalCommand;
import me.masstrix.eternallight.util.StringUtil;
import org.bukkit.Bukkit;
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
      Bukkit.getConsoleSender().sendMessage(StringUtil.color(
              "[" + PluginData.NAME + "] &cOnly players can use this command!"));
      return;
    }
    Player player = (Player) getSender();
    Projector projector = plugin.getProjector();
    if (!projector.contains(player.getUniqueId())) projector.add(player);
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("help")) {
        msg("");
        msg("&e/lightlevels &7-&f Toggle light indicators.");
        msg("&e/lightlevels mode [mode] &7-&f Change display mode.");
        msg("");
      } else if (args[0].equalsIgnoreCase("mode")) {
        LightVisual visual = projector.getVisual(player);
        if (args.length > 1) {
          DisplayMethod method = DisplayMethod.find(args[1]);
          if (method == null) {
            msg(PluginData.PREFIX + "&cInvalid display mode!");
            return;
          }
          visual.setType(method);
        } else {
          visual.toggleType();
        }
        msg(PluginData.PREFIX + "Toggled display mode to " + visual.getType());
      }
      return;
    }
    boolean wasShown = projector.getVisual(player).toggle();
    msg(StringUtil.color(String.format(
            PluginData.PREFIX + "%s light level projector.%s",
            wasShown ? "Disabled" : "Enabled", wasShown ? ""
                    : " &7Use /" + getLabelUsed() + " mode to toggle between display modes.")));
  }

  @Override
  public List<String> tabComplete(String[] args) {
    if (args.length == 1) {
      return Arrays.asList("mode", "help");
    } else if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {
      return DisplayMethod.getOptions();
    }
    return Collections.emptyList();
  }
}
