package com.connexal.ravelcraft.mod.server.util.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class PlayerEvents {

    @FunctionalInterface
    public interface PlayerPreJoinEvent {
        boolean onPlayerPreJoin(ServerPlayerEntity player, ClientConnection connection);
    }
    public static final Event<PlayerPreJoinEvent> PRE_JOIN = EventFactory.createArrayBacked(PlayerPreJoinEvent.class, listeners -> (player, connection) -> {
        for (PlayerPreJoinEvent listener : listeners) {
            boolean out = listener.onPlayerPreJoin(player, connection);
            if (!out) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface PlayerJoinedEvent {
        void onPlayerJoined(FabricRavelPlayer player);
    }
    public static final Event<PlayerJoinedEvent> JOINED = EventFactory.createArrayBacked(PlayerJoinedEvent.class, listeners -> (player) -> {
        for (PlayerJoinedEvent listener : listeners) {
            listener.onPlayerJoined(player);
        }
    });

    @FunctionalInterface
    public interface PlayerLeftEvent {
        void onPlayerLeft(FabricRavelPlayer player, String reason);
    }
    public static final Event<PlayerLeftEvent> LEFT = EventFactory.createArrayBacked(PlayerLeftEvent.class, listeners -> (player, reason) -> {
        for (PlayerLeftEvent listener : listeners) {
            listener.onPlayerLeft(player, reason);
        }
    });

    @FunctionalInterface
    public interface PlayerChatEvent {
        boolean onPlayerChat(FabricRavelPlayer player, String message);
    }
    public static final Event<PlayerChatEvent> CHAT = EventFactory.createArrayBacked(PlayerChatEvent.class, listeners -> (player, message) -> {
        for (PlayerChatEvent listener : listeners) {
            boolean out = listener.onPlayerChat(player, message);
            if (!out) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface PlayerCommandPreprocessEvent {
        void onPlayerCommandPreprocess(FabricRavelPlayer player, String command);
    }
    public static final Event<PlayerCommandPreprocessEvent> COMMAND = EventFactory.createArrayBacked(PlayerCommandPreprocessEvent.class, listeners -> (player, command) -> {
        for (PlayerCommandPreprocessEvent listener : listeners) {
            listener.onPlayerCommandPreprocess(player, command);
        }
    });

    @FunctionalInterface
    public interface PlayerTeleportEvent {
        void onPlayerTeleport(FabricRavelPlayer player, World world, double x, double y, double z, float yaw, float pitch);
    }
    public static final Event<PlayerTeleportEvent> TELEPORT = EventFactory.createArrayBacked(PlayerTeleportEvent.class, listeners -> (player, world, x, y, z, yaw, pitch) -> {
        for (PlayerTeleportEvent listener : listeners) {
            listener.onPlayerTeleport(player, world, x, y, z, yaw, pitch);
        }
    });

    @FunctionalInterface
    public interface PlayerDeathEvent {
        boolean onPlayerDeath(FabricRavelPlayer player, DamageSource source);
    }
    public static final Event<PlayerDeathEvent> DEATH = EventFactory.createArrayBacked(PlayerDeathEvent.class, listeners -> (player, source) -> {
        for (PlayerDeathEvent listener : listeners) {
            boolean out = listener.onPlayerDeath(player, source);
            if (!out) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface PlayerKillPlayerEvent {
        void onPlayerKillPlayer(FabricRavelPlayer player, FabricRavelPlayer target);
    }
    public static final Event<PlayerKillPlayerEvent> KILL_PLAYER = EventFactory.createArrayBacked(PlayerKillPlayerEvent.class, listeners -> (player, target) -> {
        for (PlayerKillPlayerEvent listener : listeners) {
            listener.onPlayerKillPlayer(player, target);
        }
    });

    @FunctionalInterface
    public interface PlayerKillEntityEvent {
        void onPlayerKillEntity(FabricRavelPlayer player, Entity entity);
    }
    public static final Event<PlayerKillEntityEvent> KILL_ENTITY = EventFactory.createArrayBacked(PlayerKillEntityEvent.class, listeners -> (player, entity) -> {
        for (PlayerKillEntityEvent listener : listeners) {
            listener.onPlayerKillEntity(player, entity);
        }
    });

    @FunctionalInterface
    public interface PlayerAttackEntityEvent {
        boolean onPlayerAttackEntity(FabricRavelPlayer player, Entity entity);
    }
    public static final Event<PlayerAttackEntityEvent> ATTACK_ENTITY = EventFactory.createArrayBacked(PlayerAttackEntityEvent.class, listeners -> (player, entity) -> {
        for (PlayerAttackEntityEvent listener : listeners) {
            boolean out = listener.onPlayerAttackEntity(player, entity);
            if (!out) {
                return false;
            }
        }

        return true;
    });
}
