package com.connexal.ravelcraft.proxy.java.website;

public enum RequestMethod {
    GET,
    POST,
    PUT,
    HEAD,
    DELETE,
    PATCH,
    OPTIONS,
    CONNECT,
    TRACE;

    public static RequestMethod fromString(String method) {
        for (RequestMethod m : values()) {
            if (m.name().equalsIgnoreCase(method)) {
                return m;
            }
        }

        return null;
    }
}
