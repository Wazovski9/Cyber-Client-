package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.EventManager;
import dev.tr7zw.exordium.event.events.ButtonListener;
import dev.tr7zw.exordium.event.events.MouseMoveListener;
import dev.tr7zw.exordium.event.events.MouseUpdateListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
	@Shadow @Final private MinecraftClient client;

	@Inject(method = "updateMouse", at = @At("RETURN"))
	private void onMouseUpdate(CallbackInfo ci) {
		try {
			if (Exordium.INSTANCE != null && Exordium.INSTANCE.getEventManager() != null) {
				EventManager.fire(new MouseUpdateListener.MouseUpdateEvent());
			}
		} catch (Exception e) {
			Exordium.LOGGER.error("Error in MouseMixin updateMouse: " + e.getMessage());
		}
	}

	@Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
	private void onMouseMove(long window, double x, double y, CallbackInfo ci) {
		if (window != client.getWindow().getHandle()) return;
		
		try {
			if (Exordium.INSTANCE != null && Exordium.INSTANCE.getEventManager() != null) {
				MouseMoveListener.MouseMoveEvent event = new MouseMoveListener.MouseMoveEvent(window, x, y);
				EventManager.fire(event);
				if (event.isCancelled()) {
					ci.cancel();
				}
			}
		} catch (Exception e) {
			Exordium.LOGGER.error("Error in MouseMixin onCursorPos: " + e.getMessage());
		}
	}

	@Inject(method = "onMouseButton", at = @At("HEAD"))
	private void onMousePress(long window, int button, int action, int mods, CallbackInfo ci) {
		if (window != client.getWindow().getHandle()) return;
		
		try {
			if (Exordium.INSTANCE != null && Exordium.INSTANCE.getEventManager() != null) {
				EventManager.fire(new ButtonListener.ButtonEvent(button, window, action));
			}
		} catch (Exception e) {
			Exordium.LOGGER.error("Error in MouseMixin onMouseButton: " + e.getMessage());
		}
	}
}