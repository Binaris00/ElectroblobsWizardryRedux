package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class CombustionRuneConstruct extends ScaledConstructEntity {
    public CombustionRuneConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    public CombustionRuneConstruct(Level world) {
        super(EBEntities.COMBUSTION_RUNE.get(), world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, getX(), getY(), getZ(), level());
            for (LivingEntity target : targets) {
                if (this.isValidTarget(target)) {
                    float strength = Spells.COMBUSTION_RUNE.property(DefaultProperties.BLAST_RADIUS) * sizeMultiplier;
                    level().explode(this.getCaster(), this.getX(), this.getY(), this.getZ(), strength, true, EntityUtil.canDamageBlocks(getCaster(), level()) ?
                            Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
                    this.discard();
                }
            }
        } else if (this.random.nextInt(15) == 0) {
            double radius = 0.5 + random.nextDouble() * 0.3;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            level().addParticle(ParticleTypes.FLAME, this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle), 0, 0, 0);
        }
    }

    @Override
    protected boolean shouldScaleWidth() {
        return false;
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
