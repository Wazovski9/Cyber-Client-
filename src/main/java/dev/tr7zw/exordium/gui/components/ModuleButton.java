package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.gui.Window;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static dev.tr7zw.exordium.Exordium.mc;

public class ModuleButton {

    public Module module;
    public Window parent;
    public int offset;
    public List<Component> components;
    public boolean extended;

    public ModuleButton(Module module, Window parent, int offset) {
        this.module = module;
        this.parent = parent;
        this.offset = offset;
        this.extended = false;
        this.components = new ArrayList<>();

        int setOffset = 0; // Start at 0 relative to module button
        
        for (Setting<?> setting : module.getSettings()) {
            Component component = null;
            
            if (setting instanceof KeybindSetting) {
                setOffset += parent.height;
                component = new KeybindBox(setting, this, setOffset);
            } else if (setting instanceof MinMaxSetting) {
                setOffset += parent.height;
                component = new MinMaxSlider(setting, this, setOffset);
            } else if (setting instanceof BooleanSetting) {
                setOffset += parent.height;
                component = new CheckBox(setting, this, setOffset);
            } else if (setting instanceof ModeSetting) {
                setOffset += parent.height;
                component = new ModeBox(setting, this, setOffset);
            } else if (setting instanceof NumberSetting) {
                setOffset += parent.height;
                component = new Slider(setting, this, setOffset);
            }
            // Skip unsupported setting types (ItemSetting, etc.)
            // without incrementing offset so no blank space appears
            
            if (component != null) {
                components.add(component);
            }
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);
        
        // Module button background
        drawContext.fill(
            parent.x,
            parent.y + offset,
            parent.x + parent.width,
            parent.y + offset + parent.height,
            new Color(50, 50, 50, 160).getRGB()
        );
        
        // Hover effect
        if (isHovered(mouseX, mouseY)) {
            drawContext.fill(
                parent.x,
                parent.y + offset,
                parent.x + parent.width,
                parent.y + offset + parent.height,
                new Color(70, 70, 70, 100).getRGB()
            );
        }
        
        // Module name (green if enabled, gray if disabled)
        mc.textRenderer.draw(
            module.getName().toString(),
            (float) parent.x + 2.0f,
            (float) parent.y + (float) offset + 2.0f,
            module.isEnabled() ? 0x00FF00 : 0xE0E0E0,
            false,
            matrices.peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            TextRenderer.TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
        
        // Render settings if extended
        if (extended) {
            for (Component component : components) {
                component.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            // Left click: toggle module
            if (button == 0 && parent.extended) {
                module.toggle();
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
                return;
            }
            // Right click: expand/collapse settings
            else if (button == 1 && parent.extended) {
                extended = !extended;
                parent.updateButtons();
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
                return;
            }
        }

        // Forward clicks to components if extended
        if (extended) {
            for (Component component : components) {
                component.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (extended) {
            for (Component component : components) {
                component.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (extended) {
            for (Component component : components) {
                if (component.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > parent.x 
            && mouseX < (parent.x + parent.width) 
            && mouseY > (parent.y + offset) 
            && mouseY < (parent.y + offset + parent.height);
    }
}
