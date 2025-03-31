package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;

public class WitherSkullSpell extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        Vec3 look = player.getLookAngle();

        WitherSkull witherSkull = new WitherSkull(player.level(), player, 1, 1, 1);

        witherSkull.setPos(player.getX() + look.x, player.getY() + look.y + 1.3, player.getZ() + look.z);

        double acceleration = property(DefaultProperties.SPEED);

        witherSkull.xPower = look.x * acceleration;
        witherSkull.yPower = look.y * acceleration;
        witherSkull.zPower = look.z * acceleration;

        witherSkull.setOwner(player);

        //player.level().playSound(player, player.getOnPos(), EBSounds.SPELL_WITHER_SKULL.get(), SoundSource.PLAYERS);
        player.level().addFreshEntity(witherSkull);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.SPEED, 0.1F)
                .build();
    }
}
