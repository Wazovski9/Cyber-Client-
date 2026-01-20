package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.module.setting.BooleanSetting;
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

public class CheckBox extends Component {

    private BooleanSetting booleanSetting;

    public CheckBox(Setting<?> setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.booleanSetting = (BooleanSetting) setting;
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

        // Text color: green if enabled, gray if disabled
        int color = booleanSetting.getValue() ? 0x00FF00 : 0xE0E0E0;
        
        mc.textRenderer.draw(
            booleanSetting.getName().toString(),
            (float) parent.parent.x + 2.0f,
            (float) parent.parent.y + (float) parent.offset + (float) offset + 2.0f,
            color,
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
        if (isHovered(mouseX, mouseY) && button == 0 && parent.extended) {
            booleanSetting.setValue(!booleanSetting.getValue());
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
        }
    }
}
