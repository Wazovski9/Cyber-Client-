package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.module.setting.ModeSetting;
import dev.tr7zw.exordium.module.setting.Setting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

import static dev.tr7zw.exordium.Exordium.mc;

public class ModeBox extends Component {

    private ModeSetting modeSetting;

    public ModeBox(Setting<?> setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.modeSetting = (ModeSetting) setting;
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

        // Text with current mode
        String displayText = modeSetting.getName().toString() + ": " + modeSetting.getMode();
        
        mc.textRenderer.draw(
            displayText,
            (float) parent.parent.x + 2.0f,
            (float) parent.parent.y + (float) parent.offset + (float) offset + 2.0f,
            0xE0E0E0,
            false,
            matrices.peek().getPositionMatrix(),
            mc.getBufferBuilders().getEntityVertexConsumers(),
            TextRenderer.TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        );
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && parent.extended) {
            // Left click OR right click cycles
            modeSetting.cycle();
            parent.parent.updateButtons();
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
        }
    }
}
