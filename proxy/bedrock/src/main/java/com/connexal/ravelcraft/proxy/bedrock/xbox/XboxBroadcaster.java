package com.connexal.ravelcraft.proxy.bedrock.xbox;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.bedrock.util.Motd;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.RavelMain;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.rtm516.mcxboxbroadcast.core.Logger;
import com.rtm516.mcxboxbroadcast.core.SessionInfo;
import com.rtm516.mcxboxbroadcast.core.SessionManager;
import com.rtm516.mcxboxbroadcast.core.configs.FriendSyncConfig;
import dev.waterdog.waterdogpe.network.protocol.ProtocolVersion;

import java.nio.file.Files;
import java.nio.file.Path;

public class XboxBroadcaster {
    private final String dataPath;
    private final Logger logger;

    private SessionManager sessionManager;
    private SessionInfo sessionInfo;

    public XboxBroadcaster() {
        Path dataPath = RavelMain.get().getDataPath().resolve("xbox");
        if (!Files.exists(dataPath)) {
            try {
                Files.createDirectories(dataPath);
            } catch (Exception e) {
                RavelInstance.getLogger().error("Failed to create Xbox data directory", e);
            }
        }

        this.dataPath = dataPath.toAbsolutePath().toString();
        this.logger = new XboxLogger(BeProxy.getServer().getLogger());

        this.sessionManager = new SessionManager(this.dataPath, this.logger);

        RavelInstance.scheduleTask(() -> {
            this.sessionInfo = new SessionInfo();
            this.sessionInfo.setHostName(Motd.FIRST_LINE);
            this.sessionInfo.setWorldName(RavelProxyInstance.getMotdManager().getMotd());
            this.sessionInfo.setVersion(ProtocolVersion.latest().getMinecraftVersion());
            this.sessionInfo.setProtocol(ProtocolVersion.latest().getProtocol());
            this.sessionInfo.setPlayers(RavelInstance.getPlayerManager().getOnlineCount());
            this.sessionInfo.setMaxPlayers(Ravel.MAX_PLAYERS);

            this.sessionInfo.setIp(RavelServer.BE_PROXY.getAddress());
            this.sessionInfo.setPort(RavelServer.BE_PROXY.getPort());

            this.createSession();
        });
    }

    public void restart() {
        this.sessionManager.shutdown();
        this.sessionManager = new SessionManager(this.dataPath, this.logger);

         this.createSession();
    }

    public void createSession() {
        FriendSyncConfig friendSync = new FriendSyncConfig(30, true, true);

        this.sessionManager.restartCallback(this::restart);
        try {
            this.sessionManager.init(this.sessionInfo, friendSync);
        } catch (Exception e) {
            RavelInstance.getLogger().error("Failed to create Xbox session", e);
            return;
        }

        this.sessionManager.friendManager().initAutoFriend(friendSync);
        RavelInstance.scheduleRepeatingTask(this::tick, friendSync.updateInterval());
    }

    public void tick() {
        try {
            this.sessionInfo.setPlayers(RavelInstance.getPlayerManager().getOnlineCount());
            this.sessionInfo.setWorldName(RavelProxyInstance.getMotdManager().getMotd());
            this.sessionManager.updateSession(this.sessionInfo);
        } catch (Exception e) {
            RavelInstance.getLogger().error("Failed to update Xbox session", e);
        }
    }
}
