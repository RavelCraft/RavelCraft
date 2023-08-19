package com.connexal.ravelcraft.mod.server.util;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Location {
    private Vec3d position;
    private float pitch;
    private float yaw;
    private RegistryKey<World> world;

    public Location(Vec3d position, float pitch, float yaw, RegistryKey<World> world) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.world = world;
    }

    public Location(double x, double y, double z, float pitch, float yaw, RegistryKey<World> world) {
        this.position = new Vec3d(x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
        this.world = world;
    }

    public static Location of(Entity entity) {
        return new Location(Vec3d.ZERO.add(entity.getPos()), entity.getPitch(), entity.getHeadYaw(), entity.getWorld().getRegistryKey());
    }

    public Vec3d getPosition() {
        return position;
    }

    public Location setPosition(Vec3d position) {
        this.position = position;
        return this;
    }

    public Location setPosition(double x, double y, double z) {
        this.position = new Vec3d(x, y, z);
        return this;
    }

    public double getX() {
        return position.getX();
    }

    public Location setX(double x) {
        this.position = new Vec3d(x, this.position.getY(), this.position.getZ());
        return this;
    }

    public double getY() {
        return position.getY();
    }

    public Location setY(double y) {
        this.position = new Vec3d(this.position.getX(), y, this.position.getZ());
        return this;
    }

    public double getZ() {
        return position.getZ();
    }

    public Location setZ(double z) {
        this.position = new Vec3d(this.position.getX(), this.position.getY(), z);
        return this;
    }

    public Location add(double x, double y, double z) {
        this.position = this.position.add(x, y, z);
        return this;
    }

    public Location add(Vec3d vec) {
        this.position = this.position.add(vec);
        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.position = this.position.subtract(x, y, z);
        return this;
    }

    public Location subtract(Vec3d vec) {
        this.position = this.position.subtract(vec);
        return this;
    }

    public Location multiply(double x, double y, double z) {
        this.position = this.position.multiply(x, y, z);
        return this;
    }

    public Location multiply(Vec3d vec) {
        this.position = this.position.multiply(vec);
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public Location setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float getYaw() {
        return yaw;
    }

    public Location setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    public Location setWorld(RegistryKey<World> world) {
        this.world = world;
        return this;
    }

    public Location copy() {
        return new Location(this.position, this.pitch, this.yaw, this.world);
    }

    public String chatFormat() {
        return this.getWorld().getValue().toString() + ": " + Math.round(this.getX()) + " " + Math.round(this.getY()) + " " + Math.round(this.getZ());
    }
}
