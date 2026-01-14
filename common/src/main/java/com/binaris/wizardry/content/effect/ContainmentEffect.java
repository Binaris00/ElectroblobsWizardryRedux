package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.content.data.ContainmentData;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.GeometryUtil;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ContainmentEffect extends MagicMobEffect {
    public ContainmentEffect() {
        super(MobEffectCategory.NEUTRAL, 0x7988cc);
    }

    public static float getContainmentDistance(int effectStrength) {
        return 15 - effectStrength * 4;
    }

    public static void onLivingUpdateEvent(EBLivingTick event) {
        LivingEntity entity = event.getEntity();
        if (!entity.isAlive()) return;
        ContainmentData data = Services.OBJECT_DATA.getContainmentData(entity);

        if (entity.tickCount % 20 == 0 && data.getContainmentPos() != null && !entity.hasEffect(EBMobEffects.CONTAINMENT.get()))
            data.setContainmentPos(null);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity target, int amplifier) {
        if (!target.isAlive()) return;
        float maxDistance = getContainmentDistance(amplifier);
        ContainmentData data = Services.OBJECT_DATA.getContainmentData(target);

        if (data.getContainmentPos() == null) {
            data.setContainmentPos(target.blockPosition().offset(-1, -1, -1));
        }

        Vec3 origin = GeometryUtil.getCentre(data.getContainmentPos());

        double x = target.xo, y = target.yo, z = target.zo;

        // Containment fields are cubes so we're dealing with each axis separately
        if (target.getBoundingBox().maxX > origin.x + maxDistance) x = origin.x + maxDistance - target.getBbWidth() / 2;
        if (target.getBoundingBox().minX < origin.x - maxDistance) x = origin.x - maxDistance + target.getBbWidth() / 2;

        if (target.getBoundingBox().maxY > origin.y + maxDistance) y = origin.y + maxDistance - target.getBbHeight();
        if (target.getBoundingBox().minY < origin.y - maxDistance) y = origin.y - maxDistance;

        if (target.getBoundingBox().maxZ > origin.z + maxDistance) z = origin.z + maxDistance - target.getBbWidth() / 2;
        if (target.getBoundingBox().minZ < origin.z - maxDistance) z = origin.z - maxDistance + target.getBbWidth() / 2;

        if (x != target.xo || y != target.yo || z != target.zo) {
            target.addDeltaMovement(new Vec3(0.15 * Math.signum(x - target.xo), 0.15 * Math.signum(y - target.yo), 0.15 * Math.signum(z - target.zo)));
            EntityUtil.undoGravity(target);
            if (target.level().isClientSide) {
                target.level().playSound(null, target.blockPosition(), EBSounds.ENTITY_FORCEFIELD_DEFLECT.get(), SoundSource.HOSTILE, 0.3f, 1f);
            }
        }

        // need to check the -1, being the infinite duration case
        if (target.hasEffect(this) && target.getEffect(this).getDuration() <= 1 && target.getEffect(this).getDuration() != -1) {
            data.setContainmentPos(null);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {

    }
}
