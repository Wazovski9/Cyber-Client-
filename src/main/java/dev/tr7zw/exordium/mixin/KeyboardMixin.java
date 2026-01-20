package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.EventManager;
import dev.tr7zw.exordium.event.events.ButtonListener;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 0))
	private void onPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (window != client.getWindow().getHandle()) return;
		
		try {
			if (Exordium.INSTANCE != null && Exordium.INSTANCE.getEventManager() != null) {
				EventManager.fire(new ButtonListener.ButtonEvent(key, window, action));
			}
		} catch (Exception e) {
			Exordium.LOGGER.error("Error in KeyboardMixin: " + e.getMessage());
		}
	}
}
