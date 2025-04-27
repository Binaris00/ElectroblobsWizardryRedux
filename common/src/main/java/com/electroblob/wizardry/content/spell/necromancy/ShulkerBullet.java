package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class ShulkerBullet extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (shoot(ctx.world(), ctx.caster(), ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ())) return false;
        this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (shoot(ctx.world(), ctx.caster(), ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ())) return false;
        this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return true;
    }

    private boolean shoot(Level world, LivingEntity caster, double x, double y, double z) {
        if (!world.isClientSide) {
            double range = property(DefaultProperties.RANGE);

            List<LivingEntity> possibleTargets = EntityUtil.getLivingWithinRadius(range, x, y, z, world);

            possibleTargets.remove(caster);
            possibleTargets.removeIf(t -> t instanceof ArmorStand);

            if (possibleTargets.isEmpty()) return true;

            possibleTargets.sort(Comparator.comparingDouble(t -> t.distanceToSqr(x, y, z)));

            Entity target = possibleTargets.get(0);
            world.addFreshEntity(new net.minecraft.world.entity.projectile.ShulkerBullet(world, caster, target, Direction.UP.getAxis()));
        }

        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(Tiers.ADVANCED, Elements.NECROMANCY, SpellType.PROJECTILE, SpellAction.POINT_DOWN, 25, 0, 40)
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }
}
