package dev.tr7zw.exordium.module;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.EventManager;
import dev.tr7zw.exordium.module.setting.Setting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module implements Serializable {
	private final List<Setting<?>> settings = new ArrayList<>();
	public final EventManager eventManager = Exordium.INSTANCE.eventManager;
	protected MinecraftClient mc = MinecraftClient.getInstance();
	private CharSequence name;
	private CharSequence description;
	private boolean enabled;
	private int key;
	private Category category;

	public Module(CharSequence name, int key, Category category) {
		this.name = name;
		this.description = description;
		this.enabled = false;
		this.key = key;
		this.category = category;
	}

	public Module(String name, Category category) {
	}

	public void toggle() {
		enabled = !enabled;
		if (enabled)
			onEnable();
		else onDisable();
	}

	public CharSequence getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public CharSequence getDescription() {
		return description;
	}

	public int getKey() {
		return key;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setName(CharSequence name) {
		this.name = name;
	}

	public void setDescription(CharSequence description) {
		this.description = description;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public List<Setting<?>> getSettings() {
		return settings;
	}

	public void onEnable() {}

	public void onDisable() {}

	public void addSetting(Setting<?> setting) {
		this.settings.add(setting);
	}

	public void addSettings(Setting<?>... settings) {
		this.settings.addAll(Arrays.asList(settings));
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled)
			onEnable();
		else onDisable();
	}

	public void setEnabledStatus(boolean enabled) {
		this.enabled = enabled;
	}

	public abstract void onRender3D(MatrixStack matrixStack, float partialTicks);
}
