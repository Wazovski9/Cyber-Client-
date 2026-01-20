package dev.tr7zw.exordium.module.modules.client;

import com.sun.jna.Memory;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.module.setting.Setting;
import dev.tr7zw.exordium.module.setting.StringSetting;
import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.gui.ClickGui;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import java.util.Base64;

import dev.tr7zw.exordium.utils.EncryptedString;
import dev.tr7zw.exordium.utils.Utils;
import net.minecraft.client.util.math.MatrixStack;

import java.io.File;

@SuppressWarnings("all")
public final class SD extends Module {
	public static boolean destruct = false;

	private final BooleanSetting replacecheat = new BooleanSetting("Replace Cheat", true);
	private final BooleanSetting saveLastModified = new BooleanSetting("SavaLastModified", true);
	private final StringSetting downloadURL = new StringSetting("url", "https://cdn.modrinth.com/data/DynYZEae/versions/itjAAfxl/exordium-fabric-1.4.1-mc1.21.jar");

	public SD() {
		super(EncryptedString.of("SelfDestruct"), -1, Category.CLIENT);
		addSettings(replacecheat, saveLastModified, downloadURL);
	}

	@Override
	public void onEnable() {
		destruct = true;

		Exordium.INSTANCE.getModuleManager().getModule(CG.class).setEnabled(false);
		setEnabled(false);

		Exordium.INSTANCE.getProfileManager().saveProfile();

		if (mc.currentScreen instanceof ClickGui) {
			Exordium.INSTANCE.guiInitialized = false;
			mc.currentScreen.close();
		}

		if (replacecheat.getValue()) {
			try {
				String modUrl = downloadURL.getValue();
				File currentJar = Utils.getCurrentJarPath();

				if (currentJar.exists())
					Utils.replaceModFile(modUrl, Utils.getCurrentJarPath());
			} catch (Exception ignored) {}
		}

		for (Module module : Exordium.INSTANCE.getModuleManager().getModules()) {
			module.setEnabled(false);

			module.setName(null);
			module.setDescription(null);

			for (Setting<?> setting : module.getSettings()) {
				setting.setName(null);
				setting.setDescription(null);

				if(setting instanceof StringSetting set)
					set.setValue(null);
			}
			module.getSettings().clear();
		}

		Runtime runtime = Runtime.getRuntime();

		if (saveLastModified.getValue())
			Exordium.INSTANCE.resetModifiedDate();

		for (int i = 0; i <= 10; i++) {
			runtime.gc();
			runtime.runFinalization();

			try {
				Thread.sleep(100 * i);
				Memory.purge();
				Memory.disposeAll();
			} catch (InterruptedException ignored) {}
		}
	}

	@Override
	public void onRender3D(MatrixStack matrixStack, float partialTicks) {

	}
}