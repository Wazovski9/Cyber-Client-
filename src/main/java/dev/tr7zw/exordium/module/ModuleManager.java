package dev.tr7zw.exordium.module;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.events.ButtonListener;
import dev.tr7zw.exordium.module.modules.client.CG;
import dev.tr7zw.exordium.module.modules.client.SD;
import dev.tr7zw.exordium.module.modules.combat.*;
import dev.tr7zw.exordium.module.modules.misc.*;
import dev.tr7zw.exordium.module.setting.KeybindSetting;
import dev.tr7zw.exordium.utils.EncryptedString;

import static dev.tr7zw.exordium.Exordium.mc;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager implements ButtonListener {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		addModules();
		addKeybinds();
	}

	public void addModules() {
		//Combat
		add(new AT());  // Aim Assist
		add(new SD());  // Self Destruct
		add(new NHD()); // No Hit Delay
		add(new KTB()); // Krypton Trigger Bot
		add(new ATB()); // Advanced Trigger Bot

		//Misc
		add(new FP());  // FastPlace
		add(new SP());  // Sprint
		add(new AFW()); // Auto Firework

		//Client
		add(new CG());  // Click GUI
	}

	public List<Module> getEnabledModules() {
		return modules.stream()
				.filter(Module::isEnabled)
				.toList();
	}

	public List<Module> getModules() {
		return modules;
	}

	public void addKeybinds() {
		Exordium.INSTANCE.getEventManager().add(ButtonListener.class, this);

		for (Module module : modules)
			module.addSetting(new KeybindSetting(EncryptedString.of("Keybind"), module.getKey(), true).setDescription(EncryptedString.of("Key to enabled the module")));
	}

	public List<Module> getModulesInCategory(Category category) {
		return modules.stream()
				.filter(module -> module.getCategory() == category)
				.toList();
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> moduleClass) {
		return (T) modules.stream()
				.filter(moduleClass::isInstance)
				.findFirst()
				.orElse(null);
	}

	public void add(Module module) {
		modules.add(module);
	}

	@Override
	public void onButtonPress(ButtonEvent event) {
		if(!SD.destruct && mc.currentScreen == null) {
			modules.forEach(module -> {
				// Find the KeybindSetting for this module and check its key
				module.getSettings().stream()
					.filter(setting -> setting instanceof KeybindSetting)
					.map(setting -> (KeybindSetting) setting)
					.filter(keybind -> keybind.isModuleKey() && keybind.getKey() == event.button && event.action == GLFW.GLFW_PRESS)
					.findFirst()
					.ifPresent(keybind -> module.toggle());
			});
		}
	}
}
