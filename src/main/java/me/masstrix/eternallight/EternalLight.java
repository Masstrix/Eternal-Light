package me.masstrix.eternallight;

import me.masstrix.eternallight.cmd.LightCommand;
import me.masstrix.eternallight.handle.Projector;
import me.masstrix.eternallight.handle.SpawnValue;
import me.masstrix.eternallight.listener.PlayerConnectionListener;
import me.masstrix.eternallight.listener.PlayerMoveListener;
import me.masstrix.eternallight.metric.Metrics;
import me.masstrix.eternallight.util.*;
import me.masstrix.eternallight.cmd.ELCommand;
import me.masstrix.eternallight.version.MinecraftRelease;
import me.masstrix.eternallight.version.MinecraftVersion;
import me.masstrix.eternallight.version.checker.CheckerApi;
import me.masstrix.eternallight.version.checker.VersionCheckInfo;
import me.masstrix.eternallight.version.checker.VersionChecker;
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
    private VersionCheckInfo versionInfo;
    private VersionChecker checker;

    /**
     * @return the native version for this plugin to run on.
     */
    public static MinecraftVersion getNativeVersion() {
        return NATIVE;
    }

    @Override
    public void onEnable() {
        MinecraftVersion sv = MinecraftRelease.getServerVersion();
        if (sv.isBehind(NATIVE)) {
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

        new Metrics(this);

        checker = new VersionChecker(RESOURCE_ID, version);
        checker.useApi(CheckerApi.SPIGOT_LEGACY);
        checkVersion();
    }

    /**
     * @return the version checker for this plugin.
     */
    public VersionChecker getVersionChecker() {
        return this.checker;
    }

    /**
     * Checks the plugins version.
     */
    private void checkVersion() {
        checker.run(info -> {
            if (info.isUnknown()) {
                getLogger().log(Level.WARNING, "Failed to check plugin version. Are you running offline?");
            }
            else if (info.isDev()) {
                getLogger().log(Level.WARNING, "You are running a development build.");
            }
            else if (info.isLatest()) {
                getLogger().log(Level.INFO, "Plugin is up to date.");
            }
            else if (info.isBehind()) {
                getLogger().log(Level.INFO, "There is a new version available. ");
            }
            versionInfo = info;
        });
    }

    /**
     * Checks if here is a newer version available.
     */
    private void checkVersion(int attempts) {
//        new VersionChecker(EternalLight.RESOURCE_ID, version)
//                .useApi(VersionChecker.ApiUsage.SPIGOT_LEGACY)
//                .run(new VersionChecker.VersionCallback() {
//            @Override
//            public void done(VersionChecker.VersionMeta s) {
//                ConsoleCommandSender sender = Bukkit.getConsoleSender();
//                if (s.getState() == VersionChecker.VersionState.UNKNOWN) {
//                    getLogger().log(Level.WARNING, "Failed to check plugin version. Are you running offline?");
//                }
//                else if (s.getState() == VersionChecker.VersionState.DEV_BUILD) {
//                    sender.sendMessage(StringUtil.color("[EternalLight] \u00A7cYou are using a development build! Expect bugs."));
//                }
//                else if (s.getState() == VersionChecker.VersionState.BEHIND) {
//                    sender.sendMessage(StringUtil.color(""));
//                    sender.sendMessage(StringUtil.color("&e New update available for "  + getName()));
//                    sender.sendMessage(StringUtil.color(" Current version: &e" + version));
//                    sender.sendMessage(StringUtil.color(" Latest version: &e" + s.getLatestVersion()));
//                    sender.sendMessage(StringUtil.color(""));
//                } else if (s.getState() == VersionChecker.VersionState.LATEST)  {
//                    getLogger().log(Level.INFO, "You are running the latest available version.");
//                }
//                versionMeta = s;
//            }
//
//            @Override
//            public void onTimeout() {
//                getLogger().log(Level.WARNING, "Connection timed out. Could not check version.");
//            }
//
//            @Override
//            public void onError(String msg) {
//                getLogger().log(Level.WARNING, "Failed to read version data: " +  msg);
//                if (msg.equals("SSLException")) {
//                    getLogger().log(Level.INFO, "This error may be caused from the version of Java you re running.");
//                }
//                if (attempts < 3) {
//                    getLogger().log(Level.WARNING, "Re Attempting to check version...");
//                    checkVersion(attempts + 1);
//                } else {
//                    getLogger().log(Level.WARNING, "Failed to check version after 3 attempts.");
//                }
//            }
//        });
    }

    /**
     * @return the plugins version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns if the server is running a version before light levels were
     * changed to use complete darkness for mobs to spawn.
     *
     * @return if the server is a legacy version for light levels.
     */
    public boolean isLegacyServer() {
        return MinecraftRelease.getServerVersion().isBehind(new MinecraftVersion("1.18"));
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
    public VersionCheckInfo getUpdateInfo() {
        return versionInfo;
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
