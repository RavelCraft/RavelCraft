package com.connexal.ravelcraft.proxy.java.website.endpoints;

import com.connexal.ravelcraft.proxy.java.website.EndpointType;
import com.connexal.ravelcraft.proxy.java.website.PageRequest;
import com.connexal.ravelcraft.proxy.java.website.PageReturn;
import com.connexal.ravelcraft.proxy.java.website.WebServer;
import com.sun.net.httpserver.HttpExchange;

public abstract class AbstractEndpoint {
    protected final EndpointType type;
    protected final String path;

    public AbstractEndpoint(EndpointType type, String path) {
        this.type = type;
        this.path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    protected abstract PageReturn getPageContents(WebServer server, PageRequest request);

    public PageReturn pageQuery(WebServer server, HttpExchange exchange) {
        PageRequest request = new PageRequest(server, exchange);
        return this.getPageContents(server, request);
    }

    public boolean canResolve(String path) {
        if (this.type == EndpointType.ALL) {
            return path.startsWith(this.path);
        } else {
            if (path.contains(".")) {
                return path.equals(this.path);
            } else {
                return path.equals(this.path) || path.equals(this.path + "/");
            }
        }
    }

    public EndpointType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
}
