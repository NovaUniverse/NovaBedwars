package net.novauniverse.bedwars.game.config.event;

import org.json.JSONObject;

public class BedBreak extends BedwarsEvent {
	public BedBreak(EventType eventType, int timeLeft, String name) {
		super(eventType, timeLeft, name);
	}

	public static BedBreak fromJSON(JSONObject json) {
		String name = json.getString("name");
		EventType eventType = json.getEnum(EventType.class, "event");
		int timeLeft = json.getInt("time");
		return new BedBreak(eventType, timeLeft, name);
	}
}