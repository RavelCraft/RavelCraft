package com.connexal.ravelcraft.mod.client.cracked;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import net.minecraft.client.util.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrackedManager {
    public static String login(String username, String password, Session session) {
        if (username == null || username.isEmpty() || username.isBlank() || password == null || password.isEmpty() || password.isBlank()) {
            return "Username and password must be set";
        }

        String name = session.getUsername();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://" + BuildConstants.SERVER_IP + "/game?username=" + username + "&password=" + password + "&name=" + name).openConnection();
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (!response.toString().equals("OK")) {
                return response.toString();
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        return null;
    }
}
