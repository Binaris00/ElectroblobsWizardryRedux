package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Bubble extends RaySpell {

    public Bubble(){
        this.soundValues(0.5f, 1.1f, 0.2f);
    }


    // This will always return true
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if(!(entityHit.getEntity() instanceof LivingEntity target)) return true;
        if(ctx.world().isClientSide) return true;

        BubbleConstruct bubble = new BubbleConstruct(ctx.world());
        bubble.setPos(target.getX(), target.getY(), target.getZ());
        if(ctx.caster() != null) bubble.setCaster(ctx.caster());
        bubble.lifetime = 200;
        bubble.isDarkOrb = false;
        bubble.damageMultiplier = 1;
        ctx.world().addFreshEntity(bubble);
        target.startRiding(bubble);

        EBMagicDamageSource.causeMagicDamage(bubble, target, 1, EBDamageSources.SORCERY, false);
        return true;
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
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.SPLASH, x, y, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.MAGIC_BUBBLE).pos(x, y, z).spawn(ctx.world());
    }

    // TODO PROPERTIES
    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(Tiers.APPRENTICE, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT, 15, 0, 20)
                .build();
    }
}
