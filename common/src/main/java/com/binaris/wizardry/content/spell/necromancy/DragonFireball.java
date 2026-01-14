package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DragonFireball extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Vec3 look = ctx.caster().getLookAngle();

        if (!ctx.world().isClientSide) {
            net.minecraft.world.entity.projectile.DragonFireball fireball =
                    new net.minecraft.world.entity.projectile.DragonFireball(ctx.world(), ctx.caster(), 1, 1, 1);

            fireball.setPos(ctx.caster().getX() + look.x, ctx.caster().getY() + look.y + 1.3, ctx.caster().getZ() + look.z);

            double acceleration = property(DefaultProperties.ACCELERATION) * ctx.modifiers().get(EBItems.RANGE_UPGRADE.get());

            fireball.xPower = look.x * acceleration;
            fireball.yPower = look.y * acceleration;
            fireball.zPower = look.z * acceleration;

            ctx.world().addFreshEntity(fireball);
        }

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
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

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        ctx.caster().swing(InteractionHand.MAIN_HAND);
        return true;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.NONE, 30, 0, 40)
                .add(DefaultProperties.ACCELERATION, 0.1F).build();
    }
}
