package com.electroblob.wizardry.common.content.spell.abstr;

import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ProjectileSpell<T extends MagicProjectileEntity> extends Spell {
    private static final float DISPENSER_INACCURACY = 1;
    private static final float FALLBACK_VELOCITY = 1.5f;

    protected final Function<Level, T> projectileFactory;

    public ProjectileSpell(Function<Level, T> projectileFactory) {
        this.projectileFactory = projectileFactory;
    }

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player){
            T projectile = projectileFactory.apply(caster.getCastLevel());

            projectile.aim(player, calculateVelocity(projectile, player.getEyeHeight()) - (float) MagicArrowEntity.LAUNCH_Y_OFFSET);
            addProjectileExtras(projectile, caster);

            caster.getCastLevel().addFreshEntity(projectile);
        }
        // Sound ?
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }


    protected float calculateVelocity(MagicProjectileEntity projectile, float launchHeight){
        float range = property(DefaultProperties.RANGE);

        if(projectile.isNoGravity()){
            if(projectile.getLifeTime() <= 0) return FALLBACK_VELOCITY;
            return range / projectile.getLifeTime();
        }else{
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight/g);
        }
    }

    /**
     * This method is called just before the projectile is spawned.
     * It is intended to be overridden by subclasses to add extra methods or behaviors to the projectile.
     * By default, it does nothing.
     * Note that this method is only called server-side, so it cannot be used to spawn particles directly.
     * @param projectile The entity being spawned.
     * @param caster The caster of this spell, or null if it was cast by a dispenser.
     */
    protected void addProjectileExtras(T projectile, @Nullable Caster caster){
        // Subclasses can put spell-specific stuff here
    }
}
