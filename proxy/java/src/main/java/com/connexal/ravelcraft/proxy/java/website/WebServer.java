package com.connexal.ravelcraft.proxy.java.website;

import com.connexal.ravelcraft.proxy.java.website.endpoints.*;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer implements HttpHandler {
    public static final long SESSION_EXPIRY_TIME = 60 * 60; // 1 hour

    private final HttpServer httpServer;
    private final Path path;

    private final Map<String, WebSession> sessionMap = new HashMap<>();
    private final List<AbstractEndpoint> endpoints = new ArrayList<>();
    private String errorPage = "";

    private WebServer(String path, int port) throws IOException {
        this.path = Path.of(path);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        this.httpServer = HttpServer.create(inetSocketAddress, 0);
        this.httpServer.setExecutor(null);

        this.httpServer.createContext("/", this);
        this.reload();

        this.httpServer.start();
        RavelInstance.getLogger().info("Web server started on port " + port);
    }

    public void reload() {
        this.endpoints.clear();

        //this.endpoints.add(new AuthEndpoint()); disabled for now
        //this.endpoints.add(new GameEndpoint()); disabled for now
        this.endpoints.add(new RobotsEndpoint());
        this.endpoints.add(new VersionEndpoint());

        //Now for the RHTML endpoints
        try {
            Path markdownRoot = this.path.resolve("pages");

            List<Path> files = Files.walk(markdownRoot)
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().endsWith(".rhtml"))
                    .toList();

            for (Path file : files) {
                endpoints.add(RhtmlEndpoint.create(this, file));
            }
        } catch (IOException | IllegalStateException e) {
            RavelInstance.getLogger().error("Error loading web markdown files", e);
        }

        try {
            this.errorPage = Files.readString(this.path.resolve("base/error.html"))
                    .replace("${header}", Files.readString(this.path.resolve("base/content/header.html")));
        } catch (IOException e) {
            RavelInstance.getLogger().error("Error loading error page", e);
        }
    }

    public static WebServer create() {
        RavelConfig config = RavelInstance.getConfig();

        String path = config.getString("website.path");
        Integer port = config.contains("website.port") ? config.getInt("website.port") : null;

        boolean isAlright = true;
        if (path == null) {
            config.set("website.path", "/path/to/website");
            isAlright = false;
        }
        if (port == null) {
            config.set("website.port", 8080);
            isAlright = false;
        }

        if (!isAlright) {
            config.save();
            RavelInstance.getLogger().error("No website port or path specified in config.yml!");
            return null;
        }

        try {
            return new WebServer(path, port);
        } catch (IOException e) {
            RavelInstance.getLogger().error("Failed to create web server", e);
            return null;
        }
    }

    @Override
    public void handle(HttpExchange exchange) {
        RavelInstance.scheduleTask(() -> {
            AbstractEndpoint bestEndpoint = null;
            for (AbstractEndpoint endpoint : this.endpoints) {
                if (endpoint.canResolve(exchange.getRequestURI().getPath())) {
                    bestEndpoint = endpoint;
                    break;
                }
            }

            PageReturn pageReturn;
            if (bestEndpoint != null) {
                pageReturn = bestEndpoint.pageQuery(this, exchange);
            } else {
                pageReturn = new PageReturn("Page not found", 404, false);
            }

            byte[] data;
            if (pageReturn.getStatusCode() == 200 || pageReturn.isRawData()) {
                data = pageReturn.getData();
            } else {
                data = this.errorPage.replace("${error}", pageReturn.getStatusCode() + " - " + new String(pageReturn.getData(), StandardCharsets.UTF_8)).getBytes();
            }

            for (Map.Entry<String, String> entry : pageReturn.getHeaders().entrySet()) {
                exchange.getResponseHeaders().add(entry.getKey(), entry.getValue());
            }
            if (pageReturn.getRedirect() != null) {
                exchange.getResponseHeaders().add("Location", pageReturn.getRedirect());
            }

            try {
                exchange.sendResponseHeaders(pageReturn.getStatusCode(), data.length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(data);
                outputStream.flush();
                exchange.close();
            } catch (IOException ignored) {
            }
        });
    }

    public Path getPath() {
        return path;
    }

    public WebSession getSession(String token, boolean create) {
        WebSession webSession = this.sessionMap.get(token);
        if (token == null || webSession == null) {
            if (create) {
                webSession = new WebSession();
                this.sessionMap.put(webSession.getToken(), webSession);
            } else {
                webSession = null;
            }
        }

        new Thread(() -> {
            for (WebSession session : this.sessionMap.values()) {
                if (session.isExpired()) {
                    this.sessionMap.remove(session.getToken());
                }
            }
        });

        return webSession;
    }
}

