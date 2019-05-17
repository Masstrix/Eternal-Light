package net.axeora.eternallight;

import net.axeora.eternallight.cmd.ELCommand;
import net.axeora.eternallight.cmd.LightCommand;
import net.axeora.eternallight.handle.Projector;
import net.axeora.eternallight.listener.PlayerConnectionListener;
import net.axeora.eternallight.listener.PlayerMoveListener;
import net.axeora.eternallight.util.ReflectionUtil;
import net.axeora.eternallight.util.StringUtil;
import net.axeora.eternallight.util.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;

public class EternalLight extends JavaPlugin {

    private static EternalLight instance;
    private Projector projector;
    private EternalLightConfig config;
    private VersionChecker.VersionMeta versionMeta;
    private EternalLightAPI api;

    public static EternalLight getInstance() {
        return instance;
    }

    /**
     * @return the plugins API.
     */
    public EternalLightAPI getAPI() {
        return api;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (isLegacy()) {
            getLogger().info("Detected legacy version. Disabling plugin!");
            this.setEnabled(false);
        }

        if (this.isEnabled()) {
            api = new EternalLightAPI();
            projector = new Projector();
            Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
            registerCommand(new LightCommand());
            registerCommand(new ELCommand());

            File file = new File(getDataFolder() + "/config.yml");
            if (!file.exists()) {
                saveDefaultConfig();
                reloadConfig();
            }

            config = new EternalLightConfig();
            config.init(this);

            VersionChecker checker = new VersionChecker(PluginData.RESOURCE_ID, PluginData.VERSION);
            checker.run(s -> {
                if (s.getState() == VersionChecker.PluginVersionState.UNKNOWN) {
                    getLogger().log(Level.WARNING, "Failed to check plugin version. Are you running offline?");
                } else if (s.getState() == VersionChecker.PluginVersionState.DEV_BUILD) {
                    ConsoleCommandSender sender = Bukkit.getConsoleSender();
                    sender.sendMessage(StringUtil.color("[EternalLight] \u00A7cYou are using a development build! Expect bugs."));
                } else if (s.getState() == VersionChecker.PluginVersionState.BEHIND) {
                    ConsoleCommandSender sender = Bukkit.getConsoleSender();
                    sender.sendMessage(StringUtil.color(""));
                    sender.sendMessage(StringUtil.color("&e New update available for " + PluginData.NAME));
                    sender.sendMessage(StringUtil.color(" Current version: &e" + PluginData.VERSION));
                    sender.sendMessage(StringUtil.color(" Latest version: &e" + s.getLatestVersion()));
                    sender.sendMessage(StringUtil.color(""));
                }
                this.versionMeta = s;
            });
        }
    }

    /**
     * @return if the server version is 1.8 or below.
     */
    public boolean isLegacy() {
        byte[] ver = ReflectionUtil.getVersionUnsafe();
        return ver.length > 1 && ver[1] < Integer.parseInt(PluginData.MIN_SUPPORTED_VERSION.split("\\.")[1]);
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

    /**
     * Register a single or list of commands. This saves you from having to
     * use the plugin.yml to register each command.
     *
     * @param commands what command(s) to register.
     */
    public static void registerCommand(Command... commands) {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            CommandMap map = (CommandMap) f.get(Bukkit.getServer());
            for (Command cmd : commands) {
                map.register(PluginData.NAME, cmd);
                map.getCommand(cmd.getName()).setAliases(cmd.getAliases());
                map.getCommand(cmd.getName()).setDescription(cmd.getDescription());
                map.getCommand(cmd.getName()).setUsage(cmd.getUsage());
                map.getCommand(cmd.getName()).setPermission(cmd.getPermission());
                map.getCommand(cmd.getName()).setPermissionMessage(cmd.getPermissionMessage());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
