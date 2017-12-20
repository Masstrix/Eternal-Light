package net.axeora.eternallight.util;

import org.bukkit.ChatColor;

public class StringUtil {

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
