package me.masstrix.eternallight;

import me.masstrix.eternallight.cmd.LightCommand;
import me.masstrix.eternallight.handle.Projector;
import me.masstrix.eternallight.handle.SpawnValue;
import me.masstrix.eternallight.listener.PlayerConnectionListener;
import me.masstrix.eternallight.listener.PlayerMoveListener;
import me.masstrix.eternallight.util.*;
import me.masstrix.eternallight.cmd.ELCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class EternalLight extends JavaPlugin {

    public static final int RESOURCE_ID = 50961;
    private static final MinecraftVersion NATIVE = new MinecraftVersion("1.14.4");

    private String version;
    private Projector projector;
    private EternalLightConfig config;
    private VersionChecker.VersionMeta versionMeta;

    /**
     * @return the native version for this plugin to run on.
     */
    public static MinecraftVersion getNativeVersion() {
        return NATIVE;
    }

    @Override
    public void onEnable() {
        if (isLegacy()) {
            getLogger().warning("Unsupported version! This version of EternalLight does not support versions below 1.13.");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp() || p.hasPermission(Perm.ADMIN)) {
                    p.sendMessage(StringUtil.color("&c[EternalLight] &cUnsupported server version! " +
                            "Plugin is disabled. Versions supported are 1.13 or later."));
                }
            }
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        version = getDescription().getVersion();

        config = new EternalLightConfig();
        config.init(this);
        SpawnValue.loadMappings(this);

        projector = new Projector(this);
        registerListener(new PlayerMoveListener(this), new PlayerConnectionListener(this));
        registerCommands(new ELCommand(this), new LightCommand(this));

        // Check for updates.
        new VersionChecker(EternalLight.RESOURCE_ID, version).run(new VersionChecker.VersionCallback() {
            @Override
            public void done(VersionChecker.VersionMeta s) {
                ConsoleCommandSender sender = Bukkit.getConsoleSender();
                if (s.getState() == VersionChecker.VersionState.UNKNOWN) {
                    getLogger().log(Level.WARNING, "Failed to check plugin version. Are you running offline?");
                }
                else if (s.getState() == VersionChecker.VersionState.DEV_BUILD) {
                    sender.sendMessage(StringUtil.color("[EternalLight] \u00A7cYou are using a development build! Expect bugs."));
                }
                else if (s.getState() == VersionChecker.VersionState.BEHIND) {
                    sender.sendMessage(StringUtil.color(""));
                    sender.sendMessage(StringUtil.color("&e New update available for "  + getName()));
                    sender.sendMessage(StringUtil.color(" Current version: &e" + version));
                    sender.sendMessage(StringUtil.color(" Latest version: &e" + s.getLatestVersion()));
                    sender.sendMessage(StringUtil.color(""));
                }
                versionMeta = s;
            }

            @Override
            public void onTimeout() {
                getLogger().log(Level.WARNING, "Connection timed out. Could not check version.");
            }

            @Override
            public void onError() {
                getLogger().log(Level.WARNING, "Failed to read version data.");
            }
        });
    }

    /**
     * @return the plugins version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * If the plugin is legacy it will be disabled on startup.
     *
     * @return if the server version is older than 1.13.
     */
    private boolean isLegacy() {
        byte[] ver = ReflectionUtil.getVersionUnsafe();
        return ver.length > 1 && ver[1] < 13;
    }

    /**
     * @return the projector.
     */
    public Projector getProjector() {
        return projector;
    }

    /**
     * @return the plugins config.
     */
    public EternalLightConfig getPluginConfig() {
        return config;
    }

    /**
     * @return the plugins version.
     */
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
