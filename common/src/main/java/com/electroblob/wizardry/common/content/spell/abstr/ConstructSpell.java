package com.electroblob.wizardry.common.content.spell.abstr;

import com.electroblob.wizardry.api.common.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ConstructSpell<T extends MagicConstructEntity> extends Spell {
    protected final Function<Level, T> constructFactory;
    protected final boolean permanent;
    protected boolean requiresFloor = false;
    protected boolean allowOverlap = false;

    public ConstructSpell(Function<Level, T> constructFactory, boolean permanent){
        this.constructFactory = constructFactory;
        this.permanent = permanent;
    }

    public ConstructSpell<T> floor(boolean requiresFloor) {
        this.requiresFloor = requiresFloor;
        return this;
    }

    public ConstructSpell<T> overlap(boolean allowOverlap) {
        this.allowOverlap = allowOverlap;
        return this;
    }

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        if (player.onGround() || !requiresFloor) {
            spawnConstruct(player.level(), player.getX(), player.getY(), player.getZ(), player.onGround() ? Direction.UP : null, player);
            // TODO spell sound ticks in use
            this.playSound(caster.getCastLevel(), player, 0, -1);
        }
    }

    protected boolean spawnConstruct(Level world, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster) {
        if (!world.isClientSide) {
            T construct = constructFactory.apply(world);

            construct.setPos(x, y, z);
            construct.setCaster((Player) caster);
            // TODO ADD SPELL OPTION FOR LIFETIME
            construct.lifetime = permanent ? -1 : 60;
            construct.damageMultiplier = 1;
            if (construct instanceof ScaledConstructEntity)
                ((ScaledConstructEntity) construct).setSizeMultiplier(1);
            addConstructExtras(construct, side, caster);

            if (!allowOverlap && !world.getEntitiesOfClass(construct.getClass(), construct.getBoundingBox()).isEmpty())
                return false;

            world.addFreshEntity(construct);
        }

        return true;
    }

    protected void addConstructExtras(T construct, Direction side, @Nullable LivingEntity caster) {
    }


    @Override
    protected SpellProperties properties() {
        return null;
    }
}
