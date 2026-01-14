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
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WitherSkullSpell extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Vec3 look = ctx.caster().getLookAngle();
        WitherSkull witherSkull = new WitherSkull(ctx.world(), ctx.caster(), 1, 1, 1);
        witherSkull.setPos(ctx.caster().getX() + look.x, ctx.caster().getY() + look.y + 1.3, ctx.caster().getZ() + look.z);
        double acceleration = property(DefaultProperties.SPEED) * ctx.modifiers().get(EBItems.RANGE_UPGRADE.get());

        witherSkull.xPower = look.x * acceleration;
        witherSkull.yPower = look.y * acceleration;
        witherSkull.zPower = look.z * acceleration;

        witherSkull.setOwner(ctx.caster());
        ctx.world().addFreshEntity(witherSkull);

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        Vec3 look = ctx.caster().getLookAngle();
        WitherSkull witherSkull = new WitherSkull(ctx.world(), ctx.caster(), 1, 1, 1);
        witherSkull.setPos(ctx.caster().getX() + look.x, ctx.caster().getY() + look.y + 1.3, ctx.caster().getZ() + look.z);
        double acceleration = property(DefaultProperties.SPEED);

        witherSkull.xPower = look.x * acceleration;
        witherSkull.yPower = look.y * acceleration;
        witherSkull.zPower = look.z * acceleration;

        witherSkull.setOwner(ctx.caster());
        ctx.world().addFreshEntity(witherSkull);

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.POINT, 20, 5, 30)
                .add(DefaultProperties.SPEED, 0.1F)
                .build();
    }
}
