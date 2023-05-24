package com.connexal.ravelcraft.shared.util;

import com.connexal.ravelcraft.shared.BuildConstants;

public enum RavelServer {
    JE_PROXY("JE Proxy", BuildConstants.SERVER_IP, 25565, false, true, false),
    BE_PROXY("BE Proxy", BuildConstants.SERVER_IP, 19132, true, false, false),

    LOBBY("Lobby", "lobby", 25565, false, false, true),
    SURVIVAL("Survival", "survival", 25565),
    MULTI("Creative", "multi", 25565),
    LOULOU("Loulou", "backup.hughes123.co.uk", 25567, "loulou.connexal.com"),

    CHARLES("Charles", "oracle-carlito.chickenkiller.com", 25565),
    TEST("Test", "backup.hughes123.co.uk", 25566);

    private final String name;
    private final String address;
    private final int port;
    private final boolean bedrockProxy;
    private final boolean javaProxy;
    private final boolean lobby;
    private final String directConnect;

    RavelServer(String name, String address, int port, boolean bedrockProxy, boolean javaProxy, boolean lobby, String directConnect) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.bedrockProxy = bedrockProxy;
        this.javaProxy = javaProxy;
        this.lobby = lobby;
        this.directConnect = directConnect;
    }

    RavelServer(String name, String address, int port) {
        this(name, address, port, false, false, false, null);
    }

    RavelServer(String name, String address, int port, boolean bedrockProxy, boolean javaProxy, boolean lobby) {
        this(name, address, port, bedrockProxy, javaProxy, lobby, null);
    }

    RavelServer(String name, String address, int port, String directConnect) {
        this(name, address, port, false, false, false, directConnect);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isBedrockProxy() {
        return bedrockProxy;
    }

    public boolean isJavaProxy() {
        return javaProxy;
    }

    public boolean isLobby() {
        return lobby;
    }

    public String getDirectConnect() {
        return directConnect;
    }
}
