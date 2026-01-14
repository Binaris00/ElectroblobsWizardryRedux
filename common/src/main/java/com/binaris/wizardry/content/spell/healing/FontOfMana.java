package com.binaris.wizardry.content.spell.healing;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.AreaEffectSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FontOfMana extends AreaEffectSpell {

    public FontOfMana() {
        super();
        this.targetAllies(true);
        this.alwaysSucceed(true);
        this.particleDensity(1.25f);
    }

    // Event handler to reduce cooldowns when caster has the buff
    public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
        if (event.getCaster() != null && event.getCaster().hasEffect(EBMobEffects.FONT_OF_MANA.get())) {
            MobEffectInstance inst = event.getCaster().getEffect(EBMobEffects.FONT_OF_MANA.get());
            if (inst != null) {
                float current = event.getModifiers().get(EBItems.COOLDOWN_UPGRADE.get());
                int amp = inst.getAmplifier();
                event.getModifiers().set(EBItems.COOLDOWN_UPGRADE.get(), current / (2 + amp), false);
            }
        }
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if (!(target instanceof Player)) return true;

        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get()));
        int strength = (int) (property(DefaultProperties.EFFECT_STRENGTH) + (ctx.modifiers().get(SpellModifiers.POTENCY) - 1f) * 2f);

        // Apply the new Font of Mana mob effect
        if (EBMobEffects.FONT_OF_MANA.get() != null) {
            if (!ctx.world().isClientSide)
                target.addEffect(new MobEffectInstance(EBMobEffects.FONT_OF_MANA.get(), duration, strength));
        }
        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z) {
        float hue = world.random.nextFloat() * 0.4f;
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.03, 0).time(50).color(1f, 1f - hue, 0.6f + hue).spawn(world);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.HEALING, SpellType.UTILITY, SpellAction.POINT_UP, 100, 15, 250)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.EFFECT_DURATION, 600)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }
}
