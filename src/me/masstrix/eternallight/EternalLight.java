package me.masstrix.eternallight;

import me.masstrix.eternallight.cmd.LightCommand;
import me.masstrix.eternallight.handle.Projector;
import me.masstrix.eternallight.listener.PlayerConnectionListener;
import me.masstrix.eternallight.listener.PlayerMoveListener;
import me.masstrix.eternallight.util.*;
import me.masstrix.eternallight.cmd.ELCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public class EternalLight extends JavaPlugin {

    private static final MinecraftVersion NATIVE = new MinecraftVersion("1.14.4");

    private String version;
    private Projector projector;
    private EternalLightConfig config;
    private VersionChecker.VersionMeta versionMeta;

    public static MinecraftVersion getNativeVersion() {
        return NATIVE;
    }

    @Override
    public void onEnable() {
        if (isLegacy()) getLogger().info("Detected legacy version. Using legacy methods to support your version.");
        version = getDescription().getVersion();

        projector = new Projector(this);
        registerListener(new PlayerMoveListener(this), new PlayerConnectionListener(this));
        registerCommands(new ELCommand(this), new LightCommand(this));

        config = new EternalLightConfig();
        config.init(this);

        VersionChecker checker = new VersionChecker(PluginData.RESOURCE_ID, version);
        checker.run(s -> {
            if (s.getState() == VersionChecker.PluginVersionState.UNKNOWN) {
                getLogger().log(Level.WARNING, "Failed to check plugin version. Are you running offline?");
            }
            else if (s.getState() == VersionChecker.PluginVersionState.DEV_BUILD) {
                ConsoleCommandSender sender = Bukkit.getConsoleSender();
                sender.sendMessage(StringUtil.color("[EternalLight] \u00A7cYou are using a development build! Expect bugs."));
            }
            else if (s.getState() == VersionChecker.PluginVersionState.BEHIND) {
                ConsoleCommandSender sender = Bukkit.getConsoleSender();
                sender.sendMessage(StringUtil.color(""));
                sender.sendMessage(StringUtil.color("&e New update available for " + PluginData.NAME));
                sender.sendMessage(StringUtil.color(" Current version: &e" + version));
                sender.sendMessage(StringUtil.color(" Latest version: &e" + s.getLatestVersion()));
                sender.sendMessage(StringUtil.color(""));
            }
            this.versionMeta = s;
        });
    }

    /**
     * @return the plugins version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return if the server version is 1.8 or below.
     */
    private boolean isLegacy() {
        byte[] ver = ReflectionUtil.getVersionUnsafe();
        return ver.length > 1 && ver[1] <= 13;
    }

    public Projector getProjector() {
        return projector;
    }

    public EternalLightConfig getPluginConfig() {
        return config;
    }

    public VersionChecker.VersionMeta getVersionMeta() {
        return versionMeta;
    }

    private void registerCommands(EternalCommand... commands) {
        for (EternalCommand cmd : commands) {
            PluginCommand pc = Bukkit.getPluginCommand(cmd.getName());
            if (pc == null) continue;
            pc.setExecutor(cmd);
            pc.setTabCompleter(cmd);
        }
    }

    private void registerListener(Listener... listeners) {
        PluginManager manager = Bukkit.getPluginManager();
        for (Listener l : listeners)
            manager.registerEvents(l, this);
    }
}
