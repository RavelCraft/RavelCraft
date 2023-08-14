package com.connexal.ravelcraft.mod.server.util.gui;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.libs.sgui.api.gui.SimpleGui;
import com.connexal.ravelcraft.mod.server.mixin.accessors.EntityAccessor;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.dimension.DimensionType;

import java.io.File;
import java.util.UUID;

public class PlayerInvGui extends SimpleGui {
    private final ServerPlayerEntity viewedPlayer;

    public PlayerInvGui(ServerPlayerEntity player, Type type, ServerPlayerEntity viewedPlayer) {
        super(type.getScreenType(), player, false);
        this.viewedPlayer = viewedPlayer;

        this.setTitle(viewedPlayer.getName());

        if (type == Type.PLAYER) {
            //Hotbar - bottom of the inventory
            for (int i = 0; i < 9; i++) {
                this.setSlotRedirect(i + 27, new Slot(viewedPlayer.getInventory(), i, 0, 0));
            }
            //Main inventory
            for (int i = 0; i < 27; i++) {
                this.setSlotRedirect(i, new Slot(viewedPlayer.getInventory(), i + 9, 0, 0));
            }
            //Armor slots + offhand
            for (int i = 0; i < 5; i++) {
                this.setSlotRedirect(i + 36, new Slot(viewedPlayer.getInventory(), i + 36, 0, 0));
            }
        } else if (type == Type.ENDER) {
            for (int i = 0; i < 27; i++) {
                this.setSlotRedirect(i, new Slot(viewedPlayer.getEnderChestInventory(), i, 0, 0));
            }
        } else {
            throw new RuntimeException("Invalid inventory type: " + type);
        }
    }

    public PlayerInvGui(ServerPlayerEntity player, Type type, UUID viewedPlayer) {
        this(player, type, PlayerInvGui.getPlayer(viewedPlayer));
    }

    @Override
    public void onClose() {
        PlayerInvGui.savePlayerData(this.viewedPlayer);
    }

    private static ServerPlayerEntity getPlayer(UUID uuid) {
        MinecraftServer server = RavelModServer.getServer();

        ServerPlayerEntity requestedPlayer = server.getPlayerManager().getPlayer(uuid);
        GameProfile requestedProfile = new GameProfile(uuid, RavelInstance.getUUIDTools().getName(uuid));

        if (requestedPlayer == null) {
            requestedPlayer = server.getPlayerManager().createPlayer(requestedProfile);
            NbtCompound compound = server.getPlayerManager().loadPlayerData(requestedPlayer);
            if (compound != null) {
                ServerWorld world = server.getWorld(
                        DimensionType.worldFromDimensionNbt(new Dynamic<>(NbtOps.INSTANCE, compound.get("Dimension")))
                                .result().get());

                if (world != null) {
                    ((EntityAccessor) requestedPlayer).callSetWorld(world);
                }
            }
        }

        return requestedPlayer;
    }

    private static void savePlayerData(ServerPlayerEntity player) {
        File playerDataDir = RavelModServer.getServer().getSavePath(WorldSavePath.PLAYERDATA).toFile();
        try {
            NbtCompound compoundTag = player.writeNbt(new NbtCompound());
            File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", playerDataDir);
            NbtIo.writeCompressed(compoundTag, file);
            File file2 = new File(playerDataDir, player.getUuidAsString() + ".dat");
            File file3 = new File(playerDataDir, player.getUuidAsString() + ".dat_old");
            Util.backupAndReplace(file2, file, file3);
        } catch (Exception e) {
            RavelInstance.getLogger().warning("Failed to save player data for " + player.getName().getString());
        }
    }

    public enum Type {
        PLAYER(ScreenHandlerType.GENERIC_9X5),
        ENDER(ScreenHandlerType.GENERIC_9X3);

        private final ScreenHandlerType<?> type;

        Type(ScreenHandlerType<?> type) {
            this.type = type;
        }

        public ScreenHandlerType<?> getScreenType() {
            return this.type;
        }
    }
}
