package dev.tr7zw.exordium.module.modules.misc;

import dev.tr7zw.exordium.event.events.TickListener;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.module.setting.NumberSetting;
import dev.tr7zw.exordium.utils.EncryptedString;
import dev.tr7zw.exordium.utils.TimerUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public final class FP extends Module implements TickListener {
    private final NumberSetting delay = new NumberSetting(EncryptedString.of("Delay"), 0, 1000, 0, 1);
    private final BooleanSetting blocks = new BooleanSetting(EncryptedString.of("Blocks"), true)
            .setDescription(EncryptedString.of("Fast place blocks"));
    private final BooleanSetting enderPearls = new BooleanSetting(EncryptedString.of("Ender Pearls"), false)
            .setDescription(EncryptedString.of("Fast place ender pearls"));
    private final BooleanSetting expBottles = new BooleanSetting(EncryptedString.of("Experience Bottles"), false)
            .setDescription(EncryptedString.of("Fast place experience bottles"));
    private final TimerUtils timer = new TimerUtils();

    public FP() {
        super(EncryptedString.of("FastPlace"), -1, Category.MISC);
        addSettings(delay, blocks, enderPearls, expBottles);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        super.onDisable();
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {

    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null) return;
        if (mc.player == null) return;

        Item mainHand = mc.player.getMainHandStack().getItem();
        Item offHand = mc.player.getOffHandStack().getItem();

        boolean isValidItem = false;
        boolean requiresBlockTarget = false;

        // Check if holding valid items
        if (blocks.getValue() && (mainHand instanceof BlockItem || offHand instanceof BlockItem)) {
            isValidItem = true;
            requiresBlockTarget = true;
        } else if (enderPearls.getValue() && (mainHand == Items.ENDER_PEARL || offHand == Items.ENDER_PEARL)) {
            isValidItem = true;
            requiresBlockTarget = false;
        } else if (expBottles.getValue() && (mainHand == Items.EXPERIENCE_BOTTLE || offHand == Items.EXPERIENCE_BOTTLE)) {
            isValidItem = true;
            requiresBlockTarget = false;
        }

        if (!isValidItem) return;

        // Check for block target if required
        if (requiresBlockTarget && (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK)) return;

        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
            if (timer.hasReached(delay.getValue())) {
                if (requiresBlockTarget) {
                    mc.interactionManager.interactBlock(mc.player, mc.player.getActiveHand(), (BlockHitResult)mc.crosshairTarget);
                } else {
                    // For items like ender pearls and exp bottles, use item directly
                    mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
                }
                timer.reset();
            }
        }
    }
} 