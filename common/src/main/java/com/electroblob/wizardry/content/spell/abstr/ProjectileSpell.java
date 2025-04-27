package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ProjectileSpell<T extends MagicProjectileEntity> extends Spell {
    private static final float FALLBACK_VELOCITY = 1.5f;
    protected final Function<Level, T> projectileFactory;

    public ProjectileSpell(Function<Level, T> projectileFactory) {
        this.projectileFactory = projectileFactory;
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean canCastByLocation() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.world().isClientSide) {
            T projectile = projectileFactory.apply(ctx.world());
            projectile.aim(ctx.caster(), calculateVelocity(projectile, ctx.caster().getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET));
            projectile.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            // TODO MODIFIERS
//            if (projectile instanceof BombEntity bomb)
//                bomb.blastMultiplier = ctx.modifiers().get(WizardryItems.BLAST_UPGRADE.get());
            addProjectileExtras(projectile, ctx.caster());
            ctx.world().addFreshEntity(projectile);
        }

        ctx.caster().swing(ctx.hand());

        this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);

        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if(ctx.target() == null) return false;

        if (!ctx.world().isClientSide) {
            T projectile = projectileFactory.apply(ctx.world());
            int aimingError = EntityUtil.getDefaultAimingError(ctx.world().getDifficulty());
            projectile.aim(ctx.caster(), ctx.target(), calculateVelocity(projectile, ctx.caster().getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET), aimingError);
            projectile.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            // TODO MODIFIERS
//            if (projectile instanceof EntityBomb)
//                ((EntityBomb) projectile).blastMultiplier = modifiers.get(WizardryItems.BLAST_UPGRADE.get());
            addProjectileExtras(projectile, ctx.caster());
            ctx.world().addFreshEntity(projectile);
        }

        ctx.caster().swing(ctx.hand());

        this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        if (!ctx.world().isClientSide) {
            T projectile = projectileFactory.apply(ctx.world());
            projectile.setPos(ctx.vec3());
            Vec3i vec = ctx.direction().getNormal();
            projectile.shoot(vec.getX(), vec.getY(), vec.getZ(), calculateVelocity(projectile, 0.375f), 1);
            projectile.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            // TODO MODIFIERS
//            if (projectile instanceof EntityBomb)
//                ((EntityBomb) projectile).blastMultiplier = modifiers.get(WizardryItems.BLAST_UPGRADE.get());
            addProjectileExtras(projectile, null);
            ctx.world().addFreshEntity(projectile);
        }

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.ticksInUse(), ctx.duration());
        return true;
    }

    protected float calculateVelocity(MagicProjectileEntity projectile, float launchHeight){
        float range = property(DefaultProperties.RANGE);

        if(projectile.isNoGravity()){
            if(projectile.getLifeTime() <= 0) return FALLBACK_VELOCITY;
            return range / projectile.getLifeTime();
        }else{
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight/g);
        }
    }

    protected void addProjectileExtras(T projectile, @Nullable LivingEntity caster){
        // Subclasses can put spell-specific stuff here
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
