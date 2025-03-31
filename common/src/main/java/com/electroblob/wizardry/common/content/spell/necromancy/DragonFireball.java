package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DragonFireball extends Spell {
    public static final SpellProperty<Float> ACCELERATION = SpellProperty.floatProperty("acceleration", 0.1F);

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        Vec3 look = player.getLookAngle();

        if (!player.level().isClientSide) {
            net.minecraft.world.entity.projectile.DragonFireball fireball =
                    new net.minecraft.world.entity.projectile.DragonFireball(player.level(), player, 1, 1, 1);

            fireball.setPos(player.getX() + look.x, player.getY() + look.y + 1.3, player.getZ() + look.z);

            double acceleration = property(ACCELERATION);

            fireball.xPower = look.x * acceleration;
            fireball.yPower = look.y * acceleration;
            fireball.zPower = look.z * acceleration;

            player.level().addFreshEntity(fireball);
        }

        this.playSound(player.level(), player, 0, -1);
        player.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(ACCELERATION).build();
    }
}
