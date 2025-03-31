package com.electroblob.wizardry.api.common.spell.internal;

import com.electroblob.wizardry.api.common.spell.Spell;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface Caster {

    static Caster of(Player player) {
        return (Caster) player;
    }

    Level getCastLevel();
    Vec3 getCastPosition();

    default void castSpell(Spell spell) {
        spell.clone().cast(this); // Need to clone spell so it doesn't save to registry supplier's held spell value
    }
}
