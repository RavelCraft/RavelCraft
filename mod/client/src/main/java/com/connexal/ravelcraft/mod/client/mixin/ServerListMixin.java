package com.connexal.ravelcraft.mod.client.mixin;

import com.connexal.ravelcraft.shared.BuildConstants;
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
    @Shadow @Final private List<ServerInfo> servers;

    @Inject(at = @At("RETURN"), method = "loadFile()V")
    private void loadFile(CallbackInfo info) {
        boolean ravelPresent = false;
        for (ServerInfo server : servers) {
            if (server.address.equals(BuildConstants.SERVER_IP)) {
                ravelPresent = true;
                break;
            }
        }

        if (!ravelPresent) {
            this.servers.add(0, new ServerInfo("RavelCraft Network", BuildConstants.SERVER_IP, false));
        }
    }
}
