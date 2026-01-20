package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.EventManager;
import dev.tr7zw.exordium.event.events.GameRenderListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow public abstract Matrix4f getBasicProjectionMatrix(double fov);

	@Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

	@Shadow @Final private Camera camera;

	@Shadow private MinecraftClient client;

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(float tickDelta, long startTime, boolean renderLevel, CallbackInfo ci) {
		MatrixStack matrixStack = new MatrixStack();
		EventManager.fire(new GameRenderListener.GameRenderEvent(matrixStack, tickDelta));
	}

	@Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
	private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
		// Block outline rendering logic can be added here if needed
	}
}