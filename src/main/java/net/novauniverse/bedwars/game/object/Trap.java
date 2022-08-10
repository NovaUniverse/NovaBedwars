package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.game.enums.TrapType;

public class Trap {
	private final TrapType trapType;

	public Trap(TrapType type) {
		trapType = type;
	}

	public TrapType getTrapType() {
		return trapType;
	}
}