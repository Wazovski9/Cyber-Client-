package dev.tr7zw.exordium.utils;

import dev.tr7zw.exordium.module.Category;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ColorHelper {

    public static int getCategoryRGB(final Category category) {
        int hackColor = 0xffff8400;

        if(category.equals(Category.COMBAT)) hackColor = 0xfffc0303;
        else if(category.equals(Category.MISC)) hackColor = 0xff42ffd6;
        else if(category.equals(Category.RENDER)) hackColor = 0xff00ff00;
        else if(category.equals(Category.CLIENT)) hackColor = 0xffff00c3;

        return hackColor;
    }

    public static Color fade(final Color color, final float delay, final int opacity) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(System.currentTimeMillis() % 2000L / delay % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new Color(red, green, blue, opacity);
    }

    public static Color alphaIntegrate(final Color color, final float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }
}
