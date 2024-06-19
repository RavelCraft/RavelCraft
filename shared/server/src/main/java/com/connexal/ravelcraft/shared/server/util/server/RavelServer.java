package com.connexal.ravelcraft.shared.server.util.server;

import com.connexal.ravelcraft.shared.all.Ravel;

import java.util.List;
import java.util.Locale;

public enum RavelServer {
    JE_PROXY("JE Proxy", Ravel.SERVER_IP, 25565, ProxyType.JAVA, false),
    BE_PROXY("BE Proxy", Ravel.SERVER_IP, 25565, ProxyType.BEDROCK, false),

    LOBBY("Lobby", "lobby", 25565, true),
    SURVIVAL("Survival", "survival", 25565),
    MULTI("Creative", "multi", 25565),
    LOULOU("Loulou", "loulou", 25565, "loulou." + Ravel.SERVER_IP),

    CHARLES("Charles", "oracle-carlito.chickenkiller.com", 25565),
    TEST("Test", "loulou", 25565);

    public static final RavelServer DEFAULT_SERVER = LOBBY;

    public static RavelServer getServerByAddress(String address) {
        if (address.equals(Ravel.SERVER_IP) || List.of(Ravel.TEST_IPS).contains(address)) {
            return DEFAULT_SERVER;
        }

        for (RavelServer server : RavelServer.values()) {
            if (address.equals(server.getDirectConnect())) {
                return server;
            }
        }

        return null;
    }

    private final String name;
    private final String address;
    private final int port;
    private final ProxyType proxy;
    private final boolean lobby;
    private final String directConnect;

    RavelServer(String name, String address, int port, ProxyType proxy, boolean lobby, String directConnect) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.proxy = proxy;
        this.lobby = lobby;
        this.directConnect = directConnect;
    }

    RavelServer(String name, String address, int port) {
        this(name, address, port, null, false, null);
    }

    RavelServer(String name, String address, int port, ProxyType proxy, boolean lobby) {
        this(name, address, port, proxy, lobby, null);
    }

    RavelServer(String name, String address, int port, boolean lobby) {
        this(name, address, port, null, lobby, null);
    }

    RavelServer(String name, String address, int port, String directConnect) {
        this(name, address, port, null, false, directConnect);
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isProxy() {
        return proxy != null;
    }

    public boolean isBedrockProxy() {
        return proxy == ProxyType.BEDROCK;
    }

    public boolean isJavaProxy() {
        return proxy == ProxyType.JAVA;
    }

    public boolean isLobby() {
        return lobby;
    }

    public String getDirectConnect() {
        return directConnect;
    }
}
