package me.masstrix.eternallight.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.awt.*;

public class RGBParticle {

    private int r = 0, g = 0, b = 0;

    public RGBParticle() {}

    public RGBParticle(int r, int g, int b) {
        setColor(r, g, b);
    }

    public RGBParticle(Color color) {
        setColor(color);
    }

    public void setColor(Color color) {
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
    }

    public void setColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setRed(int r) {
        this.r = r;
        if (this.r > 255) this.r = 255;
    }

    public void setGreen(int g) {
        this.g = g;
        if (this.g > 255) this.g = 255;
    }

    public void setBlue(int b) {
        this.b = b;
        if (this.b > 255) this.b = 255;
    }

    public void send(Player player, double x, double y, double z) {
        display(player, new Location(player.getWorld(), x, y, z));
    }

    public void display(Player player, Location loc) {
        // 1.14 / 1.13
        player.spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(org.bukkit.Color.fromRGB(r, g, b), 1));
    }

    @Override
    public String toString() {
        return String.format("[%d, %d, %d]", r, g, b);
    }
}
