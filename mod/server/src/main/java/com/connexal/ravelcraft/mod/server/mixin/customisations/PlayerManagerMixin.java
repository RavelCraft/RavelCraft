package com.connexal.ravelcraft.mod.server.mixin.customisations;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    //Remove the "player joined the game" message
    @Inject(at = @At(value = "HEAD"), method = "broadcast(Lnet/minecraft/text/Text;Z)V", cancellable = true)
    private void filterJoinedGameMessages(Text message, boolean overlay, CallbackInfo info) {
        String key = ((TranslatableTextContent) message.getContent()).getKey();
        if (key.equals("multiplayer.player.joined.renamed") || key.equals("multiplayer.player.joined") || key.equals("multiplayer.player.left")) {
            info.cancel();
        }
    }

    //FabricRavelPlayer is created within the method "playerJoin" of this mixin in the "events" package

    //Ignore the chat signature verification
    @Inject(at = @At(value = "HEAD"), method = "verify", cancellable = true)
    private void verifyPlayer(SignedMessage message, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
