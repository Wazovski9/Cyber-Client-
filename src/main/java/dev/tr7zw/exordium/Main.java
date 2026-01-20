package dev.tr7zw.exordium;

import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		try {
			new Exordium();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
