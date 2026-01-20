package dev.tr7zw.exordium.module.modules.combat;

import dev.tr7zw.exordium.event.events.AttackListener;
import dev.tr7zw.exordium.event.events.TickListener;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.module.setting.KeybindSetting;
import dev.tr7zw.exordium.module.setting.ModeSetting;
import dev.tr7zw.exordium.module.setting.NumberSetting;
import dev.tr7zw.exordium.utils.EncryptedString;
import dev.tr7zw.exordium.utils.InventoryUtils;
import dev.tr7zw.exordium.utils.WorldUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public final class ATB extends Module implements TickListener, AttackListener {
    private enum Mode {
        Automatic,
        Manual
    }

    private final ModeSetting<Mode> mode = new ModeSetting<>(EncryptedString.of("Mode"), Mode.Automatic, Mode.class);
    private final KeybindSetting activateKey = new KeybindSetting(EncryptedString.of("Activate Key"), -1, false);
    private final BooleanSetting switchBack = new BooleanSetting(EncryptedString.of("Switch Back"), true)
            .setDescription(EncryptedString.of("Switches back to the slot"));
    private final NumberSetting switchdelay = new NumberSetting(EncryptedString.of("Switch Delay"), 0, 3, 0, 1);

    private int switchClock, previousSlot = -1;
    private boolean shouldSwitchBack = false;

    public ATB() {
        super(EncryptedString.of("Shield Breaker"),
                -1,
                Category.COMBAT);
        addSettings(mode, activateKey, switchBack, switchdelay);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        eventManager.add(AttackListener.class, this);
        reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        eventManager.remove(TickListener.class, this);
        eventManager.remove(AttackListener.class, this);
        super.onDisable();
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, float partialTicks) {
    }

    @Override
    public void onTick() {
        if (shouldSwitchBack && switchBack.getValue()) {
            switchBack();
            return;
        }

        boolean shouldBreakShield = false;

        if (mode.getMode() == Mode.Automatic) {
            shouldBreakShield = true;
        } else if (mode.getMode() == Mode.Manual) {
            shouldBreakShield = isKeyPressed(activateKey.getKey());
        }

        if (!shouldBreakShield) return;

        // Get targeted entity from crosshair
        if (!(mc.crosshairTarget instanceof EntityHitResult entityHit) ||
            mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        Entity entity = entityHit.getEntity();
        if (!(entity instanceof PlayerEntity player)) return;
        if (!player.isBlocking() || WorldUtils.isShieldFacingAway(player)) return;

        if (switchBack.getValue() && previousSlot == -1) {
            previousSlot = mc.player.getInventory().selectedSlot;
        }

        if (InventoryUtils.selectItemFromHotbar(item -> item instanceof AxeItem)) {
            mc.interactionManager.attackEntity(mc.player, player);
            mc.player.swingHand(Hand.MAIN_HAND);

            if (switchBack.getValue() && previousSlot != -1) {
                shouldSwitchBack = true;
                switchClock = 0;
            }
        }
    }

    private void switchBack() {
        if (switchClock < switchdelay.getValueInt()) {
            switchClock++;
            return;
        }

        if (previousSlot != -1) {
            mc.player.getInventory().selectedSlot = previousSlot;
        }

        reset();
    }

    private void reset() {
        previousSlot = -1;
        switchClock = 0;
        shouldSwitchBack = false;
    }

    private boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode <= 4) {
            // Mouse button
            return org.lwjgl.glfw.GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), keyCode) ==
                   org.lwjgl.glfw.GLFW.GLFW_PRESS;
        } else {
            // Keyboard key
            return org.lwjgl.glfw.GLFW.glfwGetKey(mc.getWindow().getHandle(), keyCode) ==
                   org.lwjgl.glfw.GLFW.GLFW_PRESS;
        }
    }

    @Override
    public void onAttack(AttackEvent event) {
        // Only cancel if we're not manually activating
        if (mode.getMode() == Mode.Manual) {
            event.cancel();
        }
    }
}
