package net.novauniverse.bedwars.utils;

import net.novauniverse.bedwars.game.enums.Trap;

public abstract class TrapCallback<T, V> {
	private Trap trap;

	public void setTrap(Trap trap) {
		this.trap = trap;
	}

	public Trap getTrap() {
		return trap;
	}

	public abstract void execute(T val, V val2);
}