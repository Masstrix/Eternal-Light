package net.axeora.eternallight.listener;

import net.axeora.eternallight.EternalLight;
import net.axeora.eternallight.handle.Projector;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

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
        Projector projector = EternalLight.getInstance().getProjector();
        if (projector.canSee(player) && projector.getVisual(player).isEnabled()) {
            projector.getVisual(player).update();
        }
    }
}
