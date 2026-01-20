package dev.tr7zw.exordium.module.modules.client;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.events.ButtonListener;
import dev.tr7zw.exordium.event.events.PacketReceiveListener;
import dev.tr7zw.exordium.gui.ClickGui;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.module.setting.ModeSetting;
import dev.tr7zw.exordium.module.setting.NumberSetting;
import dev.tr7zw.exordium.utils.EncryptedString;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import org.lwjgl.glfw.GLFW;

public final class CG extends Module implements PacketReceiveListener, ButtonListener {
	public static final NumberSetting red = new NumberSetting(EncryptedString.of("Red"), 0, 255, 255, 1);
	public static final NumberSetting green = new NumberSetting(EncryptedString.of("Green"), 0, 255, 0, 1);
	public static final NumberSetting blue = new NumberSetting(EncryptedString.of("Blue"), 0, 255, 50, 1);
	public static final BooleanSetting customFont = new BooleanSetting(EncryptedString.of("Custom Font"), true);
	public static final NumberSetting roundQuads = new NumberSetting(EncryptedString.of("Roundness"), 1, 10, 10, 1);
	public static final ModeSetting<AnimationMode> animationMode = new ModeSetting<>(EncryptedString.of("Animations"), AnimationMode.Normal, AnimationMode.class);

	public enum AnimationMode {
		Normal, Positive, Off;
	}

	public CG() {
		super(EncryptedString.of("Click GUI"),
				GLFW.GLFW_KEY_INSERT,  // Changed to INSERT key
				Category.CLIENT);
		addSettings(red, green, blue, roundQuads, animationMode);
		setEnabled(false);
	}

	@Override
	public void onEnable() {
		if (!isEnabled()) return;
		eventManager.add(PacketReceiveListener.class, this);
		eventManager.add(ButtonListener.class, this);

		if (mc.currentScreen != null && !(mc.currentScreen instanceof ClickGui) && !mc.currentScreen.getClass().getSimpleName().toLowerCase().contains("title")) {
			Exordium.INSTANCE.previousScreen = mc.currentScreen;
		}

		if (Exordium.INSTANCE.clickGui == null) {
			Exordium.INSTANCE.clickGui = new ClickGui();
		}

		mc.execute(() -> {
			if (isEnabled()) {
				mc.setScreenAndRender(Exordium.INSTANCE.clickGui);
			}
		});

		super.onEnable();
	}

	@Override
	public void onDisable() {
		eventManager.remove(PacketReceiveListener.class, this);
		eventManager.remove(ButtonListener.class, this);

		if (mc.currentScreen instanceof ClickGui) {
			mc.setScreenAndRender(Exordium.INSTANCE.previousScreen);
		}

		super.onDisable();
	}

	@Override
	public void onRender3D(MatrixStack matrixStack, float partialTicks) {
	}

	@Override
	public void onButtonPress(ButtonListener.ButtonEvent event) {
		try {
			if (event.button == getKey() && event.action == GLFW.GLFW_PRESS) {
				return;
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
		if (event.packet instanceof OpenScreenS2CPacket) {
			event.cancel();
		}
	}
}