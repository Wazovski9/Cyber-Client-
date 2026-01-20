package dev.tr7zw.exordium.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.gui.components.Component;
import dev.tr7zw.exordium.gui.components.ModuleButton;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;

import static dev.tr7zw.exordium.Exordium.mc;

public class Window {
    public int x;
    public int y;
    public int width;
    public int height;
    public int dragX;
    public int dragY;
    public String categoryName;
    public boolean dragging;
    public boolean extended;
    public List<ModuleButton> buttons;

    public Window(String name, int x, int y, int width, int height) {
        this.categoryName = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dragging = false;
        this.extended = true; // Start extended
        this.buttons = new ArrayList<>();

        int offset = height;
        // Add ALL modules from ALL categories to this one window
        for (Category category : Category.values()) {
            for (Module module : Exordium.INSTANCE.getModuleManager().getModulesInCategory(category)) {
                // Filter out unwanted modules
                String moduleName = module.getName().toString().toLowerCase();
                if (moduleName.contains("totem") ||
                    moduleName.contains("wtap") ||
                    moduleName.contains("axe") ||
                    moduleName.contains("clicker") ||
                    moduleName.contains("jump") ||
                    moduleName.contains("invidia") ||
                    moduleName.contains("nvidia") ||
                    moduleName.contains("hud") ||
                    moduleName.contains("friend") ||
                    moduleName.contains("team")) {
                    continue; // Skip these modules
                }
                
                buttons.add(new ModuleButton(module, this, offset));
                offset += height;
            }
        }
        
        updateButtons();
    }

    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);

        // Window header with purple/pink color
        drawContext.fill(x, y, x + width, y + height, new Color(156, 81, 255).getRGB());
        
        // Category name
        mc.textRenderer.draw(
            categoryName,
            (float)(x + 2),
            (float)(y + 2),
            -1,
            false,
            matrices.getMatrices().peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            TextRenderer.TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
        
        // Expand/collapse arrow
        String arrow = extended ? "▼" : "^";
        mc.textRenderer.draw(
            arrow,
            (float)(x + width - 3 - mc.textRenderer.getWidth("-")),
            (float)(y + 2),
            -1,
            false,
            matrices.getMatrices().peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            TextRenderer.TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        // Render module buttons if extended
        if (extended) {
            for (ModuleButton mb : buttons) {
                mb.render(matrices.getMatrices(), mouseX, mouseY, delta);
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                // Start dragging
                dragging = true;
                dragX = (int)mouseX - x;
                dragY = (int)mouseY - y;
            } else if (button == 1) {
                // Toggle expand/collapse
                extended = !extended;
            }
        }
        
        if (extended) {
            for (ModuleButton moduleButton : buttons) {
                moduleButton.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
        }
        
        for (ModuleButton mb : buttons) {
            mb.mouseReleased(mouseX, mouseY, button);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleButton mb : buttons) {
            if (mb.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > x 
            && mouseX < (x + width) 
            && mouseY > y 
            && mouseY < (y + height);
    }

    public void updatePosition(double mouseX, double mouseY) {
        if (dragging) {
            x = (int)(mouseX - dragX);
            y = (int)(mouseY - dragY);
        }
    }

    public void updateButtons() {
        int offset = height;
        
        for (ModuleButton button : buttons) {
            button.offset = offset;
            offset += height;
            
            if (button.extended) {
                for (Component component : button.components) {
                    offset += height;
                }
            }
        }
    }
}
