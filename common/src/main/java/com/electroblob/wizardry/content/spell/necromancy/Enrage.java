package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Enrage extends AreaEffectSpell {
    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if (ctx.caster() instanceof Player player && target instanceof PathfinderMob) {
            target.setLastHurtByMob(player);
        }

        return true;
    }

    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        if (ctx.caster() != null) origin = ctx.caster().getEyePosition(1);

        for (int i = 0; i < 30; i++) {
            double x = origin.x - 1 + ctx.world().random.nextDouble() * 2;
            double y = origin.y - 0.25 + ctx.world().random.nextDouble() * 0.5;
            double z = origin.z - 1 + ctx.world().random.nextDouble() * 2;
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.9f, 0.1f, 0).spawn(ctx.world());
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.SUMMON, 20, 0, 100)
                .add(DefaultProperties.EFFECT_RADIUS, 8).build();
    }
}
