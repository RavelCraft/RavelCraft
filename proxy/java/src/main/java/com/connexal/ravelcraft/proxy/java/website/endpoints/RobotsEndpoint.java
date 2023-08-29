package com.connexal.ravelcraft.proxy.java.website.endpoints;

import com.connexal.ravelcraft.proxy.java.website.EndpointType;
import com.connexal.ravelcraft.proxy.java.website.PageRequest;
import com.connexal.ravelcraft.proxy.java.website.PageReturn;
import com.connexal.ravelcraft.proxy.java.website.WebServer;
import com.connexal.ravelcraft.shared.RavelInstance;

public class RobotsEndpoint extends AbstractEndpoint {
    private final PageReturn page;

    public RobotsEndpoint() {
        super(EndpointType.SIMPLE, "/robots.txt");

        this.page = new PageReturn("User-agent: *\nDisallow: /\n");
    }

    @Override
    protected PageReturn getPageContents(WebServer server, PageRequest request) {
        return this.page;
    }
}

