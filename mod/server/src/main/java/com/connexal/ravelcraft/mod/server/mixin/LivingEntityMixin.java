package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.EntityEvents;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onKilledBy(Lnet/minecraft/entity/LivingEntity;)V"),
            method = "onDeath", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onKilledByEntity(DamageSource damageSource, CallbackInfo ci, Entity entity, LivingEntity livingEntity) {
        if (entity instanceof ServerPlayerEntity player) {
            RavelPlayer ravelPlayer = RavelInstance.getPlayerManager().getPlayer(player.getUuid());
            PlayerEvents.KILL_ENTITY.invoker().onPlayerKillEntity((FabricRavelPlayer) ravelPlayer, (Entity) (Object) this);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", shift = At.Shift.BEFORE), method = "damage", cancellable = true)
    private void onPlayerDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        boolean allowed = EntityEvents.DAMAGE.invoker().onPlayerDamage((Entity) (Object) this, source, amount);
        if (!allowed) {
            cir.setReturnValue(false);
        }
    }
}
