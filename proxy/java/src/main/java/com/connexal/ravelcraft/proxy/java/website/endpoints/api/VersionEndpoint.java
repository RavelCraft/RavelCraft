package com.connexal.ravelcraft.proxy.java.website.endpoints.api;

import com.connexal.ravelcraft.proxy.java.website.EndpointType;
import com.connexal.ravelcraft.proxy.java.website.PageRequest;
import com.connexal.ravelcraft.proxy.java.website.PageReturn;
import com.connexal.ravelcraft.proxy.java.website.WebServer;
import com.connexal.ravelcraft.proxy.java.website.endpoints.AbstractEndpoint;
import com.connexal.ravelcraft.shared.all.Ravel;

public class VersionEndpoint extends AbstractEndpoint {
    private final PageReturn page;

    public VersionEndpoint() {
        super(EndpointType.SIMPLE, "/api/version");

        this.page = new PageReturn(Ravel.VERSION, 200, false, "text/plain");
    }

    @Override
    protected PageReturn getPageContents(WebServer server, PageRequest request) {
        return this.page;
    }
}

