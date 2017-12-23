package net.axeora.eternallight.util;

import net.axeora.eternallight.EternalLight;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.awt.*;

public class RGBParticle {

    private double r = 0, g = 0, b = 0;

    public RGBParticle() {}

    public RGBParticle(int r, int g, int b) {
        setColor(r, g, b);
    }

    public RGBParticle(Color color) {
        setColor(color);
    }

    public void setColor(Color color) {
        r = color.getRed() / 255;
        g = color.getGreen() / 255;
        b = color.getBlue() / 255;
    }

    public void setColor(int r, int g, int b) {
        this.r = r / 255;
        this.g = g / 255;
        this.b = b / 255;
    }

    public void setRed(int r) {
        this.r = r / 255;
    }

    public void setGreen(int g) {
        this.g = g / 255;
    }

    public void setBlue(int b) {
        this.b = b / 255;
    }

    public void setRed(double r) {
        this.r = r;
    }

    public void setGreen(double g) {
        this.g = g;
    }

    public void setBlue(double b) {
        this.b = b;
    }

    public void send(Player player, double x, double y, double z) {
        if (EternalLight.getInstance().isLegacy()) {
            Location location = new Location(player.getWorld(), x, y, z);
            player.spigot().playEffect(location, Effect.COLOURED_DUST, 0, 1,
                    (float) r + 0.01F, (float) g, (float) b, 1, 0, 64);
        } else {
            player.spawnParticle(Particle.REDSTONE, x, y, z, 0, r + 0.0001, g, b, 1);
        }
    }
}
