package dev.tr7zw.exordium.module.modules.combat;

import dev.tr7zw.exordium.Exordium;
import dev.tr7zw.exordium.event.events.AttackListener;
import dev.tr7zw.exordium.event.events.TickListener;
import dev.tr7zw.exordium.module.Category;
import dev.tr7zw.exordium.module.Module;
import dev.tr7zw.exordium.module.setting.BooleanSetting;
import dev.tr7zw.exordium.module.setting.MinMaxSetting;
import dev.tr7zw.exordium.utils.EncryptedString;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public final class KTB extends Module implements TickListener, AttackListener {
    private final BooleanSetting onLeftClick = new BooleanSetting(EncryptedString.of("On Left Click"), false)
            .setDescription(EncryptedString.of("Only gets triggered if holding down left click"));
    private final MinMaxSetting hitDelay = new MinMaxSetting(EncryptedString.of("Hit Delay"), 0, 20, 1, 1, 11);
    private final BooleanSetting checkShield = new BooleanSetting(EncryptedString.of("Check Shield"), false)
            .setDescription(EncryptedString.of("Checks if the player is blocking your hits with a shield"));
    private final BooleanSetting onlyCrit = new BooleanSetting(EncryptedString.of("Only Crit"), false)
            .setDescription(EncryptedString.of("Only does critical hits"));
    private final BooleanSetting whileAscend = new BooleanSetting(EncryptedString.of("While Ascending"), false)
            .setDescription(EncryptedString.of("Wont hit if you're ascending from a jump"));
    private final BooleanSetting allEntities = new BooleanSetting(EncryptedString.of("All Entities"), false)
            .setDescription(EncryptedString.of("Will attack all entities"));
    private final BooleanSetting sticky = new BooleanSetting(EncryptedString.of("Same Player"), false)
            .setDescription(EncryptedString.of("Hits the player that was recently attacked"));
    private final BooleanSetting onlyWeapon = new BooleanSetting(EncryptedString.of("Only Weapon"), false)
            .setDescription(EncryptedString.of("Only attacks when holding a weapon (sword, axe, etc.)"));

    private int sleep;

    public KTB() {
        super(EncryptedString.of("Krypton Trigger"),
                -1,
                Category.COMBAT);
        addSettings(onLeftClick, hitDelay, checkShield, whileAscend, sticky, onlyCrit, allEntities, onlyWeapon);
    }

    @Override
    public void onEnable() {
        eventManager.add(TickListener.class, this);
        eventManager.add(AttackListener.class, this);
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
        if (sleep > 0) {
            sleep--;
            return;
        }

        if (mc.currentScreen != null) return;
        if (mc.player == null) return;

        // Only work when holding a weapon if setting is enabled
        if (onlyWeapon.getValue() && !isHoldingWeapon()) return;

        if (onLeftClick.getValue() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS)
            return;

        // Don't attack while using shield or eating
        if (mc.player.isUsingItem())
            return;

        if (!whileAscend.getValue() && ((!mc.player.isOnGround() && mc.player.getVelocity().y > 0 && !mc.player.isSubmergedInWater() && !mc.player.getBlockStateAtPos().getBlock().equals(Blocks.COBWEB)) || (!mc.player.isOnGround() && mc.player.fallDistance <= 0.0F)))
            return;

        if (mc.crosshairTarget instanceof EntityHitResult entityHit && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = entityHit.getEntity();

            if (mc.player.getAttacking() != null && sticky.getValue() && entity != mc.player.getAttacking())
                return;

            if (entity instanceof PlayerEntity || entity instanceof ZombieEntity || (allEntities.getValue() && entity != null)) {
                if (entity instanceof PlayerEntity player) {
                    if (checkShield.getValue() && player.isBlocking() && !isShieldFacingAway(player)) 
                        return;
                }

                if (onlyCrit.getValue() && mc.player.fallDistance <= 0.0F) 
                    return;

                // Attack the entity
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(Hand.MAIN_HAND);

                sleep = hitDelay.getRandomValueInt();
            }
        }
    }

    @Override
    public void onAttack(AttackEvent event) {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS)
            event.cancel();
    }

    private boolean isShieldFacingAway(PlayerEntity player) {
        Vec3d playerPos = mc.player.getPos();
        Vec3d targetPos = player.getPos();
        Vec3d direction = targetPos.subtract(playerPos).normalize();

        float targetYaw = player.getYaw();
        double targetDirX = -Math.sin(Math.toRadians(targetYaw));
        double targetDirZ = Math.cos(Math.toRadians(targetYaw));
        Vec3d targetDirection = new Vec3d(targetDirX, 0, targetDirZ).normalize();

        double dotProduct = direction.dotProduct(targetDirection);

        return dotProduct > 0;
    }

    private boolean isHoldingWeapon() {
        Item item = mc.player.getMainHandStack().getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem;
    }
}
