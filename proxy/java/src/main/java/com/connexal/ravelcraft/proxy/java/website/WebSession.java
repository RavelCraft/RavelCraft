package com.connexal.ravelcraft.proxy.java.website;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebSession {
    private final String token;
    private final long updateTime;
    private final Map<String, Object> data = new HashMap<>();

    public WebSession() {
        this.token = UUID.randomUUID() + "-" + UUID.randomUUID();
        this.updateTime = System.currentTimeMillis();
    }

    public String getToken() {
        return this.token;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() + (WebServer.SESSION_EXPIRY_TIME * 1000) < updateTime;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    public void removeData(String key) {
        this.data.remove(key);
    }

}
