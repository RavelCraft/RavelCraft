package com.connexal.ravelcraft.mod.server.managers;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.events.BlockEvents;
import com.connexal.ravelcraft.mod.server.util.events.ItemEvents;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.util.Lock;
import com.connexal.ravelcraft.shared.all.util.RavelConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Ravel1984Manager {
    private final String logPath;
    private final Queue<LogData> logQueue = new LinkedList<>();
    private final Lock lock = new Lock();

    private Ravel1984Manager(String logPath) {
        this.logPath = logPath;
        RavelInstance.scheduleRepeatingTask(this::flushCache, 60);

        //Connection events
        PlayerEvents.JOINED.register(this::playerJoined);
        PlayerEvents.LEFT.register(this::playerLeft);

        //Chat events
        PlayerEvents.CHAT.register(this::playerChat);

        //Commands
        PlayerEvents.COMMAND.register(this::playerCommand);

        //Teleport
        PlayerEvents.TELEPORT.register(this::playerTeleport);

        //Kill
        PlayerEvents.KILL_PLAYER.register(this::playerKillPlayer);
        PlayerEvents.KILL_ENTITY.register(this::playerKillEntity);

        //Death
        PlayerEvents.DEATH.register(this::playerDeath);

        //Blocks
        BlockEvents.PLACE.register(this::blockPlace);
        BlockEvents.BREAK.register(this::blockBreak);

        //Buckets
        ItemEvents.BUCKET_FILL.register(this::bucketFill);
        ItemEvents.BUCKET_EMPTY.register(this::bucketEmpty);

        //Items
        ItemEvents.ITEM_PICKUP.register(this::itemPickup);
        ItemEvents.ITEM_DROP.register(this::itemDrop);
        ItemEvents.CONTAINER_MOVE.register(this::containerMove); //TODO: Call this event
    }

    public static Ravel1984Manager create() {
        RavelConfig config = RavelInstance.getConfig();
        if (config.contains("ravel1984.enabled")) {
            if (!config.getBoolean("ravel1984.enabled")) {
                return null;
            }
        } else {
            config.set("ravel1984.enabled", false);
            return null;
        }

        String path;
        if (config.contains("ravel1984.logPath")) {
            path = config.getString("ravel1984.logPath");
        } else {
            config.set("ravel1984.logPath", "/path/to/file");
            config.save();

            RavelInstance.getLogger().error("Ravel1984 log path not set. Please set ravel1984.logPath in config.yml");
            return null;
        }

        return new Ravel1984Manager(path);
    }

    public void logData(String dataType, String data, UUID uuid) {
        RavelInstance.scheduleTask(() -> {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            File logFile = new File(this.logPath + "/" + dateFormatter.format(ZonedDateTime.now()) + "/" + uuid.toString() + "/" + dataType + ".txt");
            String logData = "[" + timeFormatter.format(ZonedDateTime.now()) + "] " + data + "\n";

            this.lock.lock();
            this.logQueue.add(new LogData(logFile, logData));
            this.lock.unlock();

            this.lock.lock();
            boolean condition = this.logQueue.size() > 900;
            this.lock.unlock();
            if (condition) {
                this.flushCache();
            }
        });
    }

    public void flushCache() {
        this.lock.lock();
        int size = this.logQueue.size();
        this.lock.unlock();

        if (size == 0) {
            return;
        }
        Map<File, List<String>> logDataMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            this.lock.lock();
            LogData logData = this.logQueue.poll();
            this.lock.unlock();

            logDataMap.computeIfAbsent(logData.file, k -> new ArrayList<>()).add(logData.data);
        }

        for (Map.Entry<File, List<String>> entry : logDataMap.entrySet()) {
            File file = entry.getKey();
            List<String> data = entry.getValue();

            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                for (String line : data) {
                    writer.write(line);
                }
                writer.close();
            } catch (IOException e) {
                RavelInstance.getLogger().error("Could not write to 1984 log file", e);
            }
        }
    }

    public record LogData(File file, String data) {
    }

    //--- Utils ---

    private String formatLocation(FabricRavelPlayer player, BlockPos pos) {
        return player.getLocation().getWorld().getValue().toString() + ": " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    //--- Events ---

    private void playerJoined(FabricRavelPlayer player) {
        this.logData("connections", "Joined the server", player.getUniqueID());
    }

    private void playerLeft(FabricRavelPlayer player, String reason) {
        this.logData("connections", "Left the server: \"" + reason + "\"", player.getUniqueID());
    }

    private boolean playerChat(FabricRavelPlayer player, String message) {
        this.logData("chat", message, player.getUniqueID());
        return true;
    }

    private void playerCommand(FabricRavelPlayer player, String command) {
        this.logData("commands", command, player.getUniqueID());
    }

    private void playerTeleport(FabricRavelPlayer player, World world, double x, double y, double z, float yaw, float pitch) {
        String location = world.getRegistryKey().getValue().toString() + ": " + x + " " + y + " " + z;
        this.logData("teleports", "Teleported to " + location, player.getUniqueID());
    }

    private void playerKillEntity(FabricRavelPlayer player, Entity entity) {
        this.logData("kills", "Killed entity " + entity.getType().getName().getString() + " at " + player.getLocation().chatFormat(), player.getUniqueID());
    }

    private void playerKillPlayer(FabricRavelPlayer killer, FabricRavelPlayer target) {
        this.logData("kills", "Killed player " + target.getName() + " at " + target.getLocation().chatFormat(), killer.getUniqueID());
    }

    private boolean playerDeath(FabricRavelPlayer player, DamageSource damageSource) {
        this.logData("death", "Died at " + player.getLocation().chatFormat() + " from " + damageSource.getName(), player.getUniqueID());
        return true;
    }

    private boolean blockPlace(FabricRavelPlayer player, BlockState futureState, BlockPos blockPos) {
        this.logData("block", "Place: " + futureState.getBlock().getName().getString() + " at " + this.formatLocation(player, blockPos), player.getUniqueID());
        return true;
    }

    private boolean blockBreak(FabricRavelPlayer player, Block block, BlockPos blockPos) {
        this.logData("block", "Break: " + block.getName().getString() + " at " + this.formatLocation(player, blockPos), player.getUniqueID());
        return true;
    }

    private void bucketFill(FabricRavelPlayer player, BlockPos blockPos, ItemStack itemStack) {
        this.logData("bucket", "Fill: " + itemStack.getName().getString() + " at " + this.formatLocation(player, blockPos), player.getUniqueID());
    }

    private void bucketEmpty(FabricRavelPlayer player, BlockPos blockPos, ItemStack itemStack) {
        this.logData("bucket", "Empty: " + itemStack.getName().getString() + " at " + this.formatLocation(player, blockPos), player.getUniqueID());
    }

    private void itemPickup(FabricRavelPlayer player, ItemStack itemStack, Location location) {
        this.logData("item", "Pickup: " + itemStack.getName().getString() + " x" + itemStack.getCount() + " at " + location.chatFormat(), player.getUniqueID());
    }

    private void itemDrop(FabricRavelPlayer player, ItemStack itemStack, Location location) {
        this.logData("item", "Drop: " + itemStack.getName().getString() + " x" + itemStack.getCount() + " at " + location.chatFormat(), player.getUniqueID());
    }

    private void containerMove(FabricRavelPlayer player, ItemStack srcItemStack, Inventory srcInventory, ItemStack destItemStack, Inventory destInventory) {

        this.logData("container", "Swap (" + player.getLocation().chatFormat() + "): " + srcItemStack.getName().getString() + " (" + srcInventory.toString() + ") -> " + destItemStack.getName().getString() + " (" + destInventory.toString() + ")", player.getUniqueID());
    }
}
