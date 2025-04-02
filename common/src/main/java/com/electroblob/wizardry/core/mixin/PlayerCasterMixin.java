package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.spell.internal.Caster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Player.class)
public abstract class PlayerCasterMixin implements Caster {

    @Override
    public Level getCastLevel() {
        return ((Player)((Object)this)).level();
    }

    @Override
    public Vec3 getCastPosition() {
        return ((Player)((Object)this)).position();
    }
}
