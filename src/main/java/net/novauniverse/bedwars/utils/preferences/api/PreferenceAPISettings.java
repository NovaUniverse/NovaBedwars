package net.novauniverse.bedwars.utils.preferences.api;

public class PreferenceAPISettings {
	private boolean enabled;
	private String url;
	private String key;

	public PreferenceAPISettings(boolean enabled, String url, String key) {
		this.enabled = enabled;
		this.url = url;
		this.key = key;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getKey() {
		return key;
	}

	public String getUrl() {
		return url;
	}
}