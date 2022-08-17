package net.novauniverse.bedwars.game.object;

public class Trap {
	private final net.novauniverse.bedwars.game.enums.Trap trapType;

	public Trap(net.novauniverse.bedwars.game.enums.Trap type) {
		trapType = type;
	}

	public net.novauniverse.bedwars.game.enums.Trap getTrapType() {
		return trapType;
	}
}