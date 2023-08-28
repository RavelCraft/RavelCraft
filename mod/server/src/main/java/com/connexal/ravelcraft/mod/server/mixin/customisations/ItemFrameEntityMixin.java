package com.connexal.ravelcraft.mod.server.mixin.customisations;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {
    @Inject(method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;)V"))
    private void makeItemFrameInvisible(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getPose().equals(EntityPose.CROUCHING)) {
            ((ItemFrameEntity) (Object) this).setInvisible(true);
        }
    }

    @Inject(method = "removeFromFrame(Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void onItemFrameDrop(CallbackInfo ci){
        ItemFrameEntity self = (ItemFrameEntity) (Object) this;
        if (self.isInvisible()) {
            self.setInvisible(false);
        }
    }
}
