package com.connexal.ravelcraft.mod.client.mixin;

import com.connexal.ravelcraft.shared.all.Ravel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(at = @At("HEAD"), method = "add", cancellable = true)
    private void add(Toast toast, CallbackInfo info) {
        ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
        if (server == null) {
            return;
        }

        //Test IP string array contain the server IP, so we can use them to test locally
        if (!server.address.equals(Ravel.SERVER_IP) && !List.of(Ravel.TEST_IPS).contains(server.address)) {
            return;
        }

        if (toast instanceof SystemToast systemToast) {
            if (systemToast.getType() == SystemToast.Type.UNSECURE_SERVER_WARNING) {
                info.cancel();
            }
        }
    }
}
