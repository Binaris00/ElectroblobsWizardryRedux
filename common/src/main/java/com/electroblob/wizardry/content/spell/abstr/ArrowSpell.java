package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.content.spell.DefaultProperties;
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

            // TODO, spell sound ticks in use, not really used, but anyway
            this.playSound(caster.getCastLevel(), player, 0, -1);
        }
    }

    public float calculateVelocity(MagicArrowEntity projectile, float launchHeight){
        float range = this.property(DefaultProperties.RANGE);

        if(projectile.isNoGravity()){
            if(projectile.getLifetime() <= 0) return FALLBACK_VELOCITY;
            return range / projectile.getLifetime();
        }else{
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight/g);
        }
    }

    protected void addArrowExtras(T arrow, @Nullable Caster caster){
    }


    @Override
    protected SpellProperties properties() {
        return SpellProperties.empty();
    }
}
