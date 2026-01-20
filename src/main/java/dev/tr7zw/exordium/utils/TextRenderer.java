package dev.tr7zw.exordium.utils;

import dev.tr7zw.exordium.font.Fonts;
import dev.tr7zw.exordium.module.modules.client.CG;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import static dev.tr7zw.exordium.Exordium.mc;


public final class TextRenderer {

	public static void drawString(CharSequence string, DrawContext context, int x, int y, int color) {
		if (string == null) return;
		boolean custom = CG.customFont.getValue() && Fonts.QUICKSAND != null;
		if (custom)
			Fonts.QUICKSAND.drawString(context.getMatrices(), string, x, y - 8, color);
		else drawMinecraftText(string, context, x, y, color);
	}

	public static int getWidth(CharSequence string) {
		if (string == null) return 0;
		boolean custom = CG.customFont.getValue() && Fonts.QUICKSAND != null;
		if (custom)
			return Fonts.QUICKSAND.getStringWidth(string);
		else return mc.textRenderer.getWidth(string.toString()) * 2;
	}

	public static void drawCenteredString(CharSequence string, DrawContext context, int x, int y, int color) {
		if (string == null) return;
		boolean custom = CG.customFont.getValue() && Fonts.QUICKSAND != null;
		if (custom)
			Fonts.QUICKSAND.drawString(context.getMatrices(), string, (x - (Fonts.QUICKSAND.getStringWidth(string) / 2)), y - 8, color);
		else drawCenteredMinecraftText(string, context, x, y, color);
	}

	public static void drawLargeString(CharSequence string, DrawContext context, int x, int y, int color) {
		if (string == null) return;
		boolean custom = CG.customFont.getValue() && Fonts.QUICKSAND != null;
		if (custom) {
			MatrixStack matrices = context.getMatrices();
			matrices.push();

			matrices.scale(1.4f, 1.4f, 1.4f);
			Fonts.QUICKSAND.drawString(context.getMatrices(), string, x, y - 8, color);
			matrices.scale(1, 1, 1);

			matrices.pop();
		} else
			drawLargerMinecraftText(string, context, x, y, color);
	}

	public static void drawMinecraftText(CharSequence string, DrawContext context, int x, int y, int color) {
		if (string == null) return;
		MatrixStack matrices = context.getMatrices();
		matrices.push();

		matrices.scale(2f, 2f, 2f);
		context.drawText(mc.textRenderer, string.toString(), (x) / 2, (y) / 2, color, false);
		matrices.scale(1, 1, 1);

		matrices.pop();
	}

	public static void drawLargerMinecraftText(CharSequence string, DrawContext context, int x, int y, int color) {
		if (string == null) return;
		MatrixStack matrices = context.getMatrices();
		matrices.push();

		matrices.scale(3, 3, 3);
		context.drawText(mc.textRenderer, (String) string, (x) / 3, (y) / 3, color, false);
		matrices.scale(1, 1, 1);

		matrices.pop();
	}

	public static void drawCenteredMinecraftText(CharSequence string, DrawContext context, int x, int y, int color) {
		if (string == null) return;
		MatrixStack matrices = context.getMatrices();
		matrices.push();

		matrices.scale(2f, 2f, 2f);
		context.drawText(mc.textRenderer, (String) string, (x / 2) - (mc.textRenderer.getWidth((String) string) / 2), (y) / 2, color, false);
		matrices.scale(1, 1, 1);

		matrices.pop();
	}
}