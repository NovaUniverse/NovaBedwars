package net.novauniverse.bedwars.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class HypixelAPI {
	public static final String BASE_URL = "https://api.hypixel.net"; // Do not end with /
	public static int FETCH_TIMEOUT = 10 * 1000; // Default: 10 seconds

	private String apiKey;

	public HypixelAPI(String apiKey) {
		this.apiKey = apiKey;
	}

	public JSONObject getProfile(Player player) throws IOException {
		return this.getProfile(player.getName());
	}

	public JSONObject getProfile(String username) throws IOException {
		URL url = new URL(BASE_URL + "/player?key=" + apiKey + "&name=" + username);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestProperty("User-Agent", "NovaUniverseS");
		connection.connect();

		connection.setConnectTimeout(FETCH_TIMEOUT);
		connection.setReadTimeout(FETCH_TIMEOUT);

		InputStream responseStream = connection.getInputStream();

		InputStreamReader isr = new InputStreamReader(responseStream);
		BufferedReader rd = new BufferedReader(isr);
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		isr.close();
		responseStream.close();
		connection.disconnect();

		JSONObject responseJson = new JSONObject(response.toString());
		return responseJson;
	}

	@Nullable
	public static final String getBedwarsPreferences(JSONObject json) {
		try {
			return json.getJSONObject("player").getJSONObject("stats").getJSONObject("Bedwars").getString("favourites_2");
		} catch (JSONException e) {
			return null;
		}
	}
}