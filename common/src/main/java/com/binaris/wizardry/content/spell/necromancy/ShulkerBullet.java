package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
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
        return shoot(ctx.world(), ctx.caster(), ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.castingTicks(), ctx.modifiers());
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        return shoot(ctx.world(), ctx.caster(), ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.castingTicks(), ctx.modifiers());
    }

    /**
     * Shoots a shulker bullet from the caster towards the nearest valid target within range.
     *
     * @param world     The level in which the spell is cast.
     * @param caster    The entity casting the spell.
     * @param x         The x-coordinate of the caster's position.
     * @param y         The y-coordinate of the caster's position.
     * @param z         The z-coordinate of the caster's position.
     * @param modifiers The spell modifiers affecting the spell's properties.
     * @return true if the spell was successfully cast, false otherwise.
     */
    private boolean shoot(Level world, LivingEntity caster, double x, double y, double z, int castingTicks, SpellModifiers modifiers) {
        if (!world.isClientSide) {
            double range = property(DefaultProperties.RANGE) * modifiers.get(EBItems.RANGE_UPGRADE.get());

            List<LivingEntity> possibleTargets = EntityUtil.getLivingWithinRadius(range, x, y, z, world);

            possibleTargets.remove(caster);
            possibleTargets.removeIf(t -> t instanceof ArmorStand);
            if (possibleTargets.isEmpty()) {
                return false;
            }

            possibleTargets.sort(Comparator.comparingDouble(t -> t.distanceToSqr(x, y, z)));

            Entity target = possibleTargets.get(0);
            world.addFreshEntity(new net.minecraft.world.entity.projectile.ShulkerBullet(world, caster, target, Direction.UP.getAxis()));
            this.playSound(world, caster, castingTicks, -1);
            return true;
        }

        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.PROJECTILE, SpellAction.POINT_DOWN, 25, 0, 40)
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }
}
