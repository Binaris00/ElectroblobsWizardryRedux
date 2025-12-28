package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.entity.construct.BoulderConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Boulder extends ConstructSpell<BoulderConstruct> {
    public static final SpellProperty<Integer> KNOCKBACK_STRENGTH = SpellProperty.intProperty("knockback_strength", 1);
    public static final SpellProperty<Float> SPEED = SpellProperty.floatProperty("speed", 0.44F);

    public Boulder() {
        super(BoulderConstruct::new, false);
    }

    @Override
    protected void addConstructExtras(BoulderConstruct construct, Direction side, @Nullable LivingEntity caster) {
        float speed = property(SPEED);
        Vec3 direction = caster == null ? new Vec3(side.step()) : GeometryUtil.horizontalise(caster.getLookAngle());
        construct.setHorizontalVelocity(direction.x * speed, direction.z * speed);
        construct.setYRot(caster == null ? side.toYRot() : caster.getYRot());
        double yOffset = caster == null ? 0 : 1.6;
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
