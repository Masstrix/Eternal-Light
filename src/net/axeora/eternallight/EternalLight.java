package net.axeora.eternallight;

import net.axeora.eternallight.cmd.ELCommand;
import net.axeora.eternallight.cmd.LightCommand;
import net.axeora.eternallight.handle.Projector;
import net.axeora.eternallight.listener.PlayerConnectionListener;
import net.axeora.eternallight.listener.PlayerMoveListener;
import net.axeora.eternallight.util.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public class EternalLight extends JavaPlugin {

    private static EternalLight instance;
    private Projector projector;
    private EternalLightConfig config;
    private VersionChecker.VersionMeta versionMeta;

    public static EternalLight getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
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

        VersionChecker checker = new VersionChecker(25618, "1.2");
        checker.run(s -> {
            System.out.println(" ");
            System.out.println(" Current: " + Arrays.toString(s.getCurrent()));
            System.out.println(" Latest: " + Arrays.toString(s.getLatest()));
            System.out.println(" State: " + s.getState());
            System.out.println(" ");
            this.versionMeta = s;
        });
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
                map.register("MassCore", cmd);
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
