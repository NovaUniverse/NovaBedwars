package net.novauniverse.bedwars.utils.preferences.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PreferenceAPI {
	public static int FETCH_TIMEOUT = 10 * 1000; // Default: 10 seconds

	private final PreferenceAPISettings settings;

	public PreferenceAPI(PreferenceAPISettings settings) {
		this.settings = settings;
	}

	// look i get kinda bothered when theres no getters
	// The settings should never get accessed by anything but this class since it
	// contains api keys
	/*
	 * public PreferenceAPISettings getSettings() { return settings; }
	 */

	@Nullable
	public JSONArray getPreferences(Player player) throws IOException, JSONException {
		return this.getPreferences(player.getUniqueId());
	}

	@Nullable
	public JSONArray getPreferences(UUID uuid) throws IOException, JSONException {
		URL url = new URL(settings.getUrl() + "/" + uuid.toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestProperty("User-Agent", "NovaUniverse Bedwars");

		connection.setConnectTimeout(FETCH_TIMEOUT);
		connection.setReadTimeout(FETCH_TIMEOUT);

		connection.connect();

		int code = connection.getResponseCode();
		if (code == 404) {
			connection.disconnect();
			return null;
		}

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

		JSONObject json = new JSONObject(response.toString());

		return json.getJSONArray("data");
	}

	public boolean updatePreferences(Player player, JSONArray json) throws IOException {
		return this.updatePreferences(player.getUniqueId(), json);
	}

	public boolean updatePreferences(UUID uuid, JSONArray json) throws IOException {
		URL url = new URL(settings.getUrl() + "/" + uuid.toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("accept", "application/json");
		connection.setRequestProperty("User-Agent", "NovaUniverse Bedwars");

		connection.setDoOutput(true);

		connection.setConnectTimeout(FETCH_TIMEOUT);
		connection.setReadTimeout(FETCH_TIMEOUT);

		String data = json.toString();

		OutputStream os = connection.getOutputStream();
		byte[] input = data.getBytes(StandardCharsets.UTF_8);
		os.write(input, 0, input.length);
		os.close();

		int code = connection.getResponseCode();

		connection.disconnect();

		return code == 200;
	}
}