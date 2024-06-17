package com.connexal.ravelcraft.mod.server.managers;

import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.UUID;

public class HomeManager {
    private final int maxHomes;
    private final RavelConfig config;

    public HomeManager() {
        this.config = RavelInstance.getConfig("homes");

        //Get the max homes from the config, or set it to 2 if it doesn't exist
        if (this.config.contains("max-homes")) {
            this.maxHomes = config.getInt("max-homes");
        } else {
            this.maxHomes = 2;
            this.config.set("max-homes", this.maxHomes);
            this.config.save();
        }
    }

    public int getMaxHomes() {
        return maxHomes;
    }

    public void setHome(UUID uuid, int number, Location location) {
        String path = uuid.toString() + "." + number;

        this.config.set(path + ".x", location.getX());
        this.config.set(path + ".y", location.getY());
        this.config.set(path + ".z", location.getZ());
        this.config.set(path + ".pitch", location.getPitch());
        this.config.set(path + ".yaw", location.getYaw());
        this.config.set(path + ".world", location.getWorld().getValue().toString());

        this.config.save();
    }

    public void deleteHome(UUID uuid, int number) {
        this.config.set(uuid.toString() + "." + number, null);
        this.config.save();
    }

    public Location getHome(UUID uuid, int number) {
        String path = uuid.toString() + "." + number;

        if (!this.config.contains(path + ".world")) {
            return null;
        }

        double x = this.config.getDouble(path + ".x");
        double y = this.config.getDouble(path + ".y");
        double z = this.config.getDouble(path + ".z");
        float pitch = this.config.getFloat(path + ".pitch");
        float yaw = this.config.getFloat(path + ".yaw");
        RegistryKey<World> world = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(this.config.getString(path + ".world")));

        return new Location(x, y, z, pitch, yaw, world);
    }
}
