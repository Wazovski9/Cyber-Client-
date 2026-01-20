package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.module.setting.KeybindSetting;
import dev.tr7zw.exordium.module.setting.Setting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static dev.tr7zw.exordium.Exordium.mc;

public class KeybindBox extends Component {

    private KeybindSetting keybindSetting;
    private boolean shouldGetKey = false;

    public KeybindBox(Setting<?> setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.keybindSetting = (KeybindSetting) setting;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);
        
        // Background
        drawContext.fill(
            parent.parent.x,
            parent.parent.y + parent.offset + offset,
            parent.parent.x + parent.parent.width,
            parent.parent.y + parent.offset + offset + parent.parent.height,
            new Color(40, 40, 40, 60).getRGB()
        );

        // Highlight on hover
        if (isHovered(mouseX, mouseY)) {
            drawContext.fill(
                parent.parent.x,
                parent.parent.y + parent.offset + offset,
                parent.parent.x + parent.parent.width,
                parent.parent.y + parent.offset + offset + parent.parent.height,
                0x70303070
            );
        }

        String keyName = keybindSetting.getKey() < 0 ? "None" : InputUtil.fromKeyCode(keybindSetting.getKey(), -1).getLocalizedText().getString();
        String displayText = shouldGetKey ? "..." : "Bind: " + keyName;

        mc.textRenderer.draw(
            displayText,
            (float) parent.parent.x + 2.0f,
            (float) parent.parent.y + (float) parent.offset + (float) offset + 2.0f,
            0xCFE0CF,
            false,
            matrices.peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            TextRenderer.TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (shouldGetKey && parent.extended) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
                keybindSetting.setKey(-1);
            } else {
                keybindSetting.setKey(keyCode);
            }
            shouldGetKey = false;
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0 && parent.extended) {
            shouldGetKey = true;
        }
    }
}
