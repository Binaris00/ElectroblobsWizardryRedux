package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

public class ShulkerBullet extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        if(player.level().isClientSide) return;

        double range = 10;

        List<LivingEntity> possibleTargets = EntityUtil.getLivingWithinRadius(range, player.getX(), player.getY(), player.getZ(),
                player.level());

        possibleTargets.remove(caster);
        possibleTargets.removeIf(t -> t instanceof ArmorStand);

        if(possibleTargets.isEmpty()) return;

        possibleTargets.sort(Comparator.comparingDouble(t -> t.distanceToSqr(player.getX(), player.getY(), player.getZ())));
        Entity target = possibleTargets.get(0);

        player.level().addFreshEntity(new net.minecraft.world.entity.projectile.ShulkerBullet(player.level(), player, target, Direction.Axis.Y));
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
