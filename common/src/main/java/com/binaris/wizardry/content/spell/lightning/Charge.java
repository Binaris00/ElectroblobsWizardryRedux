package com.binaris.wizardry.content.spell.lightning;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.ISpellVar;
import com.binaris.wizardry.api.content.data.VarPersistence;
import com.binaris.wizardry.api.content.data.SpellManagerData;
import com.binaris.wizardry.api.content.data.SpellVar;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Charge extends Spell {
    public static final ISpellVar<SpellModifiers> CHARGE_MODIFIERS = new SpellVar<>(VarPersistence.NEVER);
    public static final SpellProperty<Float> CHARGE_SPEED = SpellProperty.floatProperty("charge_speed");
    public static final ISpellVar<Integer> CHARGE_TIME = new SpellVar<Integer>(VarPersistence.NEVER).withTicker(Charge::update);

    public Charge() {
        this.soundValues(0.6f, 1, 0);
    }

    private static int update(Player player, Integer chargeTime) {
        if (chargeTime == null) chargeTime = 0;

        if (chargeTime > 0 && !player.level().isClientSide) {
            SpellModifiers modifiers = Services.OBJECT_DATA.getSpellManagerData(player).getVariable(CHARGE_MODIFIERS);
            if (modifiers == null) modifiers = new SpellModifiers();

            Vec3 look = player.getLookAngle();

            float speed = modifiers.get(SpellModifiers.RANGE, Spells.CHARGE.property(Charge.CHARGE_SPEED));

            player.setDeltaMovement(look.x * speed, player.getDeltaMovement().y, look.z * speed);

            if (player.level().isClientSide) {
                for (int i = 0; i < 5; i++) {
                    ParticleBuilder.create(EBParticles.SPARK, player).spawn(player.level());
                }
            }

            List<LivingEntity> collided = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(1));

            collided.remove(player);
            float damage = modifiers.get(SpellModifiers.POTENCY, Spells.CHARGE.property(DefaultProperties.DAMAGE));
            float knockback = Spells.CHARGE.property(DefaultProperties.KNOCKBACK);

            collided.forEach(e -> e.hurt(MagicDamageSource.causeDirectMagicDamage(player, EBDamageSources.SHOCK), damage));
            collided.forEach(e -> e.push(player.getDeltaMovement().x * knockback, player.getDeltaMovement().y * knockback + 0.3f, player.getDeltaMovement().z * knockback));

            if (player.level().isClientSide)
                player.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, player.getX() + player.getDeltaMovement().x, player.getY() + player.getBbHeight() / 2, player.getZ() + player.getDeltaMovement().z, 0, 0, 0);

            if (collided.isEmpty()) chargeTime--;
            else {
                EntityUtil.playSoundAtPlayer(player, SoundEvents.GENERIC_HURT, 1, 1);
                chargeTime = 0;
            }
            player.hurtMarked = true;
        }
        return chargeTime;
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.isCanceled()) return;

        if (event.getDamagedEntity() instanceof Player player && event.getSource().getEntity() instanceof LivingEntity attacker) {
            SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
            Integer chargeTime = data.getVariable(CHARGE_TIME);

            if (chargeTime != null && chargeTime > 0 && player.getBoundingBox().inflate(1).intersects(attacker.getBoundingBox())) {
                event.setCanceled(true);
            }

        }
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.caster());
        data.setVariable(CHARGE_TIME, (int) (ctx.modifiers().get(SpellModifiers.DURATION, property(DefaultProperties.DURATION).floatValue())));
        data.setVariable(CHARGE_MODIFIERS, ctx.modifiers());

        if (ctx.world().isClientSide)
            ctx.world().addParticle(ParticleTypes.EXPLOSION_EMITTER, ctx.caster().getX(), ctx.caster().getY() + ctx.caster().getBbHeight() / 2, ctx.caster().getZ(), 0, 0, 0);

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT, 20, 0, 50)
                .add(CHARGE_SPEED, 2.0F)
                .add(DefaultProperties.DURATION, 10)
                .add(DefaultProperties.DAMAGE, 8F)
                .add(DefaultProperties.KNOCKBACK, 1.0F)
                .build();
    }
}
