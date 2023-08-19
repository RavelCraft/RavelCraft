package com.connexal.ravelcraft.mod.server.mixin.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.events.ItemEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getStack();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V", shift = At.Shift.BEFORE), method = "onPlayerCollision", locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlayerPickup(PlayerEntity player, CallbackInfo info, ItemStack stack, Item item, int num) {
        if (item == Items.AIR) {
            return;
        }

        FabricRavelPlayer ravelPlayer = (FabricRavelPlayer) RavelInstance.getPlayerManager().getPlayer(player.getUuid());
        ItemEvents.ITEM_PICKUP.invoker().onItemPickup(ravelPlayer, new ItemStack(item, num), Location.of((ItemEntity) (Object) this));
    }
}
