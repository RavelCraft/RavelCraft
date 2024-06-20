package com.connexal.ravelcraft.mod.server.managers;

import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.all.util.RavelConfig;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SpawnManager {
    private final RavelConfig config;
    private Location spawn;

    public SpawnManager() {
        this.config = RavelInstance.getConfig();

        if (config.contains("spawn.world")) {
            double x = this.config.getDouble("spawn.x");
            double y = this.config.getDouble("spawn.y");
            double z = this.config.getDouble("spawn.z");
            float pitch = this.config.getFloat("spawn.pitch");
            float yaw = this.config.getFloat("spawn.yaw");
            RegistryKey<World> world = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(this.config.getString("spawn.world")));

            this.spawn = new Location(x, y, z, pitch, yaw, world);
        } else {
            this.spawn = null;
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;

        this.config.set("spawn.world", spawn.getWorld().getValue().toString());
        this.config.set("spawn.x", spawn.getX());
        this.config.set("spawn.y", spawn.getY());
        this.config.set("spawn.z", spawn.getZ());
        this.config.set("spawn.pitch", spawn.getPitch());
        this.config.set("spawn.yaw", spawn.getYaw());

        this.config.save();
    }
}
