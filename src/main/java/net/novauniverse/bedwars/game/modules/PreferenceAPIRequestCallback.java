package net.novauniverse.bedwars.game.modules;

import javax.annotation.Nullable;

public interface PreferenceAPIRequestCallback {
	public void onResult(boolean success, @Nullable Exception exception);
}