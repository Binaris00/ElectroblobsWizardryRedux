package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
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

        double range = property(DefaultProperties.RANGE);

        List<LivingEntity> possibleTargets = EntityUtil.getLivingWithinRadius(range, player.getX(), player.getY(), player.getZ(),
                player.level());

        possibleTargets.remove(caster);
        possibleTargets.removeIf(entity -> entity instanceof ArmorStand);

        if(possibleTargets.isEmpty()) return;

        possibleTargets.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(player.getX(), player.getY(), player.getZ())));
        Entity target = possibleTargets.get(0);

        player.level().addFreshEntity(new net.minecraft.world.entity.projectile.ShulkerBullet(player.level(), player, target, Direction.Axis.Y));
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }
}
