package com.electroblob.wizardry.common.content.spell.abstr;

import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.spell.Spell;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ArrowSpell<T extends MagicArrowEntity> extends Spell {
    private static final float FALLBACK_VELOCITY = 2;

    protected final Function<Level, T> arrowFactory;

    public ArrowSpell(Function<Level, T> arrowFactory) {
        this.arrowFactory = arrowFactory;
    }

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player){
            T arrow = arrowFactory.apply(caster.getCastLevel());

            arrow.aim(player, calculateVelocity(arrow, player.getEyeHeight()) - (float) MagicArrowEntity.LAUNCH_Y_OFFSET);
            addArrowExtras(arrow, caster);

            caster.getCastLevel().addFreshEntity(arrow);
        }

        // Sound ?
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }

    public static float calculateVelocity(MagicArrowEntity projectile, float launchHeight){
        float range = 10; // this.properties().get(RANGE);

        if(projectile.isNoGravity()){
            if(projectile.getLifetime() <= 0) return FALLBACK_VELOCITY;
            return range / projectile.getLifetime();
        }else{
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight/g);
        }
    }

    /**
     * Called just before the arrow is spawned. Does nothing by default, but subclasses can override to call extra
     * methods on the spawned arrow. This method is only called server-side so cannot be used to spawn particles directly.
     * @param arrow The entity being spawned.
     * @param caster The caster of this spell, or null if it was cast by a dispenser.
     */
    protected void addArrowExtras(T arrow, @Nullable Caster caster){
        // Subclasses can put spell-specific stuff here
    }

}
