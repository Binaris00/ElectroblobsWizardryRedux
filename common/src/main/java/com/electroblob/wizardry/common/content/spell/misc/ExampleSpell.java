package com.electroblob.wizardry.common.content.spell.misc;

import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.common.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.spell.SpellProperty;
import com.electroblob.wizardry.common.content.entity.projectile.Dart;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.networking.EBNetwork;
import com.electroblob.wizardry.common.networking.s2c.TestPacket;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ExampleSpell extends Spell {

    public static final SpellProperty<Integer> AMOUNT = SpellProperty.intProperty("cow_amount");

    @Override
    protected void perform(Caster caster) {
        float damage = property(DefaultProperties.DAMAGE);
        int amount = property(AMOUNT);

        //Dart arrow = new Dart(EBEntities.DART.get(), caster.getCastLevel());
        Player player = (Player) caster;

        //arrow.aim(player, ArrowSpell.calculateVelocity(arrow, player.getEyeHeight()) - (float) MagicArrowEntity.LAUNCH_Y_OFFSET);
        //EBNetwork.sendToPlayer(TestPacket.create("Casted!"), player);
        //player.sendSystemMessage(Component.literal(String.valueOf(this.is(Spells.EXAMPLE))));

        //caster.getCastLevel().addFreshEntity(arrow);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.DAMAGE, 3.0f)
                .add(AMOUNT, 2)
                .build();
    }

    @Override
    public int performDuration() {
        return 1;
    }


}
