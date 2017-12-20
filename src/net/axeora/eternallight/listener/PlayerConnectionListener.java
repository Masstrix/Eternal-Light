package net.axeora.eternallight.listener;

import net.axeora.eternallight.EternalLight;
import net.axeora.eternallight.PluginData;
import net.axeora.eternallight.util.StringUtil;
import net.axeora.eternallight.util.VersionChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("eternallight.admin")) {
            if (EternalLight.getInstance().getPluginConfig().isUpdateNotifications()) {
                VersionChecker.VersionMeta meta = EternalLight.getInstance().getVersionMeta();
                if (meta == null) return;
                if (meta.getState() == VersionChecker.PluginVersionState.BEHIND) {
                    player.sendMessage(StringUtil.color(
                            PluginData.PREFIX + "&bA newer version is available. " +
                            "Update now to get new features and bug patch's."));
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        EternalLight.getInstance().getProjector().remove(event.getPlayer());
    }
}
