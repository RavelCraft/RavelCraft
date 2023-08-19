package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.BlockEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    private FabricRavelPlayer ravelPlayer = null;

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    private boolean updateData() {
        if (this.ravelPlayer == null) {
            this.ravelPlayer = (FabricRavelPlayer) RavelInstance.getPlayerManager().getPlayer(this.player.getUuid());
            return this.ravelPlayer != null;
        }

        return true;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.BEFORE), method = "tryBreakBlock", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, BlockEntity entity, Block block) {
        if (!this.updateData()) {
            return;
        }

        boolean allowed = BlockEvents.BREAK.invoker().onBlockBreak(this.ravelPlayer, block, pos);
        if (!allowed) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", shift = At.Shift.BEFORE), cancellable = true)
    private void beforeBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        final Item item = stack.getItem();

        if (item instanceof BlockItem) {
            ItemUsageContext usageContext = new ItemUsageContext(player, hand, hitResult);
            ItemPlacementContext placementContext = new ItemPlacementContext(usageContext);
            BlockState futureState = ((BlockItem) item).getBlock().getPlacementState(placementContext);

            if (!this.updateData()) {
                return;
            }

            boolean allowed = BlockEvents.PLACE.invoker().onBlockPlace(this.ravelPlayer, futureState, hitResult.getBlockPos().offset(hitResult.getSide()));
            if (!allowed) {
                cir.setReturnValue(ActionResult.FAIL); //TODO: Figure out why this makes items disappear client side
            }
        }
    }
}
