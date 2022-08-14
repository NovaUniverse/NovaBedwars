package net.novauniverse.bedwars.game.modules;

public class HypixelAPIRequestCooldown {
	private String name;
	private int cooldown;
	
	public HypixelAPIRequestCooldown(String name, int cooldown) {
		this.name = name;
		this.cooldown = cooldown;
	}
	
	public String getName() {
		return name;
	}
	
	public void decrement() {
		cooldown--;
	}
	
	public boolean hasExpired() {
		return cooldown <= 0;
	}
}