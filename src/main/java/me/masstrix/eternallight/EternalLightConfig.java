package me.masstrix.eternallight;

import me.masstrix.eternallight.handle.DisplayMethod;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class EternalLightConfig {

    private final int MAX_RENDER = 20;
    private EternalLight plugin;
    private int rad;
    private DisplayMethod type;
    private boolean updates = true;

    void init(EternalLight plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        setRadius(config.contains("radius") ? config.getInt("radius") : 5);
        config.set("radius", rad);
        plugin.saveConfig();
        type = DisplayMethod.find(config.getString("display-type"));
        String update = "general.update-notifications";
        updates = !config.contains(update) || config.getBoolean(update);
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

    public void setRadius(int rad) {
        this.rad = rad > MAX_RENDER ? MAX_RENDER : rad < 1 ? 1 : rad;
        plugin.getConfig().set("radius", this.rad);
    }

    public int getRadius() {
        return rad;
    }

    public DisplayMethod getDisplayType() {
        return type;
    }

    public boolean isUpdateNotifications() {
        return updates;
    }
}
