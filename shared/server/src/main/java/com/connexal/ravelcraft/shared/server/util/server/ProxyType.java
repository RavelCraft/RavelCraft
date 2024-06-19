package com.connexal.ravelcraft.shared.server.util.server;

public enum ProxyType {
    JAVA,
    BEDROCK

    //If a type is added later for whatever reason, note that ProxyPlayerManagerImpl#proxyQueryConnected will need to be updated
}
