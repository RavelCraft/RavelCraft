package com.connexal.ravelcraft.mod.server.mixin.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void playerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        boolean out = PlayerEvents.PRE_JOIN.invoker().onPlayerPreJoin(player, connection);
        if (!out) {
            return;
        }

        FabricRavelPlayer ravelPlayer = new FabricRavelPlayer(player); //This is referenced in PlayerManagerMixin.md
        PlayerEvents.JOINED.invoker().onPlayerJoined(ravelPlayer);
    }
}
