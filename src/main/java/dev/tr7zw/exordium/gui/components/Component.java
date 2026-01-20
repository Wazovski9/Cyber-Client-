package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.module.setting.Setting;
import net.minecraft.client.util.math.MatrixStack;

public class Component {

    public Setting<?> setting;
    public ModuleButton parent;
    public int offset;

    public Component(Setting<?> setting, ModuleButton parent, int offset) {
        this.parent = parent;
        this.setting = setting;
        this.offset = offset;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > parent.parent.x 
            && mouseX < (parent.parent.x + parent.parent.width) 
            && mouseY > (parent.parent.y + parent.offset + offset) 
            && mouseY < (parent.parent.y + parent.offset + parent.parent.height + offset);
    }
}
