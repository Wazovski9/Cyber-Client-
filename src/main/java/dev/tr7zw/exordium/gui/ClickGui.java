package dev.tr7zw.exordium.gui;

import java.util.ArrayList;
import java.util.List;
import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGui extends Screen {
    public static final ClickGui INSTANCE = new ClickGui();
    private List<Window> frames = new ArrayList<>();

    public List<Window> getFrames() {
        return this.frames;
    }

    public ClickGui() {
        super(Text.literal("Gulo"));
        
        // Create ONE unified window with all modules from all categories
        this.frames.add(new Window("Gulo", 20, 20, 100, 15));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (Window frame : this.frames) {
            frame.updatePosition(mouseX, mouseY);
            frame.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Window frame : this.frames) {
            frame.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Window frame : this.frames) {
            frame.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Window frame : this.frames) {
            if (frame.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
