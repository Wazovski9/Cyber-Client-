package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.module.setting.NumberSetting;
import dev.tr7zw.exordium.module.setting.Setting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static dev.tr7zw.exordium.Exordium.mc;

public class Slider extends Component {

    private NumberSetting numberSetting;
    private boolean sliding = false;

    public Slider(Setting<?> setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.numberSetting = (NumberSetting) setting;
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

        double diff = Math.min(parent.parent.width, Math.max(0, mouseX - parent.parent.x));

        if (sliding) {
            if (diff == 0) {
                numberSetting.setValue(numberSetting.getMin());
            } else {
                double percentFilled = diff / parent.parent.width;
                double interpolatedValue = numberSetting.getMin() + percentFilled * (numberSetting.getMax() - numberSetting.getMin());
                interpolatedValue = roundToPlace(interpolatedValue, 2);
                numberSetting.setValue(interpolatedValue);
            }
        }

        // Calculate slider fill width
        int renderWidth = (int) (parent.parent.width * (numberSetting.getValue() - numberSetting.getMin()) / (numberSetting.getMax() - numberSetting.getMin()));
        int actualRenderWidth = MathHelper.clamp(renderWidth, 0, parent.parent.width);

        // Filled portion
        drawContext.fill(
            parent.parent.x,
            parent.parent.y + parent.offset + offset,
            parent.parent.x + actualRenderWidth,
            parent.parent.y + parent.offset + offset + parent.parent.height,
            new Color(100, 234, 255, 160).getRGB()
        );

        // Text
        mc.textRenderer.draw(
            numberSetting.getName().toString() + ": " + numberSetting.getValue(),
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
    public void mouseReleased(double mouseX, double mouseY, int button) {
        sliding = false;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            sliding = true;
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
        }
    }

    private double roundToPlace(double value, int place) {
        if (place < 0) {
            return value;
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(place, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
