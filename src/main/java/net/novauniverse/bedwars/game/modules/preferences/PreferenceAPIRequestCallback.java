package net.novauniverse.bedwars.game.modules.preferences;

import javax.annotation.Nullable;

public interface PreferenceAPIRequestCallback {
	void onResult(boolean success, @Nullable Exception exception);
}