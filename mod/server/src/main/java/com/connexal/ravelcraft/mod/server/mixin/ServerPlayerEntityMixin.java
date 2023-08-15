package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.events.ItemEvents;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    private FabricRavelPlayer ravelPlayer = null;

    private boolean updateData() {
        if (this.ravelPlayer == null) {
            this.ravelPlayer = (FabricRavelPlayer) RavelInstance.getPlayerManager().getPlayer(((ServerPlayerEntity) (Object) this).getUuid());
            return this.ravelPlayer != null;
        }

        return true;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getPrimeAdversary()Lnet/minecraft/entity/LivingEntity;"), method = "onDeath")
    private void onPlayerKilled(DamageSource source, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof ServerPlayerEntity player) {
            RavelPlayer killerRavelPlayer = RavelInstance.getPlayerManager().getPlayer(player.getUuid());
            if (this.updateData()) {
                PlayerEvents.KILL_PLAYER.invoker().onPlayerKillPlayer((FabricRavelPlayer) killerRavelPlayer, this.ravelPlayer);
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "onDeath")
    private void onPlayerDeath(DamageSource source, CallbackInfo info) {
        if (this.updateData()) {
            PlayerEvents.DEATH.invoker().onPlayerDeath(this.ravelPlayer, source);
        }
    }

    @Inject(method = "dropItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlayerDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir, ItemEntity entity) {
        if (stack.isEmpty()) {
            return;
        }

        if (this.updateData()) {
            ItemEvents.ITEM_DROP.invoker().onItemDrop(this.ravelPlayer, stack, Location.of(entity));
        }
    }
}
