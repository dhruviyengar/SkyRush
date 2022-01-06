package me.secretagent.skyrush.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Locale;

public class YamlUtil {

    public static Location getLocationAdvanced(World world, String string) {
        String[] strings = string.split(", ");
        double x = Double.parseDouble(strings[0]);
        double y = Double.parseDouble(strings[1]);
        double z = Double.parseDouble(strings[2]);
        float yaw = Float.parseFloat(strings[3]);
        float pitch = Float.parseFloat(strings[4]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location getLocation(World world, String string) {
        String[] strings = string.split(", ");
        double x = Double.parseDouble(strings[0]);
        double y = Double.parseDouble(strings[1]);
        double z = Double.parseDouble(strings[2]);
        return new Location(world, x, y, z);
    }

}
