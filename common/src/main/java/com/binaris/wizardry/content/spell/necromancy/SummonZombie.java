package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonZombie extends MinionSpell<Zombie> {
    public SummonZombie() {
        super(Zombie::new);
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected Zombie createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        if (caster instanceof Player player && EBAccessoriesIntegration.isEquipped(player, EBItems.CHARM_MINION_VARIANTS.get())) {
            return new Husk(EntityType.HUSK, world);
        } else {
            return super.createMinion(world, caster, modifiers);
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.NECROMANCY, SpellType.MINION, SpellAction.SUMMON, 10, 0, 40)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.MINION_COUNT, 1)
                .add(DefaultProperties.SUMMON_RADIUS, 2)
                .build();
    }
}
