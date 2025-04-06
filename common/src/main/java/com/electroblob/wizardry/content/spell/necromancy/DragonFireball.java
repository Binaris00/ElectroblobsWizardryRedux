package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class DragonFireball extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Vec3 look = ctx.caster().getLookAngle();

        if (!ctx.world().isClientSide) {
            net.minecraft.world.entity.projectile.DragonFireball fireball =
                    new net.minecraft.world.entity.projectile.DragonFireball(ctx.world(), ctx.caster(), 1, 1, 1);

            fireball.setPos(ctx.caster().getX() + look.x, ctx.caster().getY() + look.y + 1.3, ctx.caster().getZ() + look.z);

            double acceleration = property(DefaultProperties.ACCELERATION);

            fireball.xPower = look.x * acceleration;
            fireball.yPower = look.y * acceleration;
            fireball.zPower = look.z * acceleration;

            ctx.world().addFreshEntity(fireball);
        }

        this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        ctx.caster().swing(InteractionHand.MAIN_HAND);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        Vec3 look = ctx.caster().getLookAngle();

        if (!ctx.world().isClientSide) {
            net.minecraft.world.entity.projectile.DragonFireball fireball =
                    new net.minecraft.world.entity.projectile.DragonFireball(ctx.world(), ctx.caster(), 1, 1, 1);

            fireball.setPos(ctx.caster().getX() + look.x, ctx.caster().getY() + look.y + 1.3, ctx.caster().getZ() + look.z);

            double acceleration = property(DefaultProperties.ACCELERATION);

            fireball.xPower = look.x * acceleration;
            fireball.yPower = look.y * acceleration;
            fireball.zPower = look.z * acceleration;

            ctx.world().addFreshEntity(fireball);
        }

        this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        ctx.caster().swing(InteractionHand.MAIN_HAND);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.ACCELERATION, 0.1F).build();
    }
}
