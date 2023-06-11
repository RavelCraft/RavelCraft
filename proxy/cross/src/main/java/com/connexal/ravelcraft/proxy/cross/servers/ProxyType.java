package com.connexal.ravelcraft.proxy.cross.servers;

public enum ProxyType {
    JAVA,
    BEDROCK

    //If a type is added later for whatever reason, note that ProxyPlayerManagerImpl#proxyQueryConnected will need to be updated
}
