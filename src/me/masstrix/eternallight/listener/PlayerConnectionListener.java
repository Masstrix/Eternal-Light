package me.masstrix.eternallight.listener;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.util.Perm;
import me.masstrix.eternallight.util.StringUtil;
import me.masstrix.eternallight.util.VersionChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private EternalLight plugin;

    public PlayerConnectionListener(EternalLight plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission(Perm.ADMIN)
                && plugin.getPluginConfig().sendUpdateNotifications()) {
            VersionChecker.VersionMeta meta = plugin.getVersionMeta();
            if (meta == null) return;
            if (meta.getState() == VersionChecker.VersionState.BEHIND) {
                player.sendMessage(StringUtil.color(
                        plugin.getPluginConfig().getPrefix() + "&bA newer version is available. " +
                                "Update now to get new features and bug patch's."));
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        plugin.getProjector().remove(event.getPlayer());
    }
}
