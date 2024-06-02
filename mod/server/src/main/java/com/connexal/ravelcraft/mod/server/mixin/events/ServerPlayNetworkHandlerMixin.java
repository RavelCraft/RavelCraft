package com.connexal.ravelcraft.mod.server.mixin.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    private FabricRavelPlayer ravelPlayer = null;

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    @Shadow public ServerPlayerEntity player;

    private boolean updateData() {
        if (this.ravelPlayer == null) {
            this.ravelPlayer = (FabricRavelPlayer) RavelInstance.getPlayerManager().getPlayer(this.getPlayer().getUuid());
            return this.ravelPlayer != null;
        }

        return true;
    }

    @Inject(at = @At("TAIL"), method = "onDisconnected")
    private void onPlayerLeave(net.minecraft.text.Text reason, CallbackInfo ci) {
        if (this.updateData()) {
            PlayerEvents.LEFT.invoker().onPlayerLeft(this.ravelPlayer, reason.getString());
        } else {
            throw new RuntimeException("Player left without being initialized... Something is wrong!");
        }
    }

    @Inject(at = @At("TAIL"), method = "onCommandExecution")
    private void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        if (this.updateData()) {
            PlayerEvents.COMMAND.invoker().onPlayerCommandPreprocess(this.ravelPlayer, packet.command());
        }
    }

    @Inject(at = @At("HEAD"), method = "requestTeleport(DDDFFLjava/util/Set;)V")
    private void onTeleport(double x, double y, double z, float yaw, float pitch, Set<PositionFlag> flags, CallbackInfo ci) {
        if (this.updateData()) {
            PlayerEvents.TELEPORT.invoker().onPlayerTeleport(this.ravelPlayer, this.getPlayer().getWorld(), x, y, z, yaw, pitch);
        }
    }

    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V", shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void handleEntityInteraction(PlayerInteractEntityC2SPacket packet, CallbackInfo ci, ServerWorld world, Entity target) {
        if (target == null || !this.updateData()) {
            return;
        }

        boolean allowed = PlayerEvents.INTERACT_ENTITY.invoker().onPlayerInteractEntity(this.ravelPlayer, target);
        if (!allowed) {
            this.player.getInventory().markDirty();
            ci.cancel();
        }
    }
}
