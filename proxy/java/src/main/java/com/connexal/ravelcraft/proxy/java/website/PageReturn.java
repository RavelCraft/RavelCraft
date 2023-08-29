package com.connexal.ravelcraft.proxy.java.website;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PageReturn {
    private final byte[] data;
    private final boolean rawData;
    private final int statusCode;
    private String redirect;
    private final Map<String, String> headers;

    public PageReturn(byte[] data, int statusCode, boolean rawData, String mime) {
        this.data = data;
        this.statusCode = statusCode;
        this.rawData = rawData;

        this.redirect = null;

        this.headers = new HashMap<>();
        this.headers.put("Server", "RavelCraft");
        this.headers.put("Date", new Date().toString());
        this.headers.put("Content-Type", mime);
    }

    public PageReturn(String data, int statusCode, boolean rawData, String mime) {
        this(data.getBytes(), statusCode, rawData, mime);
    }
    public PageReturn(String data, int statusCode, boolean rawData) {
        this(data, statusCode, rawData, "text/html");
    }

    public PageReturn(String data) {
        this(data, 200, false);
    }

    public byte[] getData() {
        if (this.data == null) {
            return new byte[0];
        }
        return this.data;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean isRawData() {
        return this.rawData;
    }

    public String getRedirect() {
        return this.redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }
}
