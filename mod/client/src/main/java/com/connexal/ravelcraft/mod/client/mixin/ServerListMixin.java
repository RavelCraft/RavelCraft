package com.connexal.ravelcraft.mod.client.mixin;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerList.class)
public class ServerListMixin {
    private static final String SERVER_NAME = "RavelCraft Network";
    private static final String SERVER_IP = "ravelcraft.connexal.com";

    @Shadow @Final private List<ServerInfo> servers;

    @Inject(at = @At("RETURN"), method = "loadFile()V")
    private void loadFile(CallbackInfo info) {
        boolean ravelPresent = false;
        for (ServerInfo server : servers) {
            if (server.address.equals(SERVER_IP)) {
                ravelPresent = true;
                break;
            }
        }

        if (!ravelPresent) {
            this.servers.add(0, new ServerInfo(SERVER_NAME, SERVER_IP, false));
        }
    }
}
