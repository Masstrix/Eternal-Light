package net.axeora.eternallight;

import net.axeora.eternallight.handle.DisplayMethod;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class EternalLightConfig {

    private int rad;
    private DisplayMethod type;
    private boolean updates = true;

    protected void init(EternalLight plugin) {
        FileConfiguration config = plugin.getConfig();
        rad = config.contains("radius") ? config.getInt("radius") : 5;
        if (rad > 12) rad = 12;
        if (rad <= 0) rad = 1;
        config.set("radius", rad);
        plugin.saveConfig();
        type = config.contains("display-type") ? DisplayMethod.find(config.getString("display-type"))
                : DisplayMethod.NORMAL;
        String update = "general.update-notifications";
        updates = !config.contains(update) || config.getBoolean(update);
    }

    public void reload() {
        EternalLight plugin = EternalLight.getInstance();
        File file = new File(plugin.getDataFolder() + "/config.yml");
        if (!file.exists()) plugin.saveDefaultConfig();
        plugin.reloadConfig();

        init(plugin);
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
