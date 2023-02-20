package net.novauniverse.bedwars.game.config.event;

public class BedwarsEvent {
	private String name;
	private int timeLeft;
	private EventType eventType;

	public BedwarsEvent(EventType eventType, int timeLeft, String name) {
		this.name = name;
		this.timeLeft = timeLeft;
		this.eventType = eventType;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public boolean isFinished() {
		return timeLeft <= 0;
	}

	public void decrement() {
		if (timeLeft > 0) {
			this.timeLeft--;
		}
	}

	public String getName() {
		return name;
	}

	public EventType getEventType() {
		return eventType;
	}
}
