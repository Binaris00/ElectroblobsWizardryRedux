package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HealAlly extends RaySpell {
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target) {
            if (target.getHealth() < target.getMaxHealth() && target.getHealth() > 0) {
                target.heal(property(DefaultProperties.HEALTH));
                if (ctx.world().isClientSide) ParticleBuilder.spawnHealParticles(ctx.world(), target);
                playSound(ctx.world(), target, 0, -1);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(Tiers.APPRENTICE, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT, 10, 0, 20)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.HEALTH, 5F)
                .build();
    }
}
