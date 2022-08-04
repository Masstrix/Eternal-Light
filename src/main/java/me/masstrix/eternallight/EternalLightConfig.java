package me.masstrix.eternallight;

import me.masstrix.eternallight.handle.DisplayMethod;
import me.masstrix.eternallight.handle.SpawnConditions;
import me.masstrix.eternallight.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EternalLightConfig {

    // Hardcoded max, stops people using way to high of values.
    private static final int RAD_MAX = 20;

    private EternalLight plugin;
    private int scanRad, scanHeight, updateRate;
    private DisplayMethod defMethod;
    private boolean updates = true;
    private boolean usePrefix;
    private boolean messagesEnabled;
    private boolean spherical;

    private Map<ConfigMessage, String> messages = new HashMap<>();

    void init(EternalLight plugin) {
        this.plugin = plugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        FileConfiguration config = plugin.getConfig();

        spherical = config.getBoolean("scanner.spherical", true);
        scanRad = config.getInt("scanner.radius", 5);
        scanHeight = config.getInt("scanner.height", 4);
        if (scanRad > RAD_MAX) scanRad = RAD_MAX;
        if (scanHeight > RAD_MAX) scanRad = RAD_MAX;
        defMethod = DisplayMethod.find(config.getString("default-mode", DisplayMethod.SPAWNABLE.name()));
        updates = config.getBoolean("update-notifications", true);
        usePrefix = config.getBoolean("messages.use-prefix", true);
        messagesEnabled = config.getBoolean("messages.enabled", true);
        updateRate = config.getInt("update-rate", 20);

        // Get all the messages
        ConfigurationSection messageSection = config.getConfigurationSection("messages");
        if (messageSection != null) {
            for (ConfigMessage message : ConfigMessage.values()) {
                this.messages.put(message, messageSection.getString(message.getIndex(), message.getDefault()));
            }
        }
    }

    public void reload() {
        File file = new File(plugin.getDataFolder() + "/config.yml");
        if (!file.exists()) plugin.saveDefaultConfig();
        plugin.reloadConfig();
        init(plugin);

        // Reload spawn conditions
        SpawnConditions.reload();
    }

    /**
     * Saves the config.
     */
    public void save() {
        plugin.saveConfig();
    }

    /**
     * @return if the prefix should be used.
     */
    public boolean usePrefix() {
        return usePrefix;
    }

    public int getProjectorUpdateRate() {
        return updateRate;
    }

    /**
     * Sets the radius of the projector.
     *
     * @param rad new radius of projector.
     */
    public void setScannerRadius(int rad) {
        this.scanRad = rad > RAD_MAX ? RAD_MAX : Math.max(rad, 1);
        plugin.getConfig().set("scanner..radius", this.scanRad);
    }

    /**
     * @return the display radius to show light levels.
     */
    public int getScannerRadius() {
        return scanRad;
    }

    /**
     * Sets the radius of the projector.
     *
     * @param rad new radius of projector.
     */
    public void setScannerHeight(int rad) {
        this.scanHeight = rad > RAD_MAX ? RAD_MAX : Math.max(rad, 1);
        plugin.getConfig().set("scanner.height", this.scanHeight);
    }

    /**
     * @return the display radius to show light levels.
     */
    public int getScannerHeight() {
        return scanHeight;
    }

    /**
     * @return if the scanner should be spherical.
     */
    public boolean isScannerSpherical() {
        return spherical;
    }

    /**
     * @return the the default mode for players.
     */
    public DisplayMethod getDefaultMode() {
        return defMethod;
    }

    /**
     * @return if messages are enabled.
     */
    public boolean isMessagesEnabled() {
        return messagesEnabled;
    }

    /**
     * @return if update notifications should be sent to admins and operators.
     */
    public boolean sendUpdateNotifications() {
        return updates;
    }

    /**
     * @return the prefix or an empty string if prefix is disabled.
     */
    public String getPrefix() {
        return usePrefix ? getMessage(ConfigMessage.PREFIX) : "";
    }

    /**
     * Returns a message from the config.
     *
     * @param message message to get.
     * @return the message.
     */
    public String getMessage(ConfigMessage message) {
        return StringUtil.color(messages.get(message));
    }

    /**
     * All messages in the config.
     */
    public enum ConfigMessage {
        PREFIX("prefix", "&e&l[EternalLight] &f"),
        ENABLE("activate", "&aEnabled&f the light level projector. &7Use /ll mode to toggle between display modes."),
        DISABLE("deactivate", "&cDisabled&f light level projector."),
        CHANGE_MODE("change-mode", "Set display mode to &7%mode%"),
        INVALID_MODE("invalid-mode", "&cInvalid display mode!"),
        NO_PERMISSION("no-permission", "&cYou do not have permission to do this!");

        private String index;
        private String def;

        ConfigMessage(String index, String def) {
            this.index = index;
            this.def = def;
        }

        /**
         * @return the index value used in the config.
         */
        public String getIndex() {
            return index;
        }

        /**
         * @return the default value for this message.
         */
        public String getDefault() {
            return def;
        }
    }
}
