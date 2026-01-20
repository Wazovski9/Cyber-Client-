package dev.tr7zw.exordium.gui.components;

import dev.tr7zw.exordium.module.setting.MinMaxSetting;
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

import static dev.tr7zw.exordium.Exordium.mc;

public class MinMaxSlider extends Component {

    private MinMaxSetting minMaxSetting;
    private boolean slidingMin = false;
    private boolean slidingMax = false;

    public MinMaxSlider(Setting<?> setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.minMaxSetting = (MinMaxSetting) setting;
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

        // Handle sliding for min value
        if (slidingMin) {
            if (diff == 0) {
                minMaxSetting.setMinValue(minMaxSetting.getMin());
            } else {
                double percentFilled = diff / parent.parent.width;
                double interpolatedValue = minMaxSetting.getMin() + percentFilled * (minMaxSetting.getMax() - minMaxSetting.getMin());
                minMaxSetting.setMinValue(Math.min(interpolatedValue, minMaxSetting.getMaxValue()));
            }
        }

        // Handle sliding for max value
        if (slidingMax) {
            if (diff == 0) {
                minMaxSetting.setMaxValue(minMaxSetting.getMin());
            } else {
                double percentFilled = diff / parent.parent.width;
                double interpolatedValue = minMaxSetting.getMin() + percentFilled * (minMaxSetting.getMax() - minMaxSetting.getMin());
                minMaxSetting.setMaxValue(Math.max(interpolatedValue, minMaxSetting.getMinValue()));
            }
        }

        // Calculate slider positions
        int minRenderWidth = (int) (parent.parent.width * (minMaxSetting.getMinValue() - minMaxSetting.getMin()) / (minMaxSetting.getMax() - minMaxSetting.getMin()));
        int maxRenderWidth = (int) (parent.parent.width * (minMaxSetting.getMaxValue() - minMaxSetting.getMin()) / (minMaxSetting.getMax() - minMaxSetting.getMin()));
        
        minRenderWidth = MathHelper.clamp(minRenderWidth, 0, parent.parent.width);
        maxRenderWidth = MathHelper.clamp(maxRenderWidth, 0, parent.parent.width);

        // Filled portion between min and max
        drawContext.fill(
            parent.parent.x + minRenderWidth,
            parent.parent.y + parent.offset + offset,
            parent.parent.x + maxRenderWidth,
            parent.parent.y + parent.offset + offset + parent.parent.height,
            new Color(100, 234, 255, 160).getRGB()
        );

        // Text showing range
        String displayText = minMaxSetting.getName().toString() + ": " + 
            (int)minMaxSetting.getMinValue() + "-" + (int)minMaxSetting.getMaxValue();
        
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
    public void mouseReleased(double mouseX, double mouseY, int button) {
        slidingMin = false;
        slidingMax = false;
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) { // Only left click
            // Calculate slider positions
            int minRenderWidth = (int) (parent.parent.width * (minMaxSetting.getMinValue() - minMaxSetting.getMin()) / (minMaxSetting.getMax() - minMaxSetting.getMin()));
            int maxRenderWidth = (int) (parent.parent.width * (minMaxSetting.getMaxValue() - minMaxSetting.getMin()) / (minMaxSetting.getMax() - minMaxSetting.getMin()));

            // Calculate relative mouse position
            double relativeX = mouseX - parent.parent.x;

            // Determine which value to adjust based on click position
            double minDistance = Math.abs(relativeX - minRenderWidth);
            double maxDistance = Math.abs(relativeX - maxRenderWidth);

            if (minDistance < maxDistance) {
                slidingMin = true;
            } else {
                slidingMax = true;
            }

            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
        }
    }
}
