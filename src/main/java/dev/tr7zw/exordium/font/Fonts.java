package dev.tr7zw.exordium.font;

import net.minecraft.util.Identifier;
import java.awt.Font;
import java.io.InputStream;

public final class Fonts {
	public static final Identifier FONT_PATH = new Identifier("argon", "font/font.ttf");
	public static GlyphPageFontRenderer QUICKSAND;

	static {
		try {
			InputStream is = Fonts.class.getClassLoader().getResourceAsStream("assets/argon/font/font.ttf");
			if (is == null) {
				throw new RuntimeException("Font file not found: " + FONT_PATH);
			}
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			QUICKSAND = GlyphPageFontRenderer.createFromFont(font, 40, false, false, false);
		} catch (Exception e) {
			e.printStackTrace();
			// Fallback to Minecraft's default font
			QUICKSAND = null;
		}
	}
}
