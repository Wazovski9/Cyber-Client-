package dev.tr7zw.exordium;

import dev.tr7zw.exordium.event.EventManager;
import dev.tr7zw.exordium.gui.ClickGui;
import dev.tr7zw.exordium.managers.TeamManager;
import dev.tr7zw.exordium.module.ModuleManager;
import dev.tr7zw.exordium.managers.UserManager;
import dev.tr7zw.exordium.utils.rotation.RotatorManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;

@SuppressWarnings("all")
public final class Exordium implements ClientModInitializer {
	public static final String MOD_ID = "exordium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	public RotatorManager rotatorManager;
	public UserManager userManager;
	public ModuleManager moduleManager;
	public EventManager eventManager;
	public TeamManager teamManager;
	public String version = "1.4.1";
	public static boolean BETA;
	public static Exordium INSTANCE;
	public boolean guiInitialized;
	public ClickGui clickGui;
	public Screen previousScreen = null;
	public long lastModified;
	public File argonJar;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Exordium Client");
		
		INSTANCE = this;
		this.eventManager = new EventManager();
		this.moduleManager = new ModuleManager();
		this.rotatorManager = new RotatorManager();
		this.userManager = new UserManager();
		this.teamManager = new TeamManager();
		this.clickGui = new ClickGui();

		this.getProfileManager().loadProfile();
		this.setLastModified();

		this.guiInitialized = false;
	}

	public UserManager getProfileManager() {
		return userManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public TeamManager getFriendManager() {
		return teamManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public ClickGui getClickGui() {
		return clickGui;
	}

	public void resetModifiedDate() {
		this.argonJar.setLastModified(lastModified);
	}

	public String getVersion() {
		return version;
	}

	public void setLastModified() {
		try {
			this.argonJar = new File(Exordium.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			this.lastModified = argonJar.lastModified();
		} catch (URISyntaxException ignored) {}
	}
}