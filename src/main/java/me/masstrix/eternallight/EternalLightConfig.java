package me.masstrix.eternallight;

import me.masstrix.eternallight.handle.DisplayMethod;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class EternalLightConfig {

  private EternalLight plugin;
  private int rad;
  private DisplayMethod type;
  private boolean updates = true;

  void init(EternalLight plugin) {
    this.plugin = plugin;
    FileConfiguration config = plugin.getConfig();

    setRadius(config.getInt("radius", 5));

    String defaultDisplay = "general.default-display-type";
    type = DisplayMethod.find(config.getString(defaultDisplay, "ALL"));
    config.set(defaultDisplay, type.name());

    String update = "general.update-notifications";
    updates = config.getBoolean(update, true);
    config.set(update, updates);

    save();
  }

  public void reload() {
    File file = new File(plugin.getDataFolder() + "/config.yml");
    if (!file.exists()) plugin.saveDefaultConfig();
    plugin.reloadConfig();
    init(plugin);
  }

  public void save() {
    plugin.saveConfig();
  }

  public int getRadius() {
    return rad;
  }

  public void setRadius(int rad) {
    this.rad = rad > 12 || rad < 1 ? 5 : rad;
    plugin.getConfig().set("radius", this.rad);
    save();
  }

  public DisplayMethod getDefaultDisplayType() {
    return type;
  }

  public boolean isUpdateNotifications() {
    return updates;
  }
}
