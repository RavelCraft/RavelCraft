package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.players.PlayerManager;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.players.RavelRank;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class FabricRavelPlayer implements RavelPlayer {
    private final ServerPlayerEntity player;
    private final RavelServer ownerProxy;
    private RavelRank rank;
    private Language language;
    private RavelServer server;

    public FabricRavelPlayer(ServerPlayerEntity player) {
        this.player = player;

        if (GeyserApi.api().isBedrockPlayer(player.getUuid())) {
            this.ownerProxy = RavelServer.BE_PROXY;
        } else {
            this.ownerProxy = RavelServer.JE_PROXY;
        }

        this.server = RavelInstance.getServer();
        PlayerManager.PlayerSettings settings = RavelInstance.getPlayerManager().getPlayerSettings(player.getUuid(), true);
        this.rank = settings.rank();
        this.language = settings.language();

        this.updateDisplayName();
    }

    // --- Custom ---

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return Location.of(this.player);
    }

    public void teleport(Location location) {
        ServerWorld world = RavelModServer.getServer().getWorld(location.getWorld());
        this.player.teleport(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void teleport(FabricRavelPlayer player) {
        this.teleport(player.getLocation());
    }

    public void kill() {
        this.player.kill();
    }

    public boolean isCreative() {
        return this.player.isCreative();
    }

    public ServerWorld getWorld() {
        return this.player.getServerWorld();
    }

    // --- Global ---

    @Override
    public void sendMessage(RavelText message, String... values) {
        String messageString = message.getMessage(this.getLanguage(), values);
        this.player.sendMessage(Text.literal(messageString));
    }

    @Override
    public String getName() {
        return this.player.getName().getString();
    }

    @Override
    public String displayName() {
        return this.player.getCustomName().getString();
    }

    @Override
    public void updateDisplayName() {
        this.player.setCustomName(Text.literal(this.buildDisplayName()));
        this.player.setCustomNameVisible(true);
    }

    @Override
    public UUID getUniqueID() {
        return this.player.getUuid();
    }

    @Override
    public RavelRank getRank() {
        return this.rank;
    }

    @Override
    public void setRank(RavelRank rank) {
        this.rank = rank;
    }

    @Override
    public RavelServer getServer() {
        return this.server;
    }

    @Override
    public void setServer(RavelServer server) {
        this.server = server;
    }

    @Override
    public RavelServer getOwnerProxy() {
        return this.ownerProxy;
    }

    @Override
    public Language getLanguage() {
        return this.language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }
}
