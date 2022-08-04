package me.masstrix.eternallight.listener;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.handle.Projector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private EternalLight plugin;

    public PlayerMoveListener(EternalLight plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                || from.getBlockZ() == to.getBlockZ()
                || from.getBlockY() == to.getBlockY()) {
            return;
        }

        Player player = event.getPlayer();
        Projector projector = plugin.getProjector();
        if (projector.canSee(player) && projector.getVisual(player).isEnabled()) {
            projector.getVisual(player).update();
        }
    }
}
