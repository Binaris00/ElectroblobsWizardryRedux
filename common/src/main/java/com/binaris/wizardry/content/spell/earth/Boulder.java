package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.VecUtils;
import com.binaris.wizardry.content.entity.construct.BoulderConstruct;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ConstructSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Boulder extends ConstructSpell<BoulderConstruct> {
    public static final SpellProperty<Integer> KNOCKBACK_STRENGTH = SpellProperty.intProperty("knockback_strength", 1);
    public static final SpellProperty<Float> SPEED = SpellProperty.floatProperty("speed", 0.44F);

    public Boulder() {
        super(BoulderConstruct::new, false);
    }

    @Override
    protected void addConstructExtras(CastContext ctx, BoulderConstruct construct, Direction side) {
        float speed = property(SPEED);
        Vec3 direction = ctx.caster() == null ? new Vec3(side.step()) : VecUtils.flattenToHorizontal(ctx.caster().getLookAngle());
        construct.setHorizontalVelocity(direction.x * speed, direction.z * speed);
        construct.setYRot(ctx.caster() == null ? side.toYRot() : ctx.caster().getYRot());
        double yOffset = ctx.caster() == null ? 0 : 1.6;
        construct.setPos(construct.getX() + direction.x, construct.getY() + yOffset, construct.getZ() + direction.z);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellType.ATTACK, SpellAction.SUMMON, 125, 25, 350)
                .add(DefaultProperties.DURATION, 200)
                .add(DefaultProperties.DAMAGE, 10F)
                .add(SPEED)
                .add(KNOCKBACK_STRENGTH)
                .build();
    }
}
