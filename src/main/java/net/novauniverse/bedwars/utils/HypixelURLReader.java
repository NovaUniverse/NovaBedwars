package net.novauniverse.bedwars.utils;

import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HypixelURLReader {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }
    public static String getPlayerBedwarsPreferences(Player player) throws IOException {
        return getPlayerBedwarsPreferences(player.getDisplayName());
    }

    public static String getPlayerBedwarsPreferences(String player) throws IOException {
        JSONObject json = readJsonFromUrl("https://api.hypixel.net/player?key=a9cc0ba8-bf22-4f5a-807a-c157fe7503f8&name=" + player);
        return json.getJSONObject("player").getJSONObject("stats").getJSONObject("Bedwars").getString("favourites_2");
    }
}
