package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.construct.TornadoConstruct;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ConstructSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Tornado extends ConstructSpell<TornadoConstruct> {
    public Tornado() {
        super(TornadoConstruct::new, false);
        floor(true);
    }

    @Override
    protected void addConstructExtras(TornadoConstruct construct, Direction side, @Nullable LivingEntity caster) {
        float speed = property(DefaultProperties.SPEED);
        Vec3 direction = caster == null ? new Vec3(side.getStepX(), side.getStepY(), side.getStepZ()) : caster.getLookAngle();
        construct.setHorizontalVelocity((float) (direction.x * speed), (float) (direction.z * speed));
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT, 35, 10, 80)
                .add(DefaultProperties.DURATION, 200)
                .add(DefaultProperties.SPEED, 0.33F)
                .add(DefaultProperties.EFFECT_RADIUS, 4)
                .add(DefaultProperties.DAMAGE, 1F)
                .add(DefaultProperties.ACCELERATION, 0.22F)
                .build();
    }
}
